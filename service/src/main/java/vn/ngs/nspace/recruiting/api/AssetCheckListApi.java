package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.AssetCheckListRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.service.AssetCheckListService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;
@RestController
    @RequestMapping("asset-check-list")
@RequiredArgsConstructor
public class AssetCheckListApi {
    private final AssetCheckListService _service;
    private final AssetCheckListRepo repo;

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody List<AssetCheckListDTO> listDTOS) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, listDTOS));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody AssetCheckListDTO assetCheckListDTO){
        try {
           AssetCheckListDTO dto = _service.update(cid, uid, id,assetCheckListDTO);
            return ResponseUtils.handlerSuccess(dto);

        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-by-onboarding/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getByOnboardOrder(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id){
        try {
            List<AssetCheckList> datas = repo.findByCompanyIdAndOnboardOrderId(cid, id);
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, datas));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-by-onboarding-and-type/{id}/{type}")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getByOnboardOrder(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
            , @PathVariable(value = "type") String type){
        try {
            List<AssetCheckList> datas = repo.findByCompanyIdAndOnboardOrderIdAndAssetType(cid, id, type);
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, datas));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-by-employee/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getByEmployee(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id){
        try {
            List<AssetCheckList> datas = repo.findByCompanyIdAndEmployeeId(cid, id);
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, datas));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping ("/hand-over-asset-by-emid/{id}")
    @ActionMapping(action = Permission.CREATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity handOverAsset(@RequestHeader Long cid
            , @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody List<AssetCheckListDTO> listDTOS) {
        try {

            return ResponseUtils.handlerSuccess(_service.handOverAsset(cid, uid, id, listDTOS));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Change state AssetCheckList for employee"
            , description = "Change state AssetCheckList for employee"
            , tags = {"ChangeStateAssetCheckList", "changeStateAssetCheckList"}
    )
    @GetMapping(value = "/change-state-job-application/{employeeId}/{id}")
    public ResponseEntity changeStateAssetCheckList(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "employeeId") Long employeeId
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
    ) {
        try {
            AssetCheckList assetCheckList = _service.changeStateAssetCheckList(cid, employeeId, id);
            return ResponseUtils.handlerSuccess(assetCheckList);
        } catch (Exception exception) {
            return ResponseUtils.handlerException(exception);
        }

    }
}
