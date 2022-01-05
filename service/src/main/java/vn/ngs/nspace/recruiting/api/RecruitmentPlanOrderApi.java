package vn.ngs.nspace.recruiting.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.time.DateUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.service.RecruitmentPlanOrderService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

@RestController
@RequestMapping("recruiting-plan-order")
@RequiredArgsConstructor
public class RecruitmentPlanOrderApi {
    private final RecruitmentPlanOrderService _service;
    private final RecruitmentPlanOrderRepo _repo;


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
            Set<Long> orgIds = new HashSet<>();
            Set<Long> positionIds = new HashSet<>();
            Date fromDate = DateUtils.truncate(new Date(), Calendar.DATE);
            Date toDate = DateUtils.truncate(new Date(), Calendar.DATE);
            if(filter != null){
                if(filter.get("orgIds") != null){
                    List ids = (List) filter.get("orgIds");
                    if (ids != null && !ids.isEmpty()) {
                        for (Object id : ids) {
                            orgIds.add(Long.valueOf(String.valueOf(id)));
                        }
                    } else {
                        orgIds.add(0L);
                    }
                } else {
                    orgIds.add(0L);
                }

                if(filter.get("positionIds") != null){
                    List ids = (List) filter.get("positionIds");
                    if (ids != null && !ids.isEmpty()) {
                        for (Object id : ids) {
                            orgIds.add(Long.valueOf(String.valueOf(id)));
                        }
                    } else {
                        positionIds.add(0L);
                    }
                } else {
                    positionIds.add(0L);
                }

                if(filter.get("fromDate") != null){
                    fromDate = DateUtil.toDate(String.valueOf(filter.get("fromDate")), DateUtil.ISO_8601);
                    fromDate = DateUtils.truncate(fromDate, Calendar.DATE);
                }
                if(filter.get("toDate") != null){
                    toDate = DateUtil.toDate(String.valueOf(filter.get("toDate")), DateUtil.ISO_8601);
                    toDate = DateUtils.truncate(toDate, Calendar.DATE);
                }

            }
            Page<Map<String,Object>> search = _repo.searchRecruitingPlanOrder(cid,orgIds,positionIds,fromDate,toDate,pageable);
            List<Map<String,Object>> data = MapperUtils.underscoreToCamelcase(search.getContent());
            Page<Map<String,Object>>resp =new PageImpl(data, pageable, search.getTotalElements());
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
