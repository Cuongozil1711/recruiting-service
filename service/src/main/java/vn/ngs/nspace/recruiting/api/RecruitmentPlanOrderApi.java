package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.service.RecruitmentPlanOrderService;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.swing.*;
import java.util.*;

@RestController
@RequestMapping("recruiting-plan-order")
@RequiredArgsConstructor
public class RecruitmentPlanOrderApi {
    private final RecruitmentPlanOrderService _service;
    private final RecruitmentPlanOrderRepo _repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService configService;

    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create many records recruiting plan order",
            description = "can add list to create")

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createRecruitingPlanOrders(
            @Parameter(description="id of company") @RequestHeader Long cid
            , @Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="Payload DTO to create") @RequestBody List<RecruitmentPlanOrderDTO> recruitmentPlanOrderDTOS){
        try {
            List<RecruitmentPlanOrderDTO> list =  _service.create(cid, uid, recruitmentPlanOrderDTOS);
            return ResponseUtils.handlerSuccess(list);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation( summary = "create recruiting plan order"
            , description = "API for create recruiting plan order")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createRecruitingPlanOrder(
            @Parameter(description="id of company") @RequestHeader Long cid
            ,@Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="Payload DTO to create") @RequestBody RecruitmentPlanOrderDTO _dto) {
        try {
            RecruitmentPlanOrderDTO dto = _service.create(cid, uid, _dto);
            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get RecruitingPlanOrder by Id"
            , description = "API for get Recruiting Plan Order  by Id")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getRecruitingPlanOrder(
            @Parameter(description="id of company") @RequestHeader Long cid
            ,@Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="param in path") @PathVariable(value = "id") Long id){
        try{
            RecruitmentPlanOrder recruitmentPlanOrder =_repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(RecruitmentPlanOrder.class, id));
            return ResponseUtils.handlerSuccess(recruitmentPlanOrder);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
    @PostMapping("/filter")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all recruiting plan order By org"
            , description = " search by code, position, ....")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity filter(
            @Parameter(description="ID of company")
            @RequestHeader Long cid
            ,@Parameter(description="ID of user")
            @RequestHeader String uid
            , @Parameter(description="Payload to search with positionId, orgId, fromDate, deadline")
            @RequestParam(name = "filter") String filter
            ,@RequestParam(name = "orgId",value = "-1") Long orgId
            , Pageable pageable){
        try{
            Page<RecruitmentPlanOrder> page = _repo.filter(cid,orgId, filter, pageable);
            List<RecruitmentPlanOrderDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all recruiting plan order"
            , description = "Profile search by %name% format")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    protected ResponseEntity searchRecruitingPlanOrder(
            @Parameter(description="ID of company")
            @RequestHeader Long cid
             ,@Parameter(description="ID of user")
             @RequestHeader String uid
            , @Parameter(description="Payload to search with positionId, orgId, fromDate, deadline")
             @RequestBody Map<String,Object> search
            , Pageable pageable){
        try{
          Long orgId = MapUtils.getLong(search,"orgId",-1l);
          Long positionId = MapUtils.getLong(search,"positionId",-1l);

          Page<RecruitmentPlanOrder> list = _repo.searchRecruitingPlanOrder(cid,orgId,positionId,pageable);
          List<RecruitmentPlanOrderDTO> dtos = _service.toDTOs(cid, uid, list.getContent());
          Page<Map<String,Object>>resp = new PageImpl(dtos, pageable, dtos.size());
            return ResponseUtils.handlerSuccess(resp);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruiting plan order "
            , description = "API for update recruiting plan order template")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateRecruitingPlanOrder(
            @Parameter(description="ID of company")
            @RequestHeader Long cid
            ,@Parameter(description="ID of User")
             @RequestHeader String uid
            ,@Parameter(description = "param in path")
             @PathVariable Long id
            , @RequestBody RecruitmentPlanOrderDTO recruitmentPlanOrderDTO){
        try{
            RecruitmentPlanOrderDTO dto = _service.update(cid,uid,id,recruitmentPlanOrderDTO);
             return ResponseUtils.handlerSuccess(dto);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

}
