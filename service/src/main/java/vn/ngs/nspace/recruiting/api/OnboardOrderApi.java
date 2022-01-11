package vn.ngs.nspace.recruiting.api;

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
public class OnboardOrderApi {
    private final OnboardOrderService _service;
    private final OnboardOrderRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity search(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> condition
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
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity update(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity getById(@RequestHeader("cid") long cid
        , @RequestHeader("uid") String uid
        , @PathVariable(value = "id") Long id){
        try {
            OnboardOrder order = _repo.findByCompanyIdAndId(cid, id).orElse(new OnboardOrder());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid, order));
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
