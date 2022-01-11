package vn.ngs.nspace.recruiting.api;

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
    protected ResponseEntity createAssetCheckList(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody AssetCheckListDTO _dto) {
        try {
            AssetCheckListDTO dto = _service.create(cid, uid, _dto);
            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity getAssetCheckList(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
    , @RequestBody AssetCheckListDTO assetCheckListDTO){ {
        try {
           AssetCheckListDTO dto = _service.updateAssetChecklist(cid,id,assetCheckListDTO);
            return ResponseUtils.handlerSuccess(dto);

        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
;
//    @PostMapping("/search")
//    @ActionMapping(action = Permission.VIEW)
//    protected ResponseEntity searchAssetCheckList(@RequestHeader Long cid
//            , @RequestHeader String uid
//            , @RequestBody Map<String, Object> filter
//            , Pageable pageable) {
//
//        return null;
//    }


//    @PostMapping("/by-cycle")
//    @ActionMapping(action = Permission.VIEW)
//    protected ResponseEntity getAssetCheckList(@RequestHeader Long cid
//            , @RequestHeader String uid
//            , @RequestBody Map<String, Object> filter) {
//
//        return null;
    }
}
