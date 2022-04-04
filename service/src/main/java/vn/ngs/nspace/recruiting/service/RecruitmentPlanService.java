package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.CostDetail;
import vn.ngs.nspace.recruiting.model.RecruitmentPlan;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

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








        public RecruitmentPlanDTO toDTO(RecruitmentPlan obj){
        return MapperUtils.map(obj, RecruitmentPlanDTO.class);
    }
}
