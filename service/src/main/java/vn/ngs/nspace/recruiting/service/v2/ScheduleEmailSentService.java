package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
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
    private final InterviewResultRepo resultRepo;
    private final CandidateRepo candidateRepo;
    private final InterviewCheckListTemplateItemRepo templateItemRepo;


    public ScheduleEmailSentService(EventV2Factory eventV2Factory, EmailSentRepo emailSentRepo, EmailSettingRepo emailSettingRepo, ExecuteNoticeService noticeService, InterviewResultRepo resultRepo, CandidateRepo candidateRepo, InterviewCheckListTemplateItemRepo templateItemRepo) {
        this.eventV2Factory = eventV2Factory;
        this.emailSentRepo = emailSentRepo;
        this.emailSettingRepo = emailSettingRepo;
        this.noticeService = noticeService;
        this.resultRepo = resultRepo;
        this.candidateRepo = candidateRepo;
        this.templateItemRepo = templateItemRepo;
    }

    /**
     * @param cid
     * @param dateSchedule
     * @param emailSentId
     */

    public void createEmailSchedule(String uid, Long cid, Date dateSchedule, Long emailSentId) {
        ScheduleTaskCommand scheduleInvolve = new ScheduleTaskCommand();

        scheduleInvolve.setCompanyId(cid);
        scheduleInvolve.setEvent("schedule_mail");
        scheduleInvolve.setAction("schedule_mail");
        scheduleInvolve.setExecuteTime(dateSchedule);
        scheduleInvolve.setTaskId(emailSentId);
        scheduleInvolve.setActionId(emailSentId);
        scheduleInvolve.setCandidates(uid);

        ScheduleRequest taskReq = new ScheduleRequest();
        taskReq.setCmd(vn.ngs.nspace.workflow.utils.Constants.CMD_CREATE);
        taskReq.setExecuteTime(dateSchedule);
        taskReq.setChannel(scheduleTopic);
        taskReq.setEvent(scheduleInvolve.getAction());
        taskReq.setId(scheduleInvolve.getTaskId() + "_" + scheduleInvolve.getEvent() + "_" + scheduleInvolve.getAction());
        taskReq.setPayload(scheduleInvolve);
        eventV2Factory.publishSchedule(taskReq);
    }

    public void sentEmailAuto(String uid, Long cid, Long emailSentId) {
        EmailSent emailSent = emailSentRepo.findByCompanyIdAndId(cid, emailSentId).orElseThrow(() -> new EntityNotFoundException(EmailSent.class, emailSentId));

        Long emailSettingId = emailSent.getEmailSettingId();
        EmailSetting setting = emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, emailSettingId));

        List<String> emails = new ArrayList<>();
        String password = "";
        String username = "";
        if (setting.getConfigs() != null) {
            emails = Arrays.asList(emailSent.getToEmail().split(","));
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
        Date schedule = request.getScheduleDate() != null ? request.getScheduleDate() : new Date();
        // nếu chọn gửi cho ứng viên
        if (request.isSentCandidate()) {
            if (request.getCandidateIds() != null && !request.getCandidateIds().isEmpty()) {
                refIds = request.getCandidateIds().stream().map(Objects::toString).collect(Collectors.joining(","));

                Candidate candidate = candidateRepo.getOne(Long.valueOf(refIds));
                if (candidate.getState().equalsIgnoreCase(Constants.HCM_RECRUITMENT.APPROVED.name())) {
                    candidate.setState(Constants.HCM_RECRUITMENT.INTERVIEW_INVITED.name());

                    candidateRepo.save(candidate);
                }
            }

            String toEmails = String.join(",", request.getCandidateMails());
            emailSentCandidate = saveEmailSent(toEmails, request.getTemplateId(),
                    subject, content, schedule,
                    cid, username, refIds, uid,
                    request.getEmailSettingId(),
                    Constants.EMAIL_SENT_REF.CANDIDATE.name());

            // nếu đặt lịch thì gửi yêu cầu lên service đặt lịch
            if (request.getScheduleDate() != null) {
                createEmailSchedule(uid, cid, request.getScheduleDate(), emailSentCandidate.getId());
            } else {
                noticeService.publishEmail(uid, cid, username,
                        password, subject, content, Set.of(uid), new HashSet<>(request.getCandidateMails()));
            }
        }

        //nếu chọn hội đồng
        if (request.isSentInvolve()) {
            List<Long> candidateIds = request.getCandidateIds();
            List<Long> interviewerIds = request.getInterviewerIds();

            // Lưu lại các bản ghi đánh giá với ứng viên và người phỏng vấn
            List<InterviewCheckListTemplateItem> templateItems = templateItemRepo.findByCompanyIdAndTemplateId(cid,request.getTemplateCheckList());
            for (Long candidateId : candidateIds) {
                Candidate candidate = candidateRepo.findByCompanyIdAndId(cid, candidateId).orElseThrow(() -> new EntityNotFoundException(Candidate.class, candidateId));
                for (Long interviewerId : interviewerIds) {
                    for (InterviewCheckListTemplateItem item : templateItems) {
                        InterviewResult interviewResult = createInterviewResult(cid, uid, candidateId, request.getInterviewDate(), interviewerId, item.getId());
                        if (interviewerId.equals(request.getInterviewerLastId())) {
                            candidate.setInterviewResultId(interviewResult.getId());
                            candidateRepo.save(candidate);
                        }
                    }

                }
            }
            
            if (request.getInterviewerIds() != null && !request.getInterviewerIds().isEmpty()) {
                refIds = request.getInterviewerIds().stream().map(Objects::toString).collect(Collectors.joining(","));
            }
            String toEmails = String.join(",", request.getInvolveMails());
            emailSentInvolve = saveEmailSent(toEmails, request.getTemplateId(),
                    subject, content, schedule,
                    cid, username, refIds, uid,
                    request.getEmailSettingId(),
                    Constants.EMAIL_SENT_REF.INVOLVE.name());

            // kiểm tra gửi ngay hay lên lịch
            if (request.getInterviewDate() != null) {
                createEmailSchedule(uid,cid, request.getScheduleDate(), emailSentInvolve.getId());
            } else {
                noticeService.publishEmail(uid, cid, username,
                        password, subject, content, Set.of(uid), new HashSet<>(request.getInvolveMails()));
            }


        }
    }

    // tạo bản ghi lưu thông tin kết quả của vòng
    public InterviewResult createInterviewResult(Long cid, String uid, Long candidateId, Date interviewDate, Long interviewerId, Long templateCheckListId) {
        InterviewResult interviewResult = resultRepo.getByCandidateAndCompanyId(cid,candidateId,templateCheckListId, interviewerId);

        if (interviewResult != null) throw new BusinessException("invalid-title");
        interviewResult = new InterviewResult();
        interviewResult.setCompanyId(cid);
        interviewResult.setUpdateBy(uid);
        interviewResult.setCandidateId(candidateId);
        interviewResult.setInterviewDate(interviewDate);
        interviewResult.setInterviewerId(interviewerId);
        interviewResult.setUpdateBy(uid);
        interviewResult.setTemplateCheckListId(templateCheckListId);

        return resultRepo.save(interviewResult);
    }

    // lưu thông tin lịch sử gửi mail
    public EmailSent saveEmailSent(String toEmail, Long templateId, String subject, String content, Date scheduleDate, Long cid, String username, String refIds, String uid, Long emailSettingId, String refTypes) {
        EmailSent emailSent = new EmailSent();
        emailSent.setToEmail(toEmail);
        emailSent.setTemplateId(templateId);
        emailSent.setSubject(subject);
        emailSent.setContent(content);
        emailSent.setRefId(refIds);
        emailSent.setRefType(refTypes);
        emailSent.setDate(scheduleDate);
        emailSent.setCompanyId(cid);
        emailSent.setFromEmail(username);
        emailSent.setRefId(refIds);
        emailSent.setUid(uid);
        emailSent.setUpdateBy(uid);
        emailSent.setEmailSettingId(emailSettingId);

        return emailSentRepo.save(emailSent);
    }
}
