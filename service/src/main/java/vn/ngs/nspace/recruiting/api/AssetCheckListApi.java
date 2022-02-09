package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
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

    @GetMapping("/get-by-employee/{id}")
    @ActionMapping(action = Permission.VIEW)
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

}
