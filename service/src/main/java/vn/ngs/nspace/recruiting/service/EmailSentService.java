package vn.ngs.nspace.recruiting.service;

import org.apache.commons.collections.MapUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.handler.NoticeEvent;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

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
    private final NoticeEvent _noticeEvent;
    @Value("${nspace.scheduleTopic:recruiting-schedule}")
    public String scheduleTopic;

    public EmailSentService(EmailSentRepo repo, EventFactory eventFactory,ExecuteNoticeService noticeService,EmailSettingRepo emailSettingRepo,
                            ExecuteConfigService configService,CandidateRepo candidateRepo,ExecuteHcmService hcmService,OnboardOrderCheckListRepo onboardOrderCheckListRepo,
                            InterviewInvolveRepo interviewInvolveRepo, NoticeEvent noticeEvent) {

        this.repo = repo;
        this.eventFactory = eventFactory;
        _noticeService =noticeService;
        _emailSettingRepo = emailSettingRepo;
        _configService = configService;
        _candidateRepo=candidateRepo;
        _hcmService=hcmService;
        _onboardOrderCheckListRepo=onboardOrderCheckListRepo;
        _interviewInvolveRepo=interviewInvolveRepo;
        _noticeEvent=noticeEvent;
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
            List<String> emails = Collections.singletonList(es.getMails());
            // gủi mail cho ứng viên
            emails.forEach(mail->{
                _noticeService.publishEmail(es.getUid(), es.getCompanyId(), MapUtils.getString(setting.getConfigs(), "email", "")
                        , MapUtils.getString(setting.getConfigs(), "password", "")
                        , es.getSubject()
                        , es.getContent(), Collections.singleton(es.getUid()), Collections.singleton(mail));
            });
            // thông báo hội đồng
            sendNotify(cid,es.getUid(),es.getCouncil(),setting,es.getSubject(),es.getContent());
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
        String typeOnboard = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "typeOnboard", "");
        Long councilSelect =vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "councilSelect", 0l);
        List<String> mails = convertListString((List) payload.getOrDefault("mails", new ArrayList()));

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
        es.setMails(mails.toString());

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
        scheduleAction.setEvent("schedule_mail");
        scheduleAction.setAction("schedule_mail");
        scheduleAction.setExecuteTime(schedule_date);
        scheduleAction.setTaskId(taskId);
        scheduleAction.setActionId(candidateId);
        createEmailSchedule(scheduleAction);
        return es;
    }
    /**
     * @param payload
     * @param cid
     * @param uid
     * @param type
     * @return
     */
    public  EmailSent setScheduleMailList(Map<String, Object> payload,Long cid, String uid,String type){
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
    public  EmailSent sendMailNowList(Map<String, Object> payload,Long cid, String uid,String type){
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
        String title = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "title", vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "title", ""));
        String mailSendUser=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", "");
        setSendUserAndCouncil(payload,cid,uid,setting,candidateId,title,content);
        EmailSent  es = saveEmailSend(payload,cid,uid,councilSelect,candidateId,mailSendUser,content,title,setting);
        return  es;
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
        String title = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "title", vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "title", ""));
        String mailSendUser=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", "");
        setSendUserAndCouncil(payload,cid,uid,setting,candidateId,title,content);
       EmailSent  es = saveEmailSend(payload,cid,uid,councilSelect,candidateId,mailSendUser,content,title,setting);
        return  es;
    }
    /**
     * @saveEmailSend
     * @param payload
     * @param cid
     * @param uid
     * @param councilSelect
     * @param candidateId
     * @param mailSendUser
     * @param content
     * @param title
     * @param setting
     * @return
     */
    private EmailSent saveEmailSend( Map<String, Object> payload, Long cid,String uid, Long councilSelect, List<Long> candidateId,String mailSendUser,String content,String title,EmailSetting setting){
        EmailSent es = new EmailSent();
        String refType = Constants.EMAIL_SENT_REF.CANDIDATE.name();
        String refId = candidateId.toString();
        List<String> mails = convertListString((List) payload.getOrDefault("mails", new ArrayList()));
        String typeOnboard = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "typeOnboard", "");

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
        return es;
    }
    /**
     * @setSendUserAndCouncil
     * @param payload
     * @param cid
     * @param uid
     * @param setting
     * @param candidateId
     * @param title
     * @param content
     */
    private void setSendUserAndCouncil(Map<String, Object> payload,Long cid, String uid,EmailSetting setting,List<Long>candidateId,String title,String content){
        EmailSent finalEs = new EmailSent();
        String mailSendUser=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", "");
        String mailSendPwd=vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "password", "");
        Long councilSelect =vn.ngs.nspace.lib.utils.MapUtils.getLong(payload, "councilSelect", 0l);

       String refType = Constants.EMAIL_SENT_REF.CANDIDATE.name();
       String refId = candidateId.toString();
        //gửi mail ứng viên
        List<String> mails = convertListString((List) payload.getOrDefault("mails", new ArrayList()));
        if(candidateId.size()>0){
            List<Candidate> _candidate = _candidateRepo.findByArrayId(cid, candidateId);
            _candidate.stream().forEach(e -> {
                String emailTo = e.getEmail();
                _noticeService.publishEmail(uid, cid, mailSendUser
                        , mailSendPwd
                        , title
                        , content, Collections.singleton(uid), Collections.singleton(emailTo));

            });
        }
        //gửi mail hội đồng
        sendNotify(cid,uid,councilSelect,setting,title,content);
    }

    /**
     *
     * @param cid
     * @param uid
     * @param councilSelect
     * @param setting
     * @param title
     * @param content
     */
    private  void  sendNotify(Long cid,String uid,Long councilSelect,EmailSetting setting,String title,String content){
        InterviewInvolve interviewInvolve =_interviewInvolveRepo.findByCompanyIdAndId(cid,councilSelect).orElse(new InterviewInvolve());
        List<String> interviewIds=interviewInvolve.getInterviewerId();
        Long templId=211L;
        Map<String, Object> noticeConfig = _configService.getEmailConfigById(uid, cid, templId);
        // String finalRefId = refId;
        interviewIds.forEach(e->{
            Long employeeId = Long.valueOf(String.valueOf(e));
            //emps.add(value);
            List<EmployeeDTO> emps = _hcmService.getEmployees(uid, cid, Collections.singleton(employeeId));
            EmployeeDTO emp = emps.get(0);
            String emailToCouncil = emp.getWorkEmail();
            if(!emailToCouncil.isEmpty()) {
                _noticeService.publishEmail(uid, cid, vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "email", "")
                        , vn.ngs.nspace.lib.utils.MapUtils.getString(setting.getConfigs(), "password", "")
                        , title
                        , content, Collections.singleton(uid), Collections.singleton(emailToCouncil));

                Map<String, Object> entityData = new HashMap<>();
                entityData.put("refId", emp.getId().toString());
                entityData.put("content", content);
                entityData.put("reaction", title);

                Set<String> involves = new HashSet<>();
                // if (!CompareUtil.compare(emp.getUserMappingId(), uid)) {
                involves.add(emp.getUserMappingId());
                //}
                String application= vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "application", Constants.HCM_SERVICE_RECRUITING);
                String action =vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "action", Constants.HCM_SERVICE_RECRUITING);
                String code =vn.ngs.nspace.lib.utils.MapUtils.getString(noticeConfig, "code", Constants.HCM_SERVICE_RECRUITING);
                String templateType =code.toString();// application +"." + code.toString()+"."+action;
                _noticeEvent.send(cid,emp.getUserMappingId(),templateType,action,entityData,involves);
            }
        });
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
