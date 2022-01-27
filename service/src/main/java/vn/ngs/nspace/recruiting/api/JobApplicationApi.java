package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.service.JobApplicationService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.EmployeeRecruitingReq;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("job-application")
@RequiredArgsConstructor
@Tag(name = "JobApplication", description = "Job Application API")
public class JobApplicationApi {
    private final JobApplicationService _service;
    private final JobApplicationRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Candidate Order"
            , description = "Search by condition : name, gender, wardCode, phone, email,..."
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
//        String fullname = MapUtils.getString(condition, "fullname", "all");
//        Long gender = MapUtils.getLong(condition, "gender", -1l);
//        String wardCode = MapUtils.getString(condition, "wardCode", "all");
//        String phone = MapUtils.getString(condition, "phone", "all");
//        String email = MapUtils.getString(condition, "email", "all");

//        Page<Candidate> page = _repo.search(cid, fullname, gender , wardCode, phone, email, pageable);
//        List<CandidateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
        return ResponseUtils.handlerSuccess("");


        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single JobApplication"
            , description = "Create single JobApplication"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody JobApplicationDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update JobApplication by Id"
            , description = "Update JobApplication by Id"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateById(
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

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get JobApplication by Id"
            , description = "Get JobApplication by Id"
            , tags = { "JobApplication" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id){
        try {
            JobApplication obj = _repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, id));
            return ResponseUtils.handlerSuccess(_service.toDTO(obj));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deteleList(
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

    @PostMapping("/create-employee/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list JobApplication",
            description = "API for delete list JobApplication")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createEmployee(
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

    @GetMapping("/current-by-candidate/{candidateId}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "get current active JobApplication by Candidate",
            description = "API get current active JobApplication by Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getCurrentJobByCandidate(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "ID of userID") @RequestHeader String uid
            , @Parameter(description = "Id of candidate")  @PathVariable(value = "candidateId") Long id){
        try {
            return ResponseUtils.handlerSuccess(_repo.findByCompanyIdAndCandidateIdAndStatus(cid, id, Constants.ENTITY_ACTIVE).orElse(new JobApplication()));
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/init-by-candidate/{candidateId}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "get current active JobApplication by Candidate",
            description = "API get current active JobApplication by Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity initJobApplication(
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
