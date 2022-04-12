package vn.ngs.nspace.recruiting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
@Service
@Transactional
public class EmailSentService {
    private final EmailSentRepo repo;
    private final EventFactory eventFactory;
    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    public EmailSentService(EmailSentRepo repo, EventFactory eventFactory) {

        this.repo = repo;
        this.eventFactory = eventFactory;
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
}
