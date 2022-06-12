package vn.ngs.nspace.recruiting.api.v2;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.CostV2Service;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.service.*;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.RecruitmentNewsFilterRequest;

import java.util.*;

@RestController
@RequestMapping("v2/cost")
@RequiredArgsConstructor
public class CostV2Api {

    private final CostV2Service costV2Service;

    @ActionMapping(action = Permission.CREATE)
    @PostMapping("")
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody List<CostDTO> costDTOs
    ) {
        try {
            List<CostDTO> costDTOS = costV2Service.creates(cid, uid, costDTOs);
            return  ResponseUtils.handlerSuccess(costDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @ActionMapping(action = Permission.UPDATE)
    @PutMapping("")
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody List<CostDTO> costDTOs
    ) {
        try {
            List<CostDTO> costDTOS = costV2Service.updates(cid, uid, costDTOs);
            return  ResponseUtils.handlerSuccess(costDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
    @PostMapping("create-list")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create cost"
            , description = "API for create interview template"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createList(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTOs to create")  @RequestBody List<CostDTO> dtos) {
        try {
            return ResponseUtils.handlerSuccess(service.createList(cid, uid, dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("delete-list")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Update List cost"
            , description = "Update List cost"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deleteList(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTOs to update")  @RequestBody List<Long> ids) {
        try {
            return ResponseUtils.handlerSuccess(service.deleteListCost(cid, uid, ids));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

}
