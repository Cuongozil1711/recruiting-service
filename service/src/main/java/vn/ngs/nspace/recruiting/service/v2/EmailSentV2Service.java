package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.schedule.ScheduleRequest;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.service.EventFactory;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.share.dto.EmailSentDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.EmailSentRequest;

import javax.transaction.Transactional;
import java.util.*;
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

    public void SentEmail(Long cid, String uid, EmailSentRequest request) {
        EmailSent emailSent = new EmailSent();

        EmailSetting setting = emailSettingRepo.findByCompanyIdAndId(cid, request.getEmailSettingId()).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, request.getEmailSettingId()));

        String refIds = "";
        if (request.getCandidateIds() != null && !request.getCandidateIds().isEmpty()) {
            refIds = request.getCandidateIds().stream().map(Objects::toString).collect(Collectors.joining(","));
            emailSent.setRefType(Constants.EMAIL_SENT_REF.CANDIDATE.name());
            emailSent.setRefId(refIds);
        }

        String username = "";
        String password = "";
        String subject = request.getTitle() != null ? request.getTitle() : "";
        String content = request.getContent() != null ? request.getContent() : "";

        if (setting.getConfigs() != null) {
            username = MapUtils.getString(setting.getConfigs(), "email", "");
            password = MapUtils.getString(setting.getConfigs(), "password", "");
        }
        noticeService.publishEmail(uid, cid, username,
                password, subject, content, Set.of(uid), new HashSet<>(request.getMails()));

        emailSent.setToEmail(String.join(",", request.getMails()));
        emailSent.setTemplateId(request.getTemplateId());
        emailSent.setSubject(subject);
        emailSent.setContent(request.getContent());
        emailSent.setDate(request.getDate() != null ? request.getDate() : new Date());
        emailSent.setCompanyId(cid);
        emailSent.setFromEmail(username);
        emailSent.setRefId(refIds);
        emailSent.setUid(uid);
        emailSent.setUpdateBy(uid);
        emailSent.setEmailSettingId(request.getEmailSettingId());

        emailSentRepo.save(emailSent);
    }

    public List<EmailSentDTO> getList(Long cid, Long id) {
        List<EmailSent> emailSents = emailSentRepo.getListEmailSent(cid,id.toString());

        return toDTOs(emailSents);
    }

    public EmailSentDTO toDTO(EmailSent emailSent) {
        return MapperUtils.map(emailSent, EmailSentDTO.class);
    }

    public List<EmailSentDTO> toDTOs(List<EmailSent> emailSents) {
        List<EmailSentDTO> emailSentDTOS = new ArrayList<>();

        emailSents.forEach(emailSentDTO -> {
            emailSentDTOS.add(toDTO(emailSentDTO));
        });

        return emailSentDTOS;
    }
}
