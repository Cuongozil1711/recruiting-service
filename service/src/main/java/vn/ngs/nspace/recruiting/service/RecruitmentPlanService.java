package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class RecruitmentPlanService {
    private final RecruitmentPlanOrderRepo repoOder;
    private final RecruitmentPlanRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;


    public RecruitmentPlanService(RecruitmentPlanOrderRepo repoOder, RecruitmentPlanRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo =  repo;
        this.repoOder = repoOder;
        _hcmService = hcmService;
        _configService = configService;
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
    public List<RecruitmentPlanDTO> toDTOs(Long cid, String uid, List<RecruitmentPlan> objs){
        List<RecruitmentPlanDTO> dtos = new ArrayList<>();
        Set<Long> planId = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> positionIds = new HashSet<>();
        Set<Long> leverId = new HashSet<>();
        Set<Long> titleIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();
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


        });

        for(RecruitmentPlan obj : objs){
            RecruitmentPlanDTO o = toDTO(obj);


            List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
            List<OrgResp> orgs = _hcmService.getOrgResp(uid, cid, orgIds);
            Map<Long, Map<String, Object>> mapPossion = _configService.getCategoryByIds(uid, cid, positionIds);
            Map<Long, Map<String, Object>> MapTilte = _configService.getCategoryByIds(uid, cid, titleIds);
            Map<Long, Map<String, Object>> MapLevel = _configService.getCategoryByIds(uid, cid, leverId);

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
                        itemDTOs.add(itemDTO);
                    });

            o.setRecruitmentPlanDetails(itemDTOs);
            dtos.add(o);
        }
        return dtos;
    }

    public RecruitmentPlanDTO toDTO(RecruitmentPlan obj){
        return MapperUtils.map(obj, RecruitmentPlanDTO.class);
    }
}
