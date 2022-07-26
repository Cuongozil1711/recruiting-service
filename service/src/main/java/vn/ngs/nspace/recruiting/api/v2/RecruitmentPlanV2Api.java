package vn.ngs.nspace.recruiting.api.v2;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentPlanV2Service;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.request.PlantRequestFilter;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;

import java.util.List;

@RestController
@RequestMapping(value = "v2/recruitment-plan")
@AllArgsConstructor
public class RecruitmentPlanV2Api {

    private final RecruitmentPlanV2Service planV2Service;

    @PostMapping("list")
    @ActionMapping(action = Permission.VIEW)
    public ResponseEntity getPage(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentFilterRequest request
            , Pageable page
    ) {
        try {
            Page<RecruitmentPlanDTO> recruitmentPlanDTOS = planV2Service.getPage(cid, uid, request, page);
            return ResponseUtils.handlerSuccess(recruitmentPlanDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("")
    @ActionMapping(action = Permission.CREATE)
    public ResponseEntity create(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentPlanDTO dto
    ) {
        try {
            RecruitmentPlanDTO recruitmentPlanDTO = planV2Service.create(cid, uid, dto);
            return ResponseUtils.handlerSuccess(recruitmentPlanDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    public ResponseEntity getById(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("id") Long id
            , @RequestParam("size") Integer size
            , @RequestParam("page") Integer page
            ) {
        try {
            PlantRequestFilter filter = new PlantRequestFilter();
            filter.setSize(size);
            filter.setPage(page);
            RecruitmentPlanDTO recruitmentPlanDTO = planV2Service.getById(cid, uid, id, filter);
            return ResponseUtils.handlerSuccess(recruitmentPlanDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.CREATE)
    public ResponseEntity update(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("id") Long id
            , @RequestBody RecruitmentPlanDTO dto
    ) {
        try {
            RecruitmentPlanDTO recruitmentPlanDTO = planV2Service.update(cid, uid, id, dto);
            return ResponseUtils.handlerSuccess(recruitmentPlanDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("delete")
    @ActionMapping(action = Permission.CREATE)
    public ResponseEntity delete(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody List<Long> ids
    ) {
        try {
            planV2Service.delete(cid, uid, ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
