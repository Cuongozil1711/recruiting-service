package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.recruiting.repo.JobRequirementRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.JobRequirementDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

@Service
@Transactional
@Log4j2

public class JobRequirementService {
    private final JobRequirementRepo _repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public JobRequirementService(JobRequirementRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        _repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }


    public void valid(JobRequirementDTO dto){
        if (StringUtils.isEmpty(String.valueOf(dto.getCode() == null))){
            throw new BusinessException("invalid-code");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getTitle() == null))){
            throw new BusinessException("title-is-empty");
        }
        if(dto.getTitleId() == null){
            throw new BusinessException("invalid-titleId");
        }
        if(dto.getPositionId() == null){
            throw new BusinessException("invalid-position");
        }
        if(dto.getLevelId() == null){
            throw new BusinessException("invalid-level");
        }
        if(dto.getQuantity() == null){
            throw new BusinessException("invalid-quantity");
        }
        if(dto.getIndustryId() == null){
            throw new BusinessException("invalid-industryId");
        }
        if(dto.getCollaborationType() == null){
            throw new BusinessException("invalid-collaborationType");
        }
        if(dto.getMinExperience() == null){
            throw new BusinessException("invalid-experience");
        }
        if (dto.getGender() == null){
            throw new BusinessException("invalid-gender");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getSalaryRange() == null))){
            throw new BusinessException("invalid-salaryRange");
        }
        if(dto.getSalaryFrom() == null){
            throw new BusinessException("invalid-salaryFrom");
        }
        if(dto.getSalaryTo() == null){
            throw new BusinessException("invalid-salaryTo");
        }
        if(dto.getCurrencyId() == null){
            throw new BusinessException("invalid-currency");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getLocation() == null))){
            throw new BusinessException("invalid-location");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getDescription() == null))){
            throw new BusinessException("invalid-description");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getJobRequirement() == null))){
            throw new BusinessException("invalid-jobRequirement");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getSkillRequirement() == null))){
            throw new BusinessException("invalid-skillRequirement");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getBenefitDescription() == null))){
            throw new BusinessException("invalid-benefitDescription");
        }
        if(dto.getReceiptDeadline() == null){
            throw new BusinessException("invalid-receiptDeadline");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getReceiptName() == null))){
            throw new BusinessException("invalid-receiptname");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getReceiptPhone() == null))){
            throw new BusinessException("invalid-phone");
        }
        if(StringUtils.isEmpty(String.valueOf(dto.getReceiptEmail() == null))){
            throw new BusinessException("invalid-email");
        }


    }
    public JobRequirement create(Long cid, String uid, JobRequirementDTO jobRequirementDTO) {
        valid(jobRequirementDTO);
        JobRequirement jobRequirement = JobRequirement.of(cid,uid,jobRequirementDTO);
        jobRequirement.setCompanyId(cid);
        jobRequirement.setCreateBy(uid);
        jobRequirement = _repo.save(jobRequirement);
        return jobRequirement;
    }

    public JobRequirementDTO update(Long cid, String uid, Long id, JobRequirementDTO dto) {
        valid(dto);
        JobRequirement curr = _repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
        MapperUtils.copyWithoutAudit(dto,curr);
        curr.setUpdateBy(uid);
        curr = _repo.save(curr);

        return toDTO(curr);

    }

    public List<JobRequirementDTO> toDTOs(Long cid, String uid, List<JobRequirement> objs) {
        List<JobRequirementDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();

        objs.forEach(obj -> {
            if (obj.getPositionId() != null) {
                categoryIds.add(obj.getPositionId());
            }
            if (obj.getTitleId() != null) {
                categoryIds.add(obj.getTitleId());
            }
            if (obj.getLevelId() != null) {
                categoryIds.add(obj.getLevelId());
            }
            if(obj.getCurrencyId() != null){
                categoryIds.add(obj.getCurrencyId());
            }
            if (obj.getIndustryId() != null){
                categoryIds.add(obj.getIndustryId());
            }
            if(obj.getReceiptName() != null){
                categoryIds.add(obj.getReceiptName());
            }


            dtos.add(toDTO(obj));
        });

        List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for (JobRequirementDTO dto : dtos) {
            if (dto.getPositionId() != null) {
                dto.setPositionObj(mapCategory.get(dto.getPositionId()));
            }
            if (dto.getTitleId() != null) {
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
            if (dto.getLevelId() != null) {
                dto.setLevelObj(mapCategory.get(dto.getLevelId()));
            }
            if (dto.getCurrencyId() != null){
                dto.setCurrencyObj(mapCategory.get(dto.getCurrencyId()));
            }
            if (dto.getIndustryId() != null){
                dto.setIndustryObj(mapCategory.get(dto.getIndustryId()));
            }
            if (dto.getReceiptName() != null){
                EmployeeDTO emp = employees.stream().filter(e -> CompareUtil.compare(e.getId(),dto.getReceiptName())).findAny().orElse(new EmployeeDTO());
                dto.setReceiptNameObj(emp);
            }

        }
        return dtos;

    }



    public JobRequirementDTO toDTO(JobRequirement jobRequirement ){
        JobRequirementDTO dto = MapperUtils.map(jobRequirement, JobRequirementDTO.class);
        return dto;
    }
}
