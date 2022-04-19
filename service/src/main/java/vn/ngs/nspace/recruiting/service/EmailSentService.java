package vn.ngs.nspace.recruiting.service;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class EmailSentService {
    private final EmailSentRepo repo;
    private final ExecuteNoticeService _noticeService;
    private final EmailSettingRepo _emailSettingRepo;
    private final EventFactory eventFactory;
    private final ExecuteConfigService _configService;
    private  final CandidateRepo _candidateRepo;
    private final ExecuteHcmService _hcmService;
    private final InterviewInvolveRepo _interviewInvolveRepo;
    private final OnboardOrderCheckListRepo _onboardOrderCheckListRepo;
    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    public EmailSentService(EmailSentRepo repo, EventFactory eventFactory,ExecuteNoticeService noticeService,EmailSettingRepo emailSettingRepo,
                            ExecuteConfigService configService,CandidateRepo candidateRepo,ExecuteHcmService hcmService,OnboardOrderCheckListRepo onboardOrderCheckListRepo,
                            InterviewInvolveRepo interviewInvolveRepo) {

        this.repo = repo;
        this.eventFactory = eventFactory;
        _noticeService =noticeService;
        _emailSettingRepo = emailSettingRepo;
        _configService = configService;
        _candidateRepo=candidateRepo;
        _hcmService=hcmService;
        _onboardOrderCheckListRepo=onboardOrderCheckListRepo;
        _interviewInvolveRepo=interviewInvolveRepo;
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
    /**
     * @param payload
     * @param cid
     * @param uid
     * @param type
     * @return
     */
    public  EmailSent setScheduleMail(Map<String, Object> payload,Long cid, String uid,String type){
        Long templateId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "templateId", 0l);
        Long emailSettingId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "emailSettingId", 0l);
        Long candidateId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "candidateId", 0l);
        Long employeeId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "employeeId", 0l);
        String typeOnboard = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "typeOnboard", "");
        Long councilSelect =vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "councilSelect", 0l);
//        if(employeeId == 0l && candidateId == 0l){
//            throw new BusinessException("can-not-empty-both-employee-and-candidate");
//        }
        String content = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "content");
        String sign = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "sign", "");
        content = content + "</br>" + sign;
        Map<String, Object> noticeConfig = _configService.getEmailConfigById(uid, cid, templateId);
        EmailSetting setting = _emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, emailSettingId));

        String emailTo =  vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "email", null);
        String refType = "";
        String refId = "";
        if(candidateId != 0l){
            Candidate candidate = _candidateRepo.findById(cid, candidateId).orElseThrow(() -> new EntityNotFoundException(Candidate.class, candidateId));
            if(emailTo==null) emailTo = candidate.getEmail();
            refType = Constants.EMAIL_SENT_REF.CANDIDATE.name();
            refId = candidateId.toString();
        }

