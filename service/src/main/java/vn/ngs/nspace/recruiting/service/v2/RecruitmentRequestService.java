package vn.ngs.nspace.recruiting.service.v2;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;
import vn.ngs.nspace.recruiting.model.RecruitmentRequest;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRequestRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentRequestRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentRequestService {

    private final RecruitmentRequestRepo recruitmentRequestRepo;
    private final RecruitmentPlanRequestRepo recruitmentPlanRequestRepo;

    @Transactional
    public RecruitmentRequestDTO createRecruitmentRequest(Long cid, String uid, RecruitmentRequestDTO dto) {

        validateInput(dto);
        RecruitmentRequest recruitmentRequest = RecruitmentRequest.of(cid, uid, dto);
        recruitmentRequestRepo.save(recruitmentRequest);

        RecruitmentPlanRequest planRequest = RecruitmentPlanRequest.builder()
                .recruitmentPlanId(dto.getRecruitmentPlanId())
                .recruitmentRequestId(recruitmentRequest.getId())
                .build();
        planRequest.setCompanyId(cid);
        planRequest.setUpdateBy(uid);
        planRequest.setCreateBy(uid);
        recruitmentPlanRequestRepo.save(planRequest);

        return dto;
    }

    @Transactional
    public RecruitmentRequestDTO updateRecruitmentRequest(Long cid, String uid, RecruitmentRequestDTO dto) {
        validateInput(dto);
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, dto.getId(), Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new BusinessException("recruitment-request-does-not-exists"));
        recruitmentRequest.setUpdateBy(uid);
        recruitmentRequest.setCode(dto.getCode());
        recruitmentRequest.setOrgId(dto.getOrgId());
        recruitmentRequest.setOrgDeptId(dto.getOrgDeptId());
        recruitmentRequest.setGroupId(dto.getGroupId());
        recruitmentRequest.setPositionId(dto.getPositionId());
        recruitmentRequest.setTitleId(dto.getTitleId());
        recruitmentRequest.setLevelId(dto.getLevelId());
        recruitmentRequest.setContractTypeId(dto.getContractTypeId());
        recruitmentRequest.setQuantity(dto.getQuantity());
        recruitmentRequest.setType(dto.getType());
        recruitmentRequest.setStartDate(dto.getStartDate());
        recruitmentRequest.setEndDate(dto.getEndDate());
        recruitmentRequest.setWorkType(dto.getWorkType());
        recruitmentRequest.setWorkArea(dto.getWorkArea());
        recruitmentRequest.setSalaryType(dto.getSalaryType());
        recruitmentRequest.setFromSalary(dto.getFromSalary());
        recruitmentRequest.setToSalary(dto.getToSalary());
        recruitmentRequest.setCurrencyUnit(dto.getCurrencyUnit());
        recruitmentRequest.setGender(dto.getGender());
        recruitmentRequest.setDegree(dto.getDegree());
        recruitmentRequest.setFromAge(dto.getFromAge());
        recruitmentRequest.setToAge(dto.getToAge());
        recruitmentRequest.setOtherRequirement(dto.getOtherRequirement());
        recruitmentRequestRepo.save(recruitmentRequest);

        List<RecruitmentPlanRequest> recruitmentPlanRequests = recruitmentPlanRequestRepo.findByCompanyIdAndRecruitmentRequestIdAndStatus(cid, recruitmentRequest.getId(), Constants.ENTITY_ACTIVE);
        recruitmentPlanRequests.forEach(recruitmentPlanRequest -> recruitmentPlanRequest.setRecruitmentPlanId(dto.getRecruitmentPlanId()));
        recruitmentPlanRequestRepo.saveAll(recruitmentPlanRequests);

        return dto;
    }

    @Transactional
    public RecruitmentRequest deleteRecruitmentRequest(Long cid, String uid, Long id) {
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, id, Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new BusinessException("recruitment-request-does-not-exists"));
        recruitmentRequest.setStatus(Constants.ENTITY_INACTIVE);
        recruitmentRequest.setUpdateBy(uid);
        recruitmentRequestRepo.save(recruitmentRequest);

        List<RecruitmentPlanRequest> recruitmentPlanRequests = recruitmentPlanRequestRepo.findByCompanyIdAndRecruitmentRequestIdAndStatus(cid, recruitmentRequest.getId(), Constants.ENTITY_ACTIVE);
        recruitmentPlanRequests.forEach(recruitmentPlanRequest -> {
            recruitmentPlanRequest.setStatus(Constants.ENTITY_INACTIVE);
            recruitmentPlanRequest.setUpdateBy(uid);
        });
        recruitmentPlanRequestRepo.saveAll(recruitmentPlanRequests);

        return recruitmentRequest;
    }

    public void validateInput(RecruitmentRequestDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new BusinessException("request-code-null");
        }

        if (!dto.getCode().matches(Constants.RECRUITMENT_REQUEST_CODE_REGEX)) {
            throw new BusinessException("allow-only-letter-number");
        }

        if (dto.getOrgId() == null) {
            throw new BusinessException("unit-null");
        }

        if (dto.getGroupId() == null) {
            throw new BusinessException("position-null");
        }

        if (dto.getTitleId() == null) {
            throw new BusinessException("title-null");
        }

        if (dto.getQuantity() == null) {
            throw new BusinessException("quantity-recruit-null");
        }

        if (StringUtils.isEmpty(dto.getType())) {
            throw new BusinessException("request-type-null");
        }

        if (dto.getStartDate() == null) {
            throw new BusinessException("required-field-null");
        }

        if (StringUtils.isEmpty(dto.getWorkType())) {
            throw new BusinessException("required-field-null");
        }

        if (StringUtils.isEmpty(dto.getWorkArea())) {
            throw new BusinessException("required-field-null");
        }

        if (dto.getWorkArea().length() > 100) {
            throw new BusinessException("max-lenght-allow");
        }

        if (Constants.RANGED_SALARY.equals(dto.getSalaryType())) {
            if (dto.getFromSalary() == null || dto.getFromSalary() <= 0
                    || dto.getFromSalary() >= 1_000_000_000_000_000L) {
                throw new BusinessException("allow-only-number");
            }
        }

        if (Constants.UP_TO_SALARY.equals(dto.getSalaryType())
                || Constants.RANGED_SALARY.equals(dto.getSalaryType())) {

            if (dto.getToSalary() == null || dto.getToSalary() <= 0
                    || dto.getToSalary() >= 1_000_000_000_000_000L) {
                throw new BusinessException("allow-only-number");
            }
        }

        if (dto.getFromAge() == null || dto.getFromAge() >= 100) {
            throw new BusinessException("allow-only-number");
        }

        if (dto.getToAge() == null || dto.getToAge() >= 100) {
            throw new BusinessException("allow-only-number");
        }
    }

}
