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

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
}
