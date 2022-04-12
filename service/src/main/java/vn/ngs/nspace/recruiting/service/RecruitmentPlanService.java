package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;

@Service
@Transactional
@Log4j2
public class RecruitmentPlanService {
    private final RecruitmentPlanOrderRepo repoOder;
    private final RecruitmentPlanRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final JobApplicationRepo _repoJob;


    public RecruitmentPlanService(RecruitmentPlanOrderRepo repoOder, RecruitmentPlanRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService, JobApplicationRepo repoJob1) {
        this.repo =  repo;
        this.repoOder = repoOder;
        _hcmService = hcmService;
        _configService = configService;
        _repoJob = repoJob1;
    }

    public void valid(RecruitmentPlanOrderDTO dto){

    }
    public RecruitmentPlanDTO create(Long cid, String uid, RecruitmentPlanDTO dto) throws BusinessException {
        RecruitmentPlan obj = RecruitmentPlan.of(cid, uid, dto);
        obj.setCompanyId(cid);
        obj.setCreateBy(uid);
        obj.setUpdateBy(uid);
        obj.setStatus(Constants.ENTITY_ACTIVE);

        obj = repo.save(obj);
        //create detail
        if (dto.getRecruitmentPlanDetails() != null && !dto.getRecruitmentPlanDetails().isEmpty()) {
            for (RecruitmentPlanOrderDTO detailDTO : dto.getRecruitmentPlanDetails()) {
                detailDTO.setPlanId(obj.getId());
                createItem(cid, uid, detailDTO);
            }
        }
        return dto;
    }

    private void createItem(long cid, String uid, RecruitmentPlanOrderDTO itemDTO) {

        // create template
        RecruitmentPlanOrder detail = RecruitmentPlanOrder.of(cid, uid, itemDTO);
        detail.setCompanyId(cid);
        detail.setCreateBy(uid);
        detail.setUpdateBy(uid);
        detail.setStatus(Constants.ENTITY_ACTIVE);

        detail = repoOder.save(detail);
    }

