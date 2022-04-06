package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.RecruitmentPlan;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.service.RecruitmentPlanOrderService;
import vn.ngs.nspace.recruiting.service.RecruitmentPlanService;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

@RestController
@RequestMapping("recruiting-plan")
@RequiredArgsConstructor
public class RecruitmentPlanApi {
    private final RecruitmentPlanService _service;
    private final RecruitmentPlanRepo _repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService configService;

    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create many records recruiting plan ",
            description = "can add list to create")

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createRecruitingPlan(
            @Parameter(description="id of company") @RequestHeader Long cid
            , @Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="Payload DTO to create") @RequestBody RecruitmentPlanDTO recruitmentPlanDTO){
        try {
            RecruitmentPlanDTO obj =  _service.create(cid, uid, recruitmentPlanDTO);
            return ResponseUtils.handlerSuccess(obj);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruiting plan  "
            , description = "API for update recruiting plan ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateRecruitingPlanOrder(
            @Parameter(description="ID of company")
            @RequestHeader Long cid
            ,@Parameter(description="ID of User")
            @RequestHeader String uid
            ,@Parameter(description = "param in path")
            @PathVariable Long id
            , @RequestBody RecruitmentPlanDTO recruitmentPlanDTO){
        try{
            RecruitmentPlanDTO dto = _service.update(cid,uid,id,recruitmentPlanDTO);
            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all recruitment"
            , description = "recruitment search"
            , tags = { "recruitment" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload to search")
            @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Page<RecruitmentPlan> page = _repo.search(cid, pageable);
            List<RecruitmentPlanDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get plan by Id"
            , description = "API for get plan by Id"
            , tags = { "plan" }
            , responses = {
            @ApiResponse(description = "Plan with id is response OK Wrap in BaseResponse"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = BaseResponse.class ))),
            @ApiResponse(content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ProfileCheckListTemplateDTO.class))
                    , responseCode = "200"
                    , description = "success" )})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path") @PathVariable(value = "id") Long id){
        try {
            RecruitmentPlan dtos = _repo.findByCompanyIdAndId(cid,id).orElse(new RecruitmentPlan());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid,dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

}

