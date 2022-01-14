package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.InterviewInvolveRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.service.InterviewInvolveService;
import vn.ngs.nspace.recruiting.service.OnboardOrderService;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("interview-involve")
@RequiredArgsConstructor
public class InterviewInvolveApi {
    private final InterviewInvolveService _service;
    private final InterviewInvolveRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "search interview involve",
            description = "search list interview involve by interviewerId, supporterId,orgId,positionId, titleId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long interviewId = MapUtils.getLong(condition, "interviewId", -1l);
            String interviewerId = MapUtils.getString(condition, "interviewerId", "#");
            Long supporterId = MapUtils.getLong(condition, "supporterId", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);
            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long titleId = MapUtils.getLong(condition,"titleId", -1l);

            Page<InterviewInvolve> page = _repo.search(cid, interviewId, orgId, positionId, titleId, interviewerId, supporterId, pageable);
            List<InterviewInvolveDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create interview involve",
            description = "API for create interview involve ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody InterviewInvolveDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/create-list")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create list interview involve",
            description = "API for create list interview involve ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createList(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody List<InterviewInvolveDTO> dtos) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "update interview involve",
            description = "API for update interview involve by Id ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody InterviewInvolveDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "get interview involve",
            description = "API for get interview involve by Id")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(@RequestHeader("cid") long cid
        , @RequestHeader("uid") String uid
        , @PathVariable(value = "id") Long id){
        try {
            InterviewInvolve order = _repo.findByCompanyIdAndId(cid, id).orElse(new InterviewInvolve());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid, order));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/read-config")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "config interview involve",
            description = "API for config interview involve ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity readConfig(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> condition) {
        try{
            Long interviewId = MapUtils.getLong(condition, "interviewId", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);
            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long titleId = MapUtils.getLong(condition, "titleId", -1l);

            List<InterviewInvolve> data = _repo.readConfig(cid, interviewId, orgId, positionId, titleId);
            List<InterviewInvolveDTO> dtos = _service.toDTOs(cid, uid, data);
            return ResponseUtils.handlerSuccess(dtos);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/apply-involves/{id}")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "apply interview involve",
            description = "API for aplly interview involve for list org, position and title")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity applyInvolves(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable Long id
            , @RequestBody List<InterviewInvolveDTO> dtos){
        try {
            dtos = _service.applyInvolves(cid,uid, id, dtos);
            return ResponseUtils.handlerSuccess(dtos);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
}