//        if(employeeId != 0l){
//            List<EmployeeDTO> emps = _hcmService.getEmployees(uid, cid, Collections.singleton(employeeId));
//            EmployeeDTO emp = emps.get(0);
//            if(emailTo==null) emailTo = emp.getWorkEmail();
//            refType = Constants.EMAIL_SENT_REF.EMPLOYEE.name();
//            refId = employeeId.toString();
//        }

        String title = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "title", vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "title", ""));
        EmailSent es = new EmailSent();
        es.setFromEmail(vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", ""));
        es.setContent(content);
        es.setDate(vn.ngs.nspace.lib.utils.MapUtils.getDate(payload, "date"));
        es.setToEmail(emailTo);
        es.setSubject(title);
        es.setStatus(Constants.ENTITY_INACTIVE);
        es.setCreateBy(uid);
        es.setUpdateBy(uid);
        es.setCompanyId(cid);
        es.setRefType(refType);
        es.setRefId(refId);
        es.setEmailSettingId(emailSettingId);
        es.setTemplateId(templateId);
        es.setUid(uid);
        es.setType(type);
        es.setCouncil(councilSelect);

        Long onboardOrderCheckListId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "onboardOrderCheckListId", 0l);
        if(onboardOrderCheckListId != null){
            OnboardOrderCheckList orderCheckList = _onboardOrderCheckListRepo.findByCompanyIdAndId(cid, onboardOrderCheckListId).orElse(new OnboardOrderCheckList());
            if(!orderCheckList.isNew()){
                orderCheckList.setUpdateBy(uid);
                orderCheckList.setState(Constants.ONBOARD_ORDER_CHECK_LIST_STATE.complete.name());
                _onboardOrderCheckListRepo.save(orderCheckList);
            }
            es.setTypeOnboard(typeOnboard);
        }
        es = repo.save(es);
        Long taskId = es.getId();
        System.out.println("task id "+taskId.toString());
        Date schedule_date = vn.ngs.nspace.lib.utils.MapUtils.getDate(payload,"date");
        ScheduleTaskCommand scheduleAction = new ScheduleTaskCommand();
        scheduleAction.setCompanyId(cid);
        scheduleAction.setEvent("schedule_mail_"+type);
        scheduleAction.setAction("schedule_mail_"+type);
        scheduleAction.setExecuteTime(schedule_date);
        scheduleAction.setTaskId(taskId);
        scheduleAction.setActionId(candidateId);
        createEmailSchedule(scheduleAction);
        return es;
    }
    /**
     * sendMailNow
     * @param payload
     * @param cid
     * @param uid
     * @param type
     * @return
     */
    public  EmailSent sendMailNow(Map<String, Object> payload,Long cid, String uid,String type){
        Long templateId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "templateId", 0l);
        Long emailSettingId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "emailSettingId", 0l);
        List<Long> candidateId  = convertList((List) payload.getOrDefault("orgIds", new ArrayList()));
        // Long employeeId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "employeeId", 0l);
        String typeOnboard = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "typeOnboard", "");
        Long councilSelect =vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "councilSelect", 0l);
        String content = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "content");
        String sign = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "sign", "");
        content = content + "</br>" + sign;
        Map<String, Object> noticeConfig = _configService.getEmailConfigById(uid, cid, templateId);
        EmailSetting setting = _emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, emailSettingId));

        //String emailTo =  vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "email", null);
        String refType = "";
        String refId = "";
        String title = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "title", vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "title", ""));
        String mailSendUser=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", "");
        String mailSendPwd=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "password", "");
        //gửi mail ứng viên
        List<String> mails = convertListString((List) payload.getOrDefault("orgIds", new ArrayList()));
        if(candidateId.size()>0){
            List<Candidate> _candidate = _candidateRepo.findByArrayId(cid, candidateId);
            String finalContent = content;
            _candidate.stream().forEach(e -> {
                String emailTo = e.getEmail();
                _noticeService.publishEmail(uid, cid, mailSendUser
                        , mailSendPwd
                        , title
                        , finalContent, Collections.singleton(uid), Collections.singleton(emailTo));

            });
        }
        //gửi mail hội đồng
        InterviewInvolve interviewInvolve =_interviewInvolveRepo.findByCompanyIdAndId(cid,councilSelect).orElse(new InterviewInvolve());
        List<String> interviewIds=interviewInvolve.getInterviewerId();
        //List<Long> emps=new ArrayList<>();
        String finalContentCouncil = content;
        interviewIds.forEach(e->{
            Long employeeId = Long.valueOf(String.valueOf(e));
            //emps.add(value);
            List<EmployeeDTO> emps = _hcmService.getEmployees(uid, cid, Collections.singleton(employeeId));
            EmployeeDTO emp = emps.get(0);
            String emailToCouncil = emp.getWorkEmail();
            if(!emailToCouncil.isEmpty()) {
                _noticeService.publishEmail(uid, cid, mailSendUser
                        , mailSendPwd
                        , title
                        , finalContentCouncil, Collections.singleton(uid), Collections.singleton(emailToCouncil));
            }
        });
        //

        //
        refType = Constants.EMAIL_SENT_REF.CANDIDATE.name();
        refId = candidateId.toString();
        EmailSent es = new EmailSent();
        es.setFromEmail(mailSendUser);
        es.setContent(content);
        es.setDate(vn.ngs.nspace.lib.utils.MapUtils.getDate(payload, "date"));
        //es.setToEmail(emailTo);
        es.setSubject(title);
        es.setStatus(Constants.ENTITY_ACTIVE);
        es.setCreateBy(uid);
        es.setUpdateBy(uid);
        es.setCompanyId(cid);
        es.setRefType(refType);
        es.setRefId(refId);
        es.setCandidates(refId);
        es.setMails(mails.toString());
        es.setCouncil(councilSelect);
        Long onboardOrderCheckListId = vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "onboardOrderCheckListId", 0l);
        if(onboardOrderCheckListId != null){
            OnboardOrderCheckList orderCheckList = _onboardOrderCheckListRepo.findByCompanyIdAndId(cid, onboardOrderCheckListId).orElse(new OnboardOrderCheckList());
            if(!orderCheckList.isNew()){
                orderCheckList.setUpdateBy(uid);
                orderCheckList.setState(Constants.ONBOARD_ORDER_CHECK_LIST_STATE.complete.name());
                _onboardOrderCheckListRepo.save(orderCheckList);
            }
            es.setTypeOnboard(typeOnboard);
        }
        es = repo.save(es);
        return  es;
    }
    /**
     * @convertList
     * @param ids
     * @return
     */
    private List<Long> convertList(List ids) {
        List<Long> result = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(id -> {
                try {
                    Long value = Long.valueOf(String.valueOf(id));
                    result.add(value);
                } catch (Exception e) {
                }
            });
        }
        if (ids == null || ids.isEmpty()) {
            result.add(0L);
        }
        return result;
    }
    /**
     * @convertList
     * @param ids
     * @return
     */
    private List<String> convertListString(List ids) {
        List<String> result = new ArrayList<>();
        if (ids != null && !ids.isEmpty()) {
            ids.forEach(id -> {
                try {
                    String value = String.valueOf(id);
                    result.add(value);
                } catch (Exception e) {
                }
            });
        }
        if (ids == null || ids.isEmpty()) {
            result.add("");
        }
        return result;
    }
}
