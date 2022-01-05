package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

@Service
@Transactional
@Log4j2
public class RecruitmentPlanOrderService {
    private final RecruitmentPlanOrderRepo repo;
    private final ExecuteHcmService _hcmService;

    public RecruitmentPlanOrderService(RecruitmentPlanOrderRepo repo, ExecuteHcmService hcmService) {
        this.repo = repo;
        _hcmService = hcmService;
    }
    public void valid(RecruitmentPlanOrderDTO dto){
        if(dto.getCode() == null){
            throw new BusinessException("invalid-code");
        }
        if (dto.getType() == null){
            throw new BusinessException("invalid-type");
        }
        if (dto.getSolutionSuggestType() == null){
            throw new BusinessException("invalid-solutionSuggestType");
        }
        if (dto.getPositionId() == null){
            throw new BusinessException("invalid-positon");
        }
        if (dto.getLevelId() == null){
            throw new BusinessException("invalid-level");
        }
        if (dto.getPic() == null){
            throw new BusinessException("invalid-pic");
        }
        if (dto.getQuantity() == null){
            throw new BusinessException("invalid-quantity");
        }
        if (dto.getOrgId() == null){
            throw new BusinessException("invalid-org");
        }
        if (dto.getStartDate() == null){
            throw new BusinessException("invalid-startDate");
        }
        if (dto.getDeadline() == null){
            throw new BusinessException("invalid-deadline");
        }
    }

    public RecruitmentPlanOrderDTO create(Long cid, String uid, RecruitmentPlanOrderDTO dto) throws Exception {
        valid(dto);
        RecruitmentPlanOrder recruitmentPlanOrder = RecruitmentPlanOrder.of(cid, uid, dto);

        repo.save(recruitmentPlanOrder);
        return MapperUtils.map(recruitmentPlanOrder,dto);
    }

    public RecruitmentPlanOrderDTO updateRecruitmentPlanOrder(Long cid, Long id, RecruitmentPlanOrderDTO recruitmentPlanOrderDTO) {
        valid(recruitmentPlanOrderDTO);
        RecruitmentPlanOrder curr = repo.findByCompanyIdAndId(cid, id).orElse(new RecruitmentPlanOrder());
        MapperUtils.copy(recruitmentPlanOrderDTO,curr,"version", "status", "createBy", "createDate", "updateBy", "modifiedDate", "id", "companyId", "categoryType");

        repo.save(curr);
        return MapperUtils.map(curr,RecruitmentPlanOrderDTO.class);
    }
}
