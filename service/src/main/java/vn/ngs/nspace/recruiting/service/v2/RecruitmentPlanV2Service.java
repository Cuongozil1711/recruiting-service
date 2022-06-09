package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.RecruitmentPlan;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;
import vn.ngs.nspace.recruiting.model.RecruitmentRequest;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRequestRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentRequestRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecruitmentPlanV2Service {

    private final RecruitmentPlanRepo planRepo;
    private final RecruitmentPlanRequestRepo planRequestRepo;
    private final RecruitmentRequestRepo requestRepo;

    public RecruitmentPlanV2Service(RecruitmentPlanRepo planRepo, RecruitmentPlanRequestRepo planRequestRepo, RecruitmentRequestRepo requestRepo) {
        this.planRepo = planRepo;
        this.planRequestRepo = planRequestRepo;
        this.requestRepo = requestRepo;
    }

    public RecruitmentPlanDTO create(Long cid, String uid, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findByCode(dto.getCode(),cid);
        if (recruitmentPlan != null) {
            throw new BusinessException("duplicate-data-with-this-Code");
        }

        recruitmentPlan = RecruitmentPlan.of(cid,uid,dto);
        recruitmentPlan.setStatus(Constants.ENTITY_ACTIVE);
        recruitmentPlan = planRepo.save(recruitmentPlan);

        List<RecruitmentPlanRequestDTO> recruitmentPlanRequests = dto.getRequestDTOS();

        recruitmentPlanRequests.forEach(
                e -> {
                    RecruitmentPlanRequest planRequest = new RecruitmentPlanRequest();
                    planRequest = RecruitmentPlanRequest.of(uid, cid, e);
                    planRequestRepo.save(planRequest);
                }
        );
        return toDTO(cid,recruitmentPlan);
    }

    public Page<RecruitmentPlanDTO> getPage(Long cid, String uid, RecruitmentFilterRequest request, Pageable pageable) {
        Page<RecruitmentPlan> recruitmentPlans = planRepo.getPage(cid,request.getState(),request.getStartFrom(), request.getStartTo(),request.getEndFrom(), request.getEndTo(),request.getCreatedBy(),request.getSearch(), pageable);
        List<RecruitmentPlanDTO> dtos = toDTOs(cid,recruitmentPlans.getContent());

        return new PageImpl<RecruitmentPlanDTO>(dtos,recruitmentPlans.getPageable(), recruitmentPlans.getTotalElements());
    }

    public RecruitmentPlanDTO getById(Long cid, String uid, Long id) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));
        return toDTO(cid,recruitmentPlan);
    }

    public RecruitmentPlanDTO update(Long cid, String uid, Long id, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));

        MapperUtils.copyWithoutAudit(dto,recruitmentPlan);
        recruitmentPlan.setUpdateBy(uid);
        planRepo.save(recruitmentPlan);

        return toDTO(cid,recruitmentPlan);
    }

    public void delete(Long cid,String uid,  List<Long> ids) {
        planRepo.delete(cid, uid, ids);
    }

    private List<RecruitmentPlanDTO> toDTOs(Long cid, List<RecruitmentPlan> plans) {
        return plans.stream().map(e->toDTO(cid,e)).collect(Collectors.toList());
    }

    private RecruitmentPlanDTO toDTO(Long cid, RecruitmentPlan plan) {
        List<RecruitmentPlanRequest> recruitmentPlanRequests = planRequestRepo.findByCompanyIdAndRecruitmentPlanIdAndStatus(cid,plan.getId(),Constants.ENTITY_ACTIVE);
        List<RecruitmentRequest> recruitmentRequests = requestRepo.getIdIn(cid,recruitmentPlanRequests.stream().map(RecruitmentPlanRequest::getRecruitmentRequestId).collect(Collectors.toList()), Constants.ENTITY_ACTIVE);

        List<RecruitmentRequestDTO> requestDTOS = toListRequestDto(recruitmentRequests);
        List<RecruitmentPlanRequestDTO> planRequestDTOS = requestDTOS.stream()
                .map(e -> {
                    RecruitmentPlanRequestDTO requestDTO = new RecruitmentPlanRequestDTO();
                    requestDTO.setRequestDTO(e);

                    return requestDTO;
                }).collect(Collectors.toList());
        RecruitmentPlanDTO dto = MapperUtils.map(plan,RecruitmentPlanDTO.class);
        dto.setRequestDTOS(planRequestDTOS);

        return  dto;
    }

    private RecruitmentRequestDTO toRequestDto(RecruitmentRequest recruitmentRequest) {
        return MapperUtils.map(recruitmentRequest,RecruitmentRequestDTO.class);
    }

    private List<RecruitmentRequestDTO> toListRequestDto(List<RecruitmentRequest> recruitmentRequests) {
        return recruitmentRequests.stream().map(this::toRequestDto).collect(Collectors.toList());
    }

}
