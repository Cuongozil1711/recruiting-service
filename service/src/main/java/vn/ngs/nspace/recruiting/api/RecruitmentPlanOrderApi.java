package vn.ngs.nspace.recruiting.api;

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
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.service.RecruitmentPlanOrderService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

@RestController
@RequestMapping("recruiting-plan-order")
@RequiredArgsConstructor
public class RecruitmentPlanOrderApi {
    private final RecruitmentPlanOrderService _service;
    private final RecruitmentPlanOrderRepo _repo;
    private final ExecuteHcmService _hcmService;


    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    protected ResponseEntity createRecruitingPlanOrder(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody RecruitmentPlanOrderDTO _dto) {
        try {
            RecruitmentPlanOrderDTO dto = _service.create(cid, uid, _dto);
            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);

        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity getRecruitingPlanOrder(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable(value = "id") Long id){
        try{
            RecruitmentPlanOrder recruitmentPlanOrder =_repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(RecruitmentPlanOrder.class, id));
            return ResponseUtils.handlerSuccess(recruitmentPlanOrder);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity searchRecruitingPlanOrder(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String,Object> filter
            , Pageable pageable){
        try{
            Long orgId = null;
            Long positionId = null;
            Date startDate = DateUtils.truncate(new Date(), Calendar.DATE);
            Date deadline = DateUtils.truncate(new Date(), Calendar.DATE);
            if(filter != null){
                if(filter.get("orgId") != null){
                    orgId = (Long) filter.get("orgId");
                } else {
                    orgId = -1l;
                }

                if(filter.get("positionId") != null){
                    positionId = (Long) filter.get("positionId");
                } else {
                    positionId = -1l;
                }

                if(filter.get("startDate") != null){
                    startDate = DateUtil.toDate(String.valueOf(filter.get("startDate")), DateUtil.ISO_8601);
                    startDate = DateUtils.truncate(startDate, Calendar.DATE);
                }else {
                    startDate = DateUtils.truncate(new Date(), Calendar.YEAR);
                }
                if(filter.get("deadline") != null){
                    deadline = DateUtil.toDate(String.valueOf(filter.get("deadline")), DateUtil.ISO_8601);
                    deadline = DateUtils.truncate(deadline, Calendar.DATE);
                }else {
                    deadline = DateUtils.truncate(new Date(), Calendar.YEAR);
                }

            }
            Page<RecruitmentPlanOrder> search = _repo.searchRecruitingPlanOrder(cid,orgId,positionId,startDate,deadline,pageable);
//            List<Map<String,Object>> data = MapperUtils.underscoreToCamelcase(search.getContent());
            Page<Map<String,Object>>resp =new PageImpl(search.getContent(), pageable, search.getTotalElements());
            return ResponseUtils.handlerSuccess(resp);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity updateRecruitingPlanOrder(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable Long id
            , @RequestBody RecruitmentPlanOrderDTO recruitmentPlanOrderDTO){
        try{
            RecruitmentPlanOrderDTO dto = _service.updateRecruitmentPlanOrder(cid,id,recruitmentPlanOrderDTO);
             return ResponseUtils.handlerSuccess(dto);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }

}
