package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.OnboardCheckListV2Service;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;

import java.util.List;

/**
 * api liên quan đến phần thủ tục onboard
 */


@RestController
@RequestMapping("v2/onboard")
@RequiredArgsConstructor
@Tag(name = "Interview Check List", description = "Interview Check List API")
public class OnboardCheckListV2Api {

    private final OnboardCheckListV2Service checkListV2Service;

    @GetMapping("list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Onboard Setting"
            , description = "Search by condition : name, code"
            , tags = {"Onboard"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    public ResponseEntity getPage(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , Pageable pageable
    ) {
        try {
            Page<OnboardOrderCheckListDTO> checkListDTOS = checkListV2Service.getPage(cid, uid, pageable);
            return ResponseUtils.handlerSuccess(checkListDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "create Onboard Setting"
            , description = "create onboard setting"
            , tags = {"Onboard"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    public ResponseEntity create(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody OnboardOrderCheckListDTO request
    ) {
        try {
            OnboardOrderCheckListDTO checkListDTO = checkListV2Service.create(cid, uid, request);
            return ResponseUtils.handlerSuccess(checkListDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "update Onboard Setting"
            , description = "update onboard setting"
            , tags = {"Onboard"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    public ResponseEntity update(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("id") Long id
            , @RequestBody OnboardOrderCheckListDTO request
    ) {
        try {
            OnboardOrderCheckListDTO checkListDTO = checkListV2Service.update(cid,id, uid, request);
            return ResponseUtils.handlerSuccess(checkListDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Onboard Setting by id"
            , description = "Get onboard setting id"
            , tags = {"Onboard"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    public ResponseEntity getById(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("id") Long id
    ) {
        try {
            OnboardOrderCheckListDTO checkListDTO = checkListV2Service.getById(cid,id, uid);
            return ResponseUtils.handlerSuccess(checkListDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete onboard type",
            description = "API for delete list Schedule type")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            , @Parameter(description = "ID of userID")
            @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            checkListV2Service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

}
