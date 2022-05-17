package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.service.EventFactory;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.EmailSentRequest;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmailSentV2Service {

    private final EmailSentRepo emailSentRepo;
    private final EventFactory eventFactory;
    private final EmailSettingRepo emailSettingRepo;
    private final ExecuteNoticeService noticeService;
    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    public EmailSentV2Service(EmailSentRepo emailSentRepo, EventFactory eventFactory, EmailSettingRepo emailSettingRepo, ExecuteNoticeService noticeService) {
        this.emailSentRepo = emailSentRepo;
        this.eventFactory = eventFactory;
        this.emailSettingRepo = emailSettingRepo;
        this.noticeService = noticeService;
    }

    /**
     * create email schedule
     *
     * @param scheduleAction
     */

    public void createEmailSchedule(ScheduleTaskCommand scheduleAction) {
        ScheduleRequest taskReq = new ScheduleRequest();
        taskReq.setCmd(vn.ngs.nspace.workflow.utils.Constants.CMD_CREATE);
        taskReq.setExecuteTime(scheduleAction.getExecuteTime());
        taskReq.setChannel(scheduleTopic);
        taskReq.setEvent(scheduleAction.getAction());
        taskReq.setId(scheduleAction.getTaskId() + "_" + scheduleAction.getEvent() + "_" + scheduleAction.getAction());
        taskReq.setPayload(scheduleAction);
        eventFactory.publishSchedule(taskReq);
    }

    public void SentEmail(Long cid, String uid, EmailSentRequest request) {
        EmailSent emailSent = new EmailSent();

        EmailSetting setting = emailSettingRepo.findByCompanyIdAndId(cid, request.getEmailSettingId()).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, request.getEmailSettingId()));

        if (request.getCandidateIds() != null && !request.getCandidateIds().isEmpty()) {
            String refIds = request.getCandidateIds().stream().map(Objects::toString).collect(Collectors.joining(","));
            emailSent.setRefType(Constants.EMAIL_SENT_REF.CANDIDATE.name());
            emailSent.setRefId(refIds);
        }

        String username = "";
        String password = "";
        String subject = request.getTitle() != null ? request.getTitle() :  "";
        String content = request.getContent() != null ? request.getContent() :  "";

        if (setting.getConfigs() != null) {
            username = MapUtils.getString(setting.getConfigs(), "email", "");
            password = MapUtils.getString(setting.getConfigs(), "password", "");
        }
        noticeService.publishEmail(uid, cid, username,
                password, subject,content, Set.of(uid), new HashSet<>(request.getMails()));

        emailSent.setToEmail(username);
        emailSent.setTemplateId(request.getTemplateId());
        emailSent.setSubject(subject);
        emailSent.setUid(uid);
        emailSent.setUpdateBy(uid);
        emailSent.setEmailSettingId(request.getEmailSettingId());

        emailSentRepo.save(emailSent);
    }

}
