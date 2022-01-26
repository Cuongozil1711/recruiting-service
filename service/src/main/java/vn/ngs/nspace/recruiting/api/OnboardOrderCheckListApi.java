package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.service.OnboardOrderCheckListService;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import java.util.List;


@RestController
@RequestMapping("onboard-order-checklist")
@RequiredArgsConstructor
@Tag(name = "OnboardOrder", description = "Onboard Order Check List API ")
public class OnboardOrderCheckListApi {
    private final OnboardOrderCheckListService _service;
    private final OnboardOrderCheckListRepo _repo;

    @PutMapping("/change-state")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Change State Onboard Order Check List by Id and by employId "
            , description = "Create single Onboard Order Check List, path param is OnboardOrderCheckListId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity changeState(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody OnboardOrderCheckListDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.changeState(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Onboard Ordere Check List by OnboardId"
            , description = "API for Onboard Ordere Check List  by OnboardId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getOnboardOrderCheckList(
            @Parameter(description="id of company") @RequestHeader Long cid
            ,@Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="param in path") @PathVariable(value = "id") Long id){
        try{
            List<OnboardOrderCheckList> lst = _repo.findByCompanyIdAndOnboardOrderId(cid, id);
            return ResponseUtils.handlerSuccess(lst);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

}
