package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.request.EmployeeReq;
import vn.ngs.nspace.hcm.share.dto.response.EmployeeResp;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;

import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.share.dto.EmployeeRecruitingReq;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.data.UserData;

import java.util.*;

@Service
@Transactional
@Log4j2

public class JobApplicationService {
    private final JobApplicationRepo _repo;
    private final CandidateRepo _candidateRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public JobApplicationService(JobApplicationRepo repo, CandidateRepo candidateRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        _repo = repo;
        _candidateRepo = candidateRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(JobApplicationDTO dto){
    }

    public JobApplicationDTO create(Long cid, String uid, JobApplicationDTO request) {
        valid(request);
        JobApplication obj = JobApplication.of(cid, uid, request);
        obj.setCompanyId(cid);
        obj.setCreateBy(uid);
        obj = _repo.save(obj);
        return toDTO(obj);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            JobApplication jr = _repo.findByCompanyIdAndId(cid, i).orElse(new JobApplication());
            if(!jr.isNew()){
                jr.setUpdateBy(uid);
                jr.setModifiedDate(new Date());
                jr.setStatus(Constants.ENTITY_INACTIVE);

                _repo.save(jr);
            }
        });
    }

    public JobApplicationDTO update(Long cid, String uid, Long id, JobApplicationDTO request) {
        valid(request);
        JobApplication curr = _repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr = _repo.save(curr);

        return toDTO(curr);
    }

    public EmployeeDTO createEmployee(Long cid, String uid, Long jobAppId, EmployeeRecruitingReq createEmp){
        JobApplication jobApplication = _repo.findByCompanyIdAndId(cid, jobAppId).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, jobAppId));
        if(!Constants.JOB_APPLICATION_STATE_DONE.equals(jobApplication.getState())){
            throw new BusinessException("job-application-is-in-process");
        }
        Candidate candidate = _candidateRepo.findByCompanyIdAndId(cid, jobApplication.getCandidateId()).orElseThrow(() -> new EntityNotFoundException(Candidate.class, jobApplication.getCandidateId()));
        EmployeeResp empResp = _hcmService.createEmployee(uid, cid, createEmp);

        candidate.setState(Constants.CANDIDATE_STATE.HIRED.toString());
        candidate.setUpdateBy(uid);
        candidate.setEmployeeId(empResp.getEmployee().getId());
        candidate.setApplyDate(createEmp.getCandicate().getApplyDate());

        jobApplication.setEmployeeId(empResp.getEmployee().getId());
        jobApplication.setUpdateBy(uid);

        _candidateRepo.save(candidate);
        _repo.save(jobApplication);

        return empResp.getEmployee();
    }

    public List<JobApplicationDTO> toDTOs(Long cid, String uid, List<JobApplication> objs) {
        List<JobApplicationDTO> dtos = new ArrayList<>();

        Set<Long> categoryIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();

        objs.forEach(obj -> {
            if(!StringUtils.isEmpty(obj.getCreateBy())){
                userIds.add(obj.getCreateBy());
            }
            if (obj.getPositionId() != null) {
                categoryIds.add(obj.getPositionId());
            }
            if (obj.getTitleId() != null) {
                categoryIds.add(obj.getTitleId());
            }
            if (obj.getEmployeeId() != null && obj.getEmployeeId() != 0) {
                empIds.add(obj.getEmployeeId());
            }
            if(obj.getOrgId() != null && obj.getOrgId() != 0){
                orgIds.add(obj.getOrgId());
            }
        });

        Map<Long, EmployeeDTO> mapEmp = _hcmService.getMapEmployees(uid,cid,empIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);
        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        for (JobApplicationDTO dto : dtos) {
            if (dto.getPositionId() != null) {
                dto.setPositionObj(mapCategory.get(dto.getPositionId()));
            }
            if (dto.getTitleId() != null) {
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
            if(dto.getOrgId() != null){
                dto.setOrg(mapOrg.get(orgIds));
            }
            if (dto.getEmployeeId() != null && dto.getEmployeeId() != 0){
                dto.setEmployeeObj(mapEmp.get(dto.getEmployeeId()));
            }
            if(!StringUtils.isEmpty(dto.getCreateBy())){
                dto.setCreateByObj((Map<String, Object>) mapperUser.get(dto.getCreateBy()));
            }
        }
        return dtos;

    }



    public JobApplicationDTO toDTO(JobApplication obj){
        JobApplicationDTO dto = MapperUtils.map(obj, JobApplicationDTO.class);
        return dto;
    }
}
