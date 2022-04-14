package vn.ngs.nspace.recruiting.service;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.Collections;

@Service
@Transactional
public class EmailSentService {
    private final EmailSentRepo repo;
    private final ExecuteNoticeService _noticeService;
    private final EmailSettingRepo _emailSettingRepo;
    private final EventFactory eventFactory;
    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    public EmailSentService(EmailSentRepo repo, EventFactory eventFactory,ExecuteNoticeService noticeService,EmailSettingRepo emailSettingRepo) {

        this.repo = repo;
        this.eventFactory = eventFactory;
        _noticeService =noticeService;
        _emailSettingRepo = emailSettingRepo;
    }

    /* create object */
    public EmailSent create(Long cid, String uid, EmailSent request) throws BusinessException {
        EmailSent obj = EmailSent.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCreateBy(uid);
        obj.setUpdateBy(uid);
        obj.setCompanyId(cid);
        obj = repo.save(obj);

        return obj;
    }

    /**
     * @createEmailSchedule
     * @param scheduleAction
     * excute set event schedule to kaka
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

    /**
     * sendMail
     * @param es
     * @return
     */
    public ResponseEntity sendMail(Long cid, Long emailSentId){
        try{
            EmailSent es = repo.findByCompanyIdAndId(cid,emailSentId).orElse(new EmailSent());
            Long emailSettingId = es.getEmailSettingId();
        EmailSetting setting = _emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class,emailSettingId));

        _noticeService.publishEmail(es.getUid(), es.getCompanyId(), MapUtils.getString(setting.getConfigs(), "email", "")
                    , MapUtils.getString(setting.getConfigs(), "password", "")
                    , es.getSubject()
                    , es.getContent(), Collections.singleton(es.getUid()), Collections.singleton(es.getToEmail()));
        EmailSent est = new EmailSent();
        est.setStatus(Constants.ENTITY_ACTIVE);
        est.setId(emailSentId);
        est = repo.save(est);
        return ResponseUtils.handlerSuccess(est);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
