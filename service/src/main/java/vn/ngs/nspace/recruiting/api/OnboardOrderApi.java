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
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.service.OnboardOrderService;
import vn.ngs.nspace.recruiting.service.ProfileCheckListTemplateService;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("onboard-order")
@RequiredArgsConstructor
@Tag(name = "OnboardOrder", description = "Onboard Order API ")
public class OnboardOrderApi {
    private final OnboardOrderService _service;
    private final OnboardOrderRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Onboard Order"
            , description = "Search by condition : employeeId, buddy, jobApplicationId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long employeeId = MapUtils.getLong(condition, "employeeId", -1l);
            Long buddy = MapUtils.getLong(condition, "buddy", -1l);
            Long jobApplicationId = MapUtils.getLong(condition, "jobApplicationId", -1l);
            Page<OnboardOrder> page = _repo.search(cid, buddy, employeeId, jobApplicationId, pageable);
            List<OnboardOrderDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Onboard Order"
            , description = "Create single Onboard Order"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Onboard Order by Id "
            , description = "Create single Onboard Order, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "View Onboard Order by Id "
            , description = "View single Onboard Order, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id){
        try {
            OnboardOrder order = _repo.findByCompanyIdAndId(cid, id).orElse(new OnboardOrder());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid, order));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/check-list/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get check list by onboard Id"
            , description = "Get check list by onboard Id"
            , tags = { "OnboardOrder", "OnboardCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getCheckListByOnboardId(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id){
        try {
            return ResponseUtils.handlerSuccess(_service.getOnboardOrderCheckList(cid, uid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/create-buddy-mentor")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create Buddy and Mentor by OnboardId"
            , description = "Create Buddy and Mentor by OnboardId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createBuddyAndMentor(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.createBuddyByOnbodrdId(cid, uid, dto.getId(), dto.getBuddy(), dto.getMentorId()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/update-buddy-mentor/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Buddy and Mentor by OnboardId "
            , description = "Update single Buddy and Mentor, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateBuddyAndMentor(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.updateBuddyByOnbodrdId(cid, uid, id, dto.getBuddy(), dto.getMentorId()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

//    @PostMapping("/by-cycle")
//    @ActionMapping(action = Permission.VIEW)
//    protected ResponseEntity getAssetCheckList(@RequestHeader Long cid
//            , @RequestHeader String uid
//            , @RequestBody Map<String, Object> filter) {
//
//        return null;

}
