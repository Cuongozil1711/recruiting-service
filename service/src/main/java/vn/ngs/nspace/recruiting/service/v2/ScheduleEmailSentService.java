package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.schedule.EventV2Factory;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.ScheduleEmailSentRequest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScheduleEmailSentService {

    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    private final EventV2Factory eventV2Factory;
    private final EmailSentRepo emailSentRepo;
    private final EmailSettingRepo emailSettingRepo;
    private final ExecuteNoticeService noticeService;


    public ScheduleEmailSentService(EventV2Factory eventV2Factory, EmailSentRepo emailSentRepo, EmailSettingRepo emailSettingRepo, ExecuteNoticeService noticeService) {
        this.eventV2Factory = eventV2Factory;
        this.emailSentRepo = emailSentRepo;
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
        eventV2Factory.publishSchedule(taskReq);
    }

    public void sentEmailAuto(Long cid, Long emailSentId) {
        EmailSent emailSent = emailSentRepo.findByCompanyIdAndId(cid, emailSentId).orElseThrow(() -> new EntityNotFoundException(EmailSent.class, emailSentId));

        Long emailSettingId = emailSent.getEmailSettingId();
        EmailSetting setting = emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, emailSettingId));

        List<String> emails = new ArrayList<>();
        String password = "";
        String username = "";
        if (setting.getConfigs() != null) {
            emails = Arrays.asList(setting.getConfigs().get("toEmail").toString().split(","));
            username = MapUtils.getString(setting.getConfigs(), "email", "");
            password = MapUtils.getString(setting.getConfigs(), "password", "");
        }

        for (String email : emails) {
            noticeService.publishEmail(email, cid, username,
                    password, emailSent.getSubject(), emailSent.getContent(), Set.of(email), Set.of(email));

        }
    }

    public void SentEmailSchedule(String uid, Long cid, ScheduleEmailSentRequest request) {
        EmailSent emailSentCandidate = new EmailSent();
        EmailSent emailSentInvolve = new EmailSent();
        EmailSetting setting = emailSettingRepo.findByCompanyIdAndId(cid, request.getEmailSettingId()).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, request.getEmailSettingId()));
        String username = "";
        String password = "";
        String subject = request.getTitle() != null ? request.getTitle() : "";
        String content = request.getContent() != null ? request.getContent() : "";

        if (setting.getConfigs() != null) {
            username = MapUtils.getString(setting.getConfigs(), "email", "");
            password = MapUtils.getString(setting.getConfigs(), "password", "");
        }

        String refIds = "";
        // nếu chọn gửi cho ứng viên
        if (request.isSentCandidate()) {
            if (request.getCandidateIds() != null && !request.getCandidateIds().isEmpty()) {
                refIds = request.getCandidateIds().stream().map(Objects::toString).collect(Collectors.joining(","));
                emailSentCandidate.setRefType(Constants.EMAIL_SENT_REF.CANDIDATE.name());
                emailSentCandidate.setRefId(refIds);
            }

            emailSentCandidate.setToEmail(String.join(",", request.getCandidateMails()));
            emailSentCandidate.setTemplateId(request.getTemplateId());
            emailSentCandidate.setSubject(subject);
            emailSentCandidate.setContent(request.getContent());
            emailSentCandidate.setDate(request.getScheduleDate() != null ? request.getScheduleDate() : new Date());
            emailSentCandidate.setCompanyId(cid);
            emailSentCandidate.setFromEmail(username);
            emailSentCandidate.setRefId(refIds);
            emailSentCandidate.setUid(uid);
            emailSentCandidate.setUpdateBy(uid);
            emailSentCandidate.setEmailSettingId(request.getEmailSettingId());

            emailSentRepo.save(emailSentCandidate);
        }

        //nếu chọn hội đồng
        if (request.isSentInvolve()) {
            if (request.getInterviewInvolveId() != null) {
                refIds = request.getInterviewInvolveId().toString();
                emailSentInvolve.setRefType(Constants.EMAIL_SENT_REF.INVOLVE.name());
                emailSentInvolve.setRefId(refIds);
            }

            emailSentInvolve.setToEmail(String.join(",", request.getInvolveMails()));
            emailSentInvolve.setTemplateId(request.getTemplateId());
            emailSentInvolve.setSubject(subject);
            emailSentInvolve.setContent(request.getContent());
            emailSentInvolve.setDate(request.getScheduleDate() != null ? request.getScheduleDate() : new Date());
            emailSentInvolve.setCompanyId(cid);
            emailSentInvolve.setFromEmail(username);
            emailSentInvolve.setRefId(refIds);
            emailSentInvolve.setUid(uid);
            emailSentInvolve.setUpdateBy(uid);
            emailSentInvolve.setEmailSettingId(request.getEmailSettingId());

            emailSentInvolve = emailSentRepo.save(emailSentInvolve);
        }

        // gộp danh sách email cần gửi
        Set<String> emailSent = new HashSet<>();
        emailSent.addAll(request.getCandidateMails());
        emailSent.addAll(request.getInvolveMails());

        // kiểm tra gửi ngay hay lên lịch

        if (request.getInterviewDate() != null) {
            ScheduleTaskCommand scheduleInvolve = new ScheduleTaskCommand();
            scheduleInvolve.setCompanyId(cid);
            scheduleInvolve.setEvent("schedule_mail");
            scheduleInvolve.setAction("schedule_mail");
            scheduleInvolve.setExecuteTime(request.getScheduleDate());
            scheduleInvolve.setTaskId(emailSentInvolve.getId());
            scheduleInvolve.setActionId(1791866326582272L);
            createEmailSchedule(scheduleInvolve);

            ScheduleTaskCommand scheduleCandidate = new ScheduleTaskCommand();
            scheduleCandidate.setCompanyId(cid);
            scheduleCandidate.setEvent("schedule_mail");
            scheduleCandidate.setAction("schedule_mail");
            scheduleCandidate.setExecuteTime(request.getScheduleDate());
            scheduleCandidate.setTaskId(emailSentCandidate.getId());
            scheduleCandidate.setActionId(1791866326582272L);
            createEmailSchedule(scheduleInvolve);
        } else {
            noticeService.publishEmail(uid, cid, username,
                    password, subject, content, Set.of(uid), emailSent);
        }
    }
}
