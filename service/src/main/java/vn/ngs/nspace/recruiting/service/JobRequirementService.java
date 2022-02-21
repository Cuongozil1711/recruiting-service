package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.recruiting.repo.JobRequirementRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.JobRequirementDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.task.core.data.UserData;

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
        if (StringUtils.isEmpty(dto.getCode())){
            throw new BusinessException("invalid-code");
        }
        if(StringUtils.isEmpty(dto.getTitle())) {
            throw new BusinessException("title-is-empty");
        }
        if(dto.getTitleId() == null){
            throw new BusinessException("invalid-titleId");
        }
//        if(dto.getPositionId() == null){
//            throw new BusinessException("invalid-position");
//        }
//        if(dto.getLevelId() == null){
//            throw new BusinessException("invalid-level");
//        }
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
        if(dto.getMinExperienceUnit() == null){
            throw new BusinessException("invalid-MinExperienceUnit");
        }
        if (dto.getGender() == null){
            throw new BusinessException("invalid-gender");
        }
        if(StringUtils.isEmpty(dto.getSalaryRange() )){
            throw new BusinessException("invalid-salaryRange");
        }
        if(dto.getSalaryFrom() == null){
            throw new BusinessException("invalid-salaryFrom");
        }
//        if(dto.getSalaryTo() == null){
//            throw new BusinessException("invalid-salaryTo");
//        }
        if(dto.getCurrencyId() == null){
            throw new BusinessException("invalid-currency");
        }
        if(StringUtils.isEmpty(dto.getLocation())){
            throw new BusinessException("invalid-location");
        }
        if(StringUtils.isEmpty(dto.getDescription())){
            throw new BusinessException("invalid-description");
        }
        if(StringUtils.isEmpty(dto.getJobRequirement() )){
            throw new BusinessException("invalid-jobRequirement");
        }
        if(StringUtils.isEmpty(dto.getSkillRequirement())){
            throw new BusinessException("invalid-skillRequirement");
        }
        if(StringUtils.isEmpty(dto.getBenefitDescription())){
            throw new BusinessException("invalid-benefitDescription");
        }
        if(dto.getReceiptDeadline() == null){
            throw new BusinessException("invalid-receiptDeadline");
        }
        if(dto.getReceiptName() == null){
            throw new BusinessException("invalid-receiptname");
        }
        if(StringUtils.isEmpty(dto.getReceiptPhone())){
            throw new BusinessException("invalid-phone");
        }
        if(StringUtils.isEmpty(dto.getReceiptEmail())){
            throw new BusinessException("invalid-email");
        }


    }
    public JobRequirementDTO create(Long cid, String uid, JobRequirementDTO jobRequirementDTO) {
        valid(jobRequirementDTO);
        JobRequirement exist = _repo.findByCompanyIdAndCodeAndStatus(cid, jobRequirementDTO.getCode(),Constants.ENTITY_ACTIVE).orElse(new JobRequirement());
        if (!exist.isNew()){
            throw new BusinessException("duplicate-data-with-code");
        }

        JobRequirement jobRequirement = JobRequirement.of(cid,uid,jobRequirementDTO);
        jobRequirement.setCompanyId(cid);
        jobRequirement.setCreateBy(uid);
        jobRequirement = _repo.save(jobRequirement);
        return toDTO(jobRequirement);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            JobRequirement jr = _repo.findByCompanyIdAndId(cid, i).orElse(new JobRequirement());
            if(!jr.isNew()){
                jr.setUpdateBy(uid);
                jr.setModifiedDate(new Date());
                jr.setStatus(Constants.ENTITY_INACTIVE);

                _repo.save(jr);
            }
        });
    }

    public JobRequirementDTO update(Long cid, String uid, Long id, JobRequirementDTO dto) {
        valid(dto);
        JobRequirement curr = _repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
        MapperUtils.copyWithoutAudit(dto,curr);
        curr.setUpdateBy(uid);
        curr = _repo.save(curr);
        try{
            _repo.findByCompanyIdAndCodeAndStatus(cid, dto.getCode(),Constants.ENTITY_ACTIVE).orElse(new JobRequirement());

        }catch (IncorrectResultSizeDataAccessException ex){
            throw new BusinessException("duplicate-data-with-code");
        }

        return toDTO(curr);

    }

    public List<JobRequirementDTO> toDTOs(Long cid, String uid, List<JobRequirement> objs) {
        List<JobRequirementDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();

        objs.forEach(obj -> {
            if(!StringUtils.isEmpty(obj.getCreateBy())){
                userIds.add(obj.getCreateBy());
            }
//            if (obj.getPositionId() != null) {
//                categoryIds.add(obj.getPositionId());
//            }
//            if (obj.getTitleId() != null) {
//                categoryIds.add(obj.getTitleId());
//            }

            if (obj.getTitleId() != null) {
                categoryIds.add(obj.getTitleId());
            }
            if (obj.getGender() != null){
                categoryIds.add(obj.getGender());
            }
            if(obj.getCurrencyId() != null){
                categoryIds.add(obj.getCurrencyId());
            }
            if (obj.getIndustryId() != null){
                obj.getIndustryId().forEach(industry -> {
                    categoryIds.add(Long.valueOf(industry));
                });
            }
            if(obj.getReceiptName() != null){
                empIds.add(obj.getReceiptName());
            }


            dtos.add(toDTO(obj));
        });

        List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);

        for (JobRequirementDTO dto : dtos) {
//            if (dto.getPositionId() != null) {
//                dto.setPositionObj(mapCategory.get(dto.getPositionId()));
//            }

            if (dto.getGender() != null){
                dto.setGenderObj(mapCategory.get(dto.getGender()));
            }
            if (dto.getTitleId() != null) {
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
//            if (dto.getLevelId() != null) {
//                dto.setLevelObj(mapCategory.get(dto.getLevelId()));
//            }
            if (dto.getCurrencyId() != null){
                dto.setCurrencyObj(mapCategory.get(dto.getCurrencyId()));
            }
            if (dto.getIndustryId() != null){
                List<Map<String,Object>> industryIds = new ArrayList<>();
                dto.getIndustryId().stream().forEach(i -> {
                    if(!StringUtils.isEmpty(i)){
                        industryIds.add(mapCategory.get(Long.valueOf(i)));
                    }
                });
                dto.setIndustryObj(industryIds);
            }
            if (dto.getReceiptName() != null){
                EmployeeDTO emp = employees.stream().filter(e -> CompareUtil.compare(e.getId(),dto.getReceiptName())).findAny().orElse(new EmployeeDTO());
                dto.setReceiptNameObj(emp);
            }
            if(!StringUtils.isEmpty(dto.getCreateBy())){
                dto.setCreateByObj((Map<String, Object>) mapperUser.get(dto.getCreateBy()));
            }
        }
        return dtos;

    }



    public JobRequirementDTO toDTO(JobRequirement jobRequirement ){
        JobRequirementDTO dto = MapperUtils.map(jobRequirement, JobRequirementDTO.class);
        return dto;
    }
}