    public RecruitmentPlanDTO update(long cid, String uid, Long planId, RecruitmentPlanDTO dto) {
        RecruitmentPlan curr = repo.findByCompanyIdAndId(cid, planId).orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class, planId));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);

        if (dto.getRecruitmentPlanDetails() != null && !dto.getRecruitmentPlanDetails().isEmpty()) {
            for (RecruitmentPlanOrderDTO itemDTO : dto.getRecruitmentPlanDetails()) {
                if (CompareUtil.compare(dto.getStatus(), Constants.ENTITY_INACTIVE)) {
                    itemDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                itemDTO.setPlanId(dto.getId());
                updateItem(cid, uid, itemDTO.getId(), itemDTO);
            }
        }

        curr = repo.save(curr);

        return dto;
    }

    private void updateItem(long cid, String uid, Long detailId, RecruitmentPlanOrderDTO detailDTO) {
        if(detailDTO.getId() != null && detailDTO.getId() != 0l){
            RecruitmentPlanOrder curr = repoOder.findByCompanyIdAndId(cid, detailId).orElseThrow(() -> new EntityNotFoundException(CostDetail.class, detailId));
            MapperUtils.copyWithoutAudit(detailDTO, curr);
            curr.setUpdateBy(uid);
            curr = repoOder.save(curr);
        }else{
            createItem(cid, uid, detailDTO);
        }
    }

    public Page<RecruitmentPlan> search(Long cid,String uid, Map<String, Object> payload, Pageable pageable) throws Exception {

        String dmin="2000-01-01T00:00:00+0700";
        String dmax="3000-01-01T00:00:00+0700";
        Date startDateTo=null;
        Date startDateFrom=null;
        Date endDateTo=null;
        Date endDateFrom=null;
        List<String> states = new ArrayList<>();
        String search = MapUtils.getString(payload, "search","#");
        if (payload.containsKey("state")){
            states = (List<String>) payload.get("state");
        }
        //String state = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "state","#");
        else states.add("#");
        if(payload.get("startDateTo")!=null)
         startDateTo = DateUtil.toDate(MapUtils.getString(payload, "startDateTo", dmax), "yyyy-MM-dd'T'HH:mm:ss");
        else
            startDateTo = DateUtil.toDate(dmax,"yyyy-MM-dd'T'HH:mm:ss");
        if(payload.get("startDateFrom")!=null)
         startDateFrom = DateUtil.toDate(MapUtils.getString(payload, "startDateFrom", dmin), "yyyy-MM-dd'T'HH:mm:ss");
        else
            startDateFrom = DateUtil.toDate(dmin,"yyyy-MM-dd'T'HH:mm:ss");
        if(payload.get("endDateTo")!=null)
            endDateTo = DateUtil.toDate(MapUtils.getString(payload, "endDateTo", dmax), "yyyy-MM-dd'T'HH:mm:ss");
        else
            endDateTo = DateUtil.toDate(dmax,"yyyy-MM-dd'T'HH:mm:ss");
        if(payload.get("endDateFrom")!=null)
            endDateFrom = DateUtil.toDate(MapUtils.getString(payload, "endDateFrom", dmin), "yyyy-MM-dd'T'HH:mm:ss");
        else
            endDateFrom = DateUtil.toDate(dmin,"yyyy-MM-dd'T'HH:mm:ss");
        Page<RecruitmentPlan> recruitmentPlansState = repo.filter(cid,states,startDateFrom,startDateTo,endDateFrom,endDateTo,pageable);
        List<RecruitmentPlanDTO> result = new ArrayList<>();
        List<RecruitmentPlan> _a = recruitmentPlansState.getContent();

        if (recruitmentPlansState.getContent() != null && !recruitmentPlansState.getContent().isEmpty()) {
            result = toDTOs(cid,uid,recruitmentPlansState.getContent());
        }
        return new PageImpl(result, recruitmentPlansState.getPageable(), recruitmentPlansState.getTotalElements());
    }
    public Page<RecruitmentPlan> searchOder(Long cid, Map<String, Object> payload, Pageable pageable) throws Exception {

        String dmin="2000-01-01T00:00:00+0700";
        String dmax="3000-01-01T00:00:00+0700";
        Date deadlineTo=null;
        Date deadlineFrom=null;
        List<String> states = Arrays.asList("#");

        String solutionSuggestType = MapUtils.getString(payload, "solutionSuggestType","#");
        String type = MapUtils.getString(payload, "type","#");
        Long planId = Long.parseLong(MapUtils.getString(payload, "planId","-1"));
        Long pic = Long.parseLong(MapUtils.getString(payload, "pic","-1"));
        Long room = Long.parseLong(MapUtils.getString(payload, "room","-1"));
        Long orgId = Long.parseLong(MapUtils.getString(payload, "orgId","-1"));
        Long titleId = Long.parseLong(MapUtils.getString(payload, "titleId","-1"));
        Long positionId = Long.parseLong(MapUtils.getString(payload, "positionId","-1"));
        if (payload.get("states") != null && !((List<String>) payload.get("states")).isEmpty()){
            states = (List<String>) payload.get("states");
        }
        //String state = vn.ngs.nspace.lib.utils.MapUtils.getString(payload, "state","#");
        if(payload.get("deadlineTo")!=null)
            deadlineTo = DateUtil.toDate(MapUtils.getString(payload, "startDateTo", dmax), "yyyy-MM-dd'T'HH:mm:ss");
        else
            deadlineTo = DateUtil.toDate(dmax,"yyyy-MM-dd'T'HH:mm:ss");
        if(payload.get("startDateFrom")!=null)
            deadlineFrom = DateUtil.toDate(MapUtils.getString(payload, "startDateFrom", dmin), "yyyy-MM-dd'T'HH:mm:ss");
        else
            deadlineFrom = DateUtil.toDate(dmin,"yyyy-MM-dd'T'HH:mm:ss");


        Page<RecruitmentPlanOrder> recruitmentPlansState = repoOder.searchByFilter(cid,planId,states,deadlineFrom,deadlineTo,orgId,pic,room,positionId,titleId,solutionSuggestType,type,pageable);
        List<RecruitmentPlanOrderDTO> result = new ArrayList<>();

        if (recruitmentPlansState.getContent() != null && !recruitmentPlansState.getContent().isEmpty()) {
            result = fromOder(recruitmentPlansState.getContent());
        }
        return new PageImpl(result, recruitmentPlansState.getPageable(), recruitmentPlansState.getTotalElements());
    }
    public List<RecruitmentPlanDTO> toDTOs(Long cid, String uid, List<RecruitmentPlan> objs){
        List<RecruitmentPlanDTO> dtos = new ArrayList<>();
        Set<Long> planId = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> positionIds = new HashSet<>();
        Set<Long> leverId = new HashSet<>();
        Set<Long> titleIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();
        Set<Long> roomIds = new HashSet<>();
        Set<String> createBy = new HashSet<>();

        Set<Long> categoryIds = new HashSet<>();
        objs.forEach(o -> {
            planId.add(o.getId());
            createBy.add(o.getCreateBy());
        });

        List<RecruitmentPlanOrder> items = repoOder.findByCompanyIdAndPlanIdInAndStatus(cid, planId, Constants.ENTITY_ACTIVE);
        items.forEach(e-> {
            if(e.getOrgId() != null){
                orgIds.add(e.getOrgId());
            }
            if(e.getPositionId() != null){
                positionIds.add(e.getPositionId());
            }
            if(e.getTitleId() != null){
                titleIds.add(e.getTitleId());
            }
            if(e.getLevelId() != null){
                leverId.add(e.getLevelId());
            }
            if(e.getPic() != null){
                empIds.add(e.getPic());
            }
            if(e.getRoom() !=null){
                roomIds.add(e.getRoom());
            }


        });

        for(RecruitmentPlan obj : objs){
            RecruitmentPlanDTO o = toDTO(obj);
            List<Map<String,Object>> _sumQuanity = repoOder.sumQuanity(cid);


            // tinh tong ung vien can tuyen
            for (Map<String, Object> objOfSum : _sumQuanity) {
                Long _planId = parseLong(objOfSum.get("plan_id").toString());
                Long _sum = Long.parseLong(objOfSum.get("sum").toString());
                if (obj.getId().equals(_planId)) {
                    o.setSumQuanity(_sum);
                }
            };

            List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
            List<OrgResp> orgs = _hcmService.getOrgResp(uid, cid, orgIds);
            BaseResponse<Map<String, Object>> objUser = _hcmService.getInfoUserByUserId(uid, cid);
            Map<Long, Map<String, Object>> mapPossion = _configService.getCategoryByIds(uid, cid, positionIds);
            Map<Long, Map<String, Object>> MapTilte = _configService.getCategoryByIds(uid, cid, titleIds);
            Map<Long, Map<String, Object>> MapLevel = _configService.getCategoryByIds(uid, cid, leverId);
            o.setCreatByObj(objUser.getData());
            List<RecruitmentPlanOrderDTO> itemDTOs = new ArrayList<>();
            items.stream().filter(i -> CompareUtil.compare(i.getPlanId(), obj.getId()))
                    .collect(Collectors.toList()).stream().forEach(i -> {
                        RecruitmentPlanOrderDTO itemDTO = MapperUtils.map(i, RecruitmentPlanOrderDTO.class);
                        if (itemDTO.getTitleId() != null) {
                            itemDTO.setTitleObj(MapTilte.get(itemDTO.getTitleId()));
                        }
                        if (itemDTO.getLevelId() != null) {
                            itemDTO.setLevelObj(MapLevel.get(itemDTO.getLevelId()));
                        }
                        if (itemDTO.getPositionId() != null) {
                            itemDTO.setPositionObj(mapPossion.get(itemDTO.getPositionId()));
                        }
                        if (itemDTO.getOrgId() != null) {
                            OrgResp org = orgs.stream().filter(b -> CompareUtil.compare(b.getId(), itemDTO.getOrgId())).findAny().orElse(new OrgResp());
                            itemDTO.setOrgResp(org);
                        }
                        if (itemDTO.getPic() != null) {
                            EmployeeDTO emp = employees.stream().filter(e -> CompareUtil.compare(e.getId(), itemDTO.getPic())).findAny().orElse(new EmployeeDTO());
                            itemDTO.setPicObj(emp);
                        }
                        //count all recruting
                        Long planIds = obj.getId();
                        List<Map<String,Object>> _getAllPlanId = repoOder.getAllPlanId(cid,planIds);
                        for(Map<String,Object> objAllPlanid : _getAllPlanId){
                            Long planOderId = Long.parseLong(objAllPlanid.get("id").toString());
                            Long possion_Id = Long.parseLong(objAllPlanid.get("position_id").toString());
                            Long orgId = Long.parseLong(objAllPlanid.get("org_id").toString());

                            List<Map<String,Object>> _countStaff = _repoJob.countStaff(cid,possion_Id,orgId,planOderId);
                            List<Map<String,Object>> _countAll = _repoJob.countAll(cid,possion_Id,orgId,planOderId);
                            Long sumRecrutingInPlan = Long.valueOf(0);
                            Long sumRecruting = Long.valueOf(0);
                            for (Map<String,Object> objCount : _countStaff) {
                                Long sumRecrutingInOder = Long.parseLong(objCount.get("count").toString());
                                itemDTO.setCountRecruting(sumRecrutingInOder);
                                sumRecrutingInPlan += sumRecrutingInOder;
                            }
                            for (Map<String,Object> objCount : _countAll) {
                                Long sumRecrutingAll = Long.parseLong(objCount.get("count").toString());
                                itemDTO.setCountAllRecruting(sumRecrutingAll);
                                sumRecruting += sumRecrutingAll;
                            }
                            o.setSumRecruting(sumRecrutingInPlan);
                            o.setSumRecrutingAll(sumRecruting);
                        }

                        itemDTOs.add(itemDTO);
                    });

            o.setRecruitmentPlanDetails(itemDTOs);
            dtos.add(o);
        }
        return dtos;
    }

    //service sum all

    public RecruitmentPlanDTO sumAll(Long cid, String uid){
        Map<String,Object> sumAll = repo.sumAll(cid);

        RecruitmentPlan recruitmentPlan = new RecruitmentPlan();
        recruitmentPlan.setTotalSumQuanity(Long.parseLong(sumAll.get("sum_quanity").toString()));
        recruitmentPlan.setTotalRecruted(Long.parseLong(sumAll.get("sum_recruting").toString()));
        recruitmentPlan.setTotalSumRecrutingAll(Long.parseLong(sumAll.get("sum_recruting_all").toString()));

        return RecruitmentPlan.toDTO(recruitmentPlan);
    }

    /* convert list model object to DTO before response */

    public List<RecruitmentPlanOrderDTO> fromOder(List<RecruitmentPlanOrder> objs) {
        return objs.stream().map(obj -> obj.toDTOOder()).collect(Collectors.toList());
    }

    public RecruitmentPlanDTO toDTOWithObj(Long cid, String uid, RecruitmentPlan obj) {
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }
    public RecruitmentPlanOrderDTO toDTOOder(RecruitmentPlanOrderDTO obj){
        return MapperUtils.map(obj, RecruitmentPlanOrderDTO.class);
    }
    public RecruitmentPlanDTO toDTO(RecruitmentPlan obj){
        return MapperUtils.map(obj, RecruitmentPlanDTO.class);
    }
}
