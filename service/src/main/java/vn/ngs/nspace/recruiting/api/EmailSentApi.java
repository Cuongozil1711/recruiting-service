package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.service.EmailSentService;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("email")
@Tag(name = "Email", description = "API for call send email")
public class EmailSentApi {

    @Autowired
    EmailSentService _service;
    private final EmailSentRepo _repo;
    private final ExecuteConfigService _configService;
    private final EmailSettingRepo _emailSettingRepo;
    private final CandidateRepo _candidateRepo;
    private final ExecuteNoticeService _noticeService;
    private final ExecuteHcmService _hcmService;
    private final OnboardOrderCheckListRepo _onboardOrderCheckListRepo;

    public EmailSentApi(EmailSettingRepo repo, EmailSentRepo repo1, ExecuteConfigService configService, EmailSettingRepo emailSettingRepo, CandidateRepo candidateRepo, ExecuteNoticeService noticeService, ExecuteHcmService hcmService, OnboardOrderCheckListRepo onboardOrderCheckListRepo) {

        _repo = repo1;
        _configService = configService;
        _emailSettingRepo = emailSettingRepo;
        _candidateRepo = candidateRepo;
        _noticeService = noticeService;
        _hcmService = hcmService;
        _onboardOrderCheckListRepo = onboardOrderCheckListRepo;
    }

    @GetMapping("/list/{refType}/{refId}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "List all Email Setting"
            , description = "Have no condition, find all !"
            , tags = { "Email" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path RefType")  @PathVariable(value = "refType") String refType
            , @Parameter(description = "Path RefId")  @PathVariable(value = "refId") String refId) {
        try{
            return ResponseUtils.handlerSuccess(_repo.findByCompanyIdAndRefTypeAndRefId(cid, refType, refId));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Email Sent by ID"
            , description = "Get Email Sent by ID"
            , tags = { "Email" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id) {
        try{
            return ResponseUtils.handlerSuccess(_repo.findByCompanyIdAndId(cid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/send")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Email Sent by ID"
            , description = "Get Email Sent by ID"
            , tags = { "Email" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity sendEmail(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload of record")  @RequestBody Map<String, Object> payload) {
        try{
            Long templateId = MapUtils.getLong(payload, "templateId", 0l);
            Long emailSettingId = MapUtils.getLong(payload, "emailSettingId", 0l);
            Long candidateId = MapUtils.getLong(payload, "candidateId", 0l);
            Long employeeId = MapUtils.getLong(payload, "employeeId", 0l);
            if(employeeId == 0l && candidateId == 0l){
                throw new BusinessException("can-not-empty-both-employee-and-candidate");
            }

            String content = MapUtils.getString(payload, "content");
            String sign = MapUtils.getString(payload, "sign", "");
            content = content + "</br>" + sign;
            Map<String, Object> noticeConfig = _configService.getEmailConfigById(uid, cid, templateId);
            EmailSetting setting = _emailSettingRepo.findByCompanyIdAndId(cid, emailSettingId).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, emailSettingId));

            String emailTo = "";
            String refType = "";
            String refId = "";
            if(candidateId != 0l){
                Candidate candidate = _candidateRepo.findByCompanyIdAndId(cid, candidateId).orElseThrow(() -> new EntityNotFoundException(Candidate.class, candidateId));
                emailTo = candidate.getEmail();
                refType = Constants.EMAIL_SENT_REF.CANDIDATE.name();
                refId = candidateId.toString();
            }
            if(employeeId != 0l){
                List<EmployeeDTO> emps = _hcmService.getEmployees(uid, cid, Collections.singleton(employeeId));
                EmployeeDTO emp = emps.get(0);
                emailTo = emp.getWorkEmail();
                refType = Constants.EMAIL_SENT_REF.EMPLOYEE.name();
                refId = employeeId.toString();
            }

            String title = MapUtils.getString(payload, "title", MapUtils.getString(noticeConfig, "title", ""));
            _noticeService.publishEmail(uid, cid, MapUtils.getString(setting.getConfigs(), "email", "")
                    , MapUtils.getString(setting.getConfigs(), "password", "")
                    , title
                    , content, Collections.singleton(uid), Collections.singleton(emailTo));

            EmailSent es = new EmailSent();
            es.setFromEmail(MapUtils.getString(setting.getConfigs(), "email", ""));
            es.setContent(content);
            es.setDate(MapUtils.getDate(payload, "date"));
            es.setToEmail(emailTo);
            es.setSubject(title);
            es.setStatus(Constants.ENTITY_ACTIVE);
            es.setCreateBy(uid);
            es.setUpdateBy(uid);
            es.setCompanyId(cid);
            es.setRefType(refType);
            es.setRefId(refId);
            es = _repo.save(es);

            Long onboardOrderCheckListId = MapUtils.getLong(payload, "onboardOrderCheckListId", 0l);
            if(onboardOrderCheckListId != null){
                OnboardOrderCheckList orderCheckList = _onboardOrderCheckListRepo.findByCompanyIdAndId(cid, onboardOrderCheckListId).orElse(new OnboardOrderCheckList());
                if(!orderCheckList.isNew()){
                    orderCheckList.setUpdateBy(uid);
                    orderCheckList.setState(Constants.ONBOARD_ORDER_CHECK_LIST_STATE.DONE.name());
                    _onboardOrderCheckListRepo.save(orderCheckList);
                }
            }

            return ResponseUtils.handlerSuccess(es);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
