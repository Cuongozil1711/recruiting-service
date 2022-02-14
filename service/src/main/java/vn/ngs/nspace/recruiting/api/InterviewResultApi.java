package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.service.InterviewResultService;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("interview-result")
@RequiredArgsConstructor
@Tag(name = "InterviewResult", description = "Interview Result API ")
public class InterviewResultApi {
    private final InterviewResultRepo repo;
    private final InterviewResultService service;


    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Interview Result"
            , description = "Search by condition : name, state, .."
            , tags = { "InterviewResult" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long candidateId = MapUtils.getLong(condition, "candidateId", -1l);
            Page<InterviewResult> page = repo.search(cid,candidateId, pageable);
            List<InterviewResultDTO> dtos = service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Interview Result"
            , description = "Create single Interview Result"
            , tags = { "InterviewResult" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody InterviewResultDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Interview Result by Id "
            , description = "uodate single Interview Result, path param is InterviewResultId"
            , tags = { "InterviewResult" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody InterviewResultDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation( summary = "get one Interview Result by Id"
            , description = "API for get Interview Result by Id")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company") @RequestHeader Long cid
            ,@Parameter(description="ID of user") @RequestHeader String uid
            , @PathVariable(value = "id") Long id){
        try{
            InterviewResult interviewResult = repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, id));
            return ResponseUtils.handlerSuccess(interviewResult);
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/check-list/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get check list by interviewResult Id"
            , description = "Get check list by interviewResult Id"
            , tags = { "InterviewResult", "InterviewCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getCheckListByInterviewResultId(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long ids
            , @RequestBody InterviewResultDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.getInterviewCheckList(cid, uid, ids, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list InterviewResult",
            description = "API for delete list InterviewResult"
            ,tags = "InterviewResult")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            ,@Parameter(description = "List id of record")
            @RequestBody List<Long> ids){
        try {
            service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }



}
