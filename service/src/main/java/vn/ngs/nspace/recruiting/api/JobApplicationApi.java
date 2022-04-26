package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.request.JobApplicationRequest;
import vn.ngs.nspace.recruiting.service.JobApplicationService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.EmployeeRecruitingReq;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.api.TaskApi;
import vn.ngs.nspace.task.core.utils.TaskPermission;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("job-application")
@Tag(name = "JobApplication", description = "Job Application API")
public class JobApplicationApi extends TaskApi<JobApplication, JobApplicationService, JobApplicationRequest> {
    private final JobApplicationService _service;
    private final JobApplicationRepo _repo;

    public JobApplicationApi(JobApplicationService service, JobApplicationRepo repo) {
        super(service);
        _service = service;
        _repo = repo;
    }


    @PostMapping("/init")
    @ActionMapping(action = TaskPermission.CREATE_TASK)
    public ResponseEntity initForm(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestHeader(value = "Accept-Language", defaultValue = "") String locale
            , @RequestParam(value = "include", defaultValue = "") Set<String> include
            , @RequestBody JobApplicationRequest formReq) {
        try {
            Map<String, Object> response = _service.initJobApplicationForm(cid, uid, locale, include, formReq);
            return ResponseUtils.handlerSuccess(response);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/sync-request")
    @ActionMapping(action = TaskPermission.UPDATE_TASK)
    public ResponseEntity syncRequest(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> data) {
        try {
            if (data.get("task") == null) {
                throw new BusinessException("invalid-request-data");
            }
            Map<String, Object> requestInfo = (Map<String, Object>) data.get("task");
            String rootApp = String.valueOf(requestInfo.getOrDefault("rootApp", ""));
            String rootEntity = String.valueOf(requestInfo.getOrDefault("rootEntity", ""));
            Long rootId = Long.valueOf(String.valueOf(requestInfo.getOrDefault("rootId", "0")));
            Long companyId = Long.valueOf(String.valueOf(requestInfo.getOrDefault("companyId", "0")));
            String requestState = String.valueOf(requestInfo.getOrDefault("state", ""));
            String updateBy = String.valueOf(requestInfo.getOrDefault("updateBy", ""));
            if (CompareUtil.compare(rootApp, "recruiting-service")
                    && CompareUtil.compare(rootEntity, "job_application")
                    && !CompareUtil.compare(rootId, 0L)
                    && !CompareUtil.compare(companyId, 0L)) {
                JobApplication jobApplication = _service.getTaskById(companyId, rootId);
                String formState = null;
                if (requestState.equals("DONE")) {
                    formState = Constants.JOB_APPLICATION_STATE.HIRED.name();
                } else if (requestState.equals("FAILED")) {
                    formState = Constants.JOB_APPLICATION_STATE.FAILED.name();
                }
                if (!StringUtils.isEmpty(formState)) {
                    if (jobApplication.getState().equals("INIT")) {
                        JobApplicationRequest jobReq = new JobApplicationRequest();
                        jobReq.setTask(jobApplication);
                        return ResponseUtils.handlerSuccess(_service.changeState(companyId, rootId, updateBy, formState, jobReq, new HashSet<>()));
                    } else if (jobApplication.getState().equals("CANCELED")) {
                        throw new BusinessException("form-was-be-canceled");
                    } else {
                        throw new BusinessException("form-was-be-approved-or-rejected");
                    }
                } else {
                    return ResponseUtils.handlerSuccess();
                }
            } else {
                throw new BusinessException("invalid-request");
            }
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/sync-state-job-application")
    @ActionMapping(action = TaskPermission.UPDATE_TASK)
    public ResponseEntity syncStateJobApplication(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> data) {
        try {

            if (data.get("task") == null) {
                throw new BusinessException("invalid-request-task");
            }

            Map<String, Object> requestInfo = (Map<String, Object>) data.get("task");
           // String rootApp = String.valueOf(requestInfo.getOrDefault("rootApp", ""));
           // String rootEntity = String.valueOf(requestInfo.getOrDefault("rootEntity", ""));
            Long rootId = Long.valueOf(String.valueOf(requestInfo.getOrDefault("rootId", "0")));
            Long companyId = Long.valueOf(String.valueOf(requestInfo.getOrDefault("companyId", "0")));
            //String requestState = String.valueOf(requestInfo.getOrDefault("state", ""));
            String updateBy = String.valueOf(requestInfo.getOrDefault("updateBy", ""));
            String state = String.valueOf(data.get("state"));
            Long requestId = Long.valueOf(String.valueOf(requestInfo.getOrDefault("requestId", 0L)));
            //CompareUtil.compare(rootApp, "recruiting-service")
            //CompareUtil.compare(rootEntity, "job_application")
            if (!CompareUtil.compare(rootId, "0") && !CompareUtil.compare(companyId, "0")) {
                JobApplication jobApplication = _service.getTaskById(companyId, rootId);
                String formState = state;// requestState;
//                if (requestState.equals("DONE")) {
//                    formState = Constants.JOB_APPLICATION_STATE.HIRED.name();
//                } else if (requestState.equals("FAILED")) {
//                    formState = Constants.JOB_APPLICATION_STATE.FAILED.name();
//                }
                if (!StringUtils.isEmpty(formState)) {
                   // if (jobApplication.getState().equals("INIT")) {
                        JobApplicationRequest jobReq = new JobApplicationRequest();
                        jobReq.setTask(jobApplication);
                        return ResponseUtils.handlerSuccess(_service.changeState(companyId, rootId, updateBy, formState, jobReq, new HashSet<>()));
//                    }
//                    else if (jobApplication.getState().equals("CANCELED")) {
//                        throw new BusinessException("form-was-be-canceled");
//                    }
//                    else {
//                        throw new BusinessException("form-was-be-approved-or-rejected");
//                    }
                } else {
                    return ResponseUtils.handlerSuccess();
                }
            } else {
                throw new BusinessException("invalid-request");
            }
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/old/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Candidate Order"
            , description = "Search by condition : name, gender, wardCode, phone, email,..."
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{

        Page<JobApplication> page = _repo.search(cid,pageable);
        List<JobApplicationDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
        return ResponseUtils.handlerSuccess(dtos);


        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @PostMapping("/old")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single JobApplication"
            , description = "Create single JobApplication"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody JobApplicationDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("/old/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update JobApplication by Id"
            , description = "Update JobApplication by Id"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity updateById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            , @RequestBody JobApplicationDTO dto){
        try {
           return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/old/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get JobApplication by Id"
            , description = "Get JobApplication by Id"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id){
        try {
            JobApplication obj = _repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, id));
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid,uid,obj));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("/old/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            _service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
    //create-job-apply

    @PostMapping("/create-job-apply")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity createJobApply(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            ,@RequestBody Map<String, Object> payload){
        try {
            return ResponseUtils.handlerSuccess(_service.createJobApply(cid, uid , payload));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/old/create-employee/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity createEmployee(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            , @Parameter(description = "Id of job-application")  @PathVariable(value = "id") Long id
            , @RequestBody EmployeeRecruitingReq request){
        try {
            return ResponseUtils.handlerSuccess(_service.createEmployee(cid, uid , id, request));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/old/update-employee/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity updateEmployee(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            , @Parameter(description = "Id of job-application")  @PathVariable(value = "id") Long id
            , @RequestBody EmployeeRecruitingReq request){
        try {
            return ResponseUtils.handlerSuccess(_service.updateEmployee(cid, uid , id, request));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/old/current-by-candidate/{candidateId}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "get current active JobApplication by Candidate",
            description = "API get current active JobApplication by Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getCurrentJobByCandidate(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            , @Parameter(description = "Id of candidate")  @PathVariable(value = "candidateId") Long id){
        try {
            JobApplication curr = _repo.findByCompanyIdAndCandidateIdAndStatus(cid, id, Constants.ENTITY_ACTIVE).orElse(new JobApplication());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid,uid,curr));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/old/init-by-candidate/{candidateId}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "get current active JobApplication by Candidate",
            description = "API get current active JobApplication by Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity initJobApplication(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            , @Parameter(description = "Id of candidate")  @PathVariable(value = "candidateId") Long id){
        try {
            return ResponseUtils.handlerSuccess(_service.initByCandidate(cid, uid, id));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

}
