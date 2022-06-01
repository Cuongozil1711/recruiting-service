package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.RecruitmentPlan;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;

import javax.transaction.Transactional;
import java.rmi.activation.ActivationID;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecruitmentPlanV2Service {

    private final RecruitmentPlanRepo planRepo;

    public RecruitmentPlanV2Service(RecruitmentPlanRepo planRepo) {
        this.planRepo = planRepo;
    }

    public RecruitmentPlanDTO create(Long cid, String uid, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findByCode(dto.getCode(),cid);
        if (recruitmentPlan != null) {
            throw new BusinessException("");
        }

        recruitmentPlan = RecruitmentPlan.of(cid,uid,dto);
        recruitmentPlan.setStatus(Constants.ENTITY_ACTIVE);
        recruitmentPlan = planRepo.save(recruitmentPlan);

        return toDTO(recruitmentPlan);
    }

    public Page<RecruitmentPlanDTO> getPage(Long cid, String uid, RecruitmentFilterRequest request, Pageable pageable) {
        Page<RecruitmentPlan> recruitmentPlans = planRepo.getPage(cid,request.getState(),request.getStartFrom(), request.getStartTo(),request.getEndFrom(), request.getEndTo(),request.getCreatedBy(),request.getSearch(), pageable);
        List<RecruitmentPlanDTO> dtos = toDTOs(recruitmentPlans.getContent());

        return new PageImpl<RecruitmentPlanDTO>(dtos,recruitmentPlans.getPageable(), recruitmentPlans.getTotalElements());
    }

    public RecruitmentPlanDTO getById(Long cid, String uid, Long id) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));
        return toDTO(recruitmentPlan);
    }

    public RecruitmentPlanDTO update(Long cid, String uid, Long id, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));

        MapperUtils.copyWithoutAudit(dto,recruitmentPlan);
        recruitmentPlan.setUpdateBy(uid);
        planRepo.save(recruitmentPlan);

        return toDTO(recruitmentPlan);
    }

    public void delete(Long cid,String uid,  List<Long> ids) {
        planRepo.delete(cid, uid, ids);
    }

    private List<RecruitmentPlanDTO> toDTOs(List<RecruitmentPlan> plans) {
        return plans.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private RecruitmentPlanDTO toDTO(RecruitmentPlan plan) {
        return MapperUtils.map(plan,RecruitmentPlanDTO.class);
    }
}
