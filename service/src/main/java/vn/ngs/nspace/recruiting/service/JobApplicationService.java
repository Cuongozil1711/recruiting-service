package vn.ngs.nspace.recruiting.service;

import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.config.share.ConfigApi;
import vn.ngs.nspace.config.share.dto.workflow.WorkFlowDTO;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.EmployeeResp;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;

import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.Reason;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.request.JobApplicationRequest;
import vn.ngs.nspace.recruiting.share.dto.EmployeeRecruitingReq;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.data.UserData;
import vn.ngs.nspace.task.core.service.TaskService;
import vn.ngs.nspace.workflow.dto.base.ApprovalDTO;
import vn.ngs.nspace.workflow.dto.base.BaseRequestOrder;
import vn.ngs.nspace.workflow.dto.request.RequestOrderReq;
import vn.ngs.nspace.workflow.service.RequestApi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@Log4j2

public class JobApplicationService extends TaskService<JobApplication, JobApplicationRepo, JobApplicationRequest> {

    private final JobApplicationRepo _repo;
    private final OnboardOrderService _onboardService;
    private final CandidateRepo _candidateRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final ConfigApi configApi;
    private final RequestApi requestApi;

    public JobApplicationService(JobApplicationRepo repo, OnboardOrderService onboardService, CandidateRepo candidateRepo, ExecuteHcmService hcmService, ExecuteConfigService configService, ConfigApi configApi, RequestApi requestApi) {
        super(repo);
        _repo = repo;
        _onboardService = onboardService;
        _candidateRepo = candidateRepo;
        _hcmService = hcmService;
        _configService = configService;
        this.configApi = configApi;
        this.requestApi = requestApi;
    }

    public Map<String, Object> initJobApplicationForm(Long cid, String uid, String locale, Set<String> include, JobApplicationRequest formReq) {
        Map<String, Object> resp = new HashMap<>();

        //init workflow request
        RequestOrderReq initRequest = initWorkflowRequest(cid, uid, locale, formReq.getTask());
        resp.put("task", formReq.getTask());
        if (initRequest != null) {
            resp.put("workflowRequest", initRequest);
        }

        return resp;
    }

    private RequestOrderReq initWorkflowRequest(Long cid, String uid, String locale, JobApplication form) {
        //Check has any workflow config
        RequestOrderReq initRequest = null;
        List<WorkFlowDTO> jobApplicationWorkflow = configApi.getWorkflowsByPeriod(cid, uid, application, "job_application", "job");
        if (jobApplicationWorkflow != null && !jobApplicationWorkflow.isEmpty()) {
            WorkFlowDTO workflow = jobApplicationWorkflow.get(0);
            initRequest = new RequestOrderReq();
            BaseRequestOrder order = new BaseRequestOrder();
            order.setWorkflowId(workflow.getId());
            order.setDescription(form.getDescription());
            if (form != null) {
                form.setWorkflowId(workflow.getId());
            }
            initRequest.setTask(order);
            initRequest = requestApi.initRequest(cid, uid, locale, "", initRequest);
        }
        return initRequest;
    }


    @Override
    public Map<String, Object> init(Long cid, String uid, JobApplicationRequest formReq, Set<String> include) throws BusinessException {
        JobApplication form = formReq.getTask();
        if (form == null) {
            throw new BusinessException("invalid-form");
        }
        String requestedBy = form.getRequestedBy() == null ? uid : form.getRequestedBy();
        if (form.getRequestedBy() == null) {
            form.setRequestedBy(requestedBy);
        }
        if (StringUtils.isEmpty(requestedBy)) {
            throw new BusinessException("invalid-requester");
        }
        //valid form
//        validInputForm(cid, form);
        RequestOrderReq initRequest = initWorkflowRequest(cid, requestedBy, formReq.getLocale(), form);
        if (initRequest != null) {
            form.setWorkflowId(initRequest.getTask().getWorkflowId());
            form.setResponsibleId(null);
        }
        Map<String, Object> result = super.init(cid, uid, formReq, include);
//        createFormDetail(cid, uid, form);
        //valid form policy
//        validFormPolicy(cid, employee, form);
//        Map<String, Object> formObj = (Map<String, Object>) result.get("task");

        if (initRequest != null) {
            Map<String, Object> formInfo = (Map<String, Object>) result.getOrDefault("task", new ConcurrentHashMap<>());
            requestApi.mappingTransferFieldValues(cid, formReq.getLocale(), initRequest, formInfo);
            BaseRequestOrder order = JsonObject.mapFrom(initRequest.getTask()).mapTo(BaseRequestOrder.class);
            order.setRootApp(application);

            order.setRootEntity("job_application");
            order.setRootId(form.getId());
            initRequest.setTask(order);
            List<ApprovalDTO> approvals = initRequest.getInitTask().getApprovals();
            if(approvals == null || approvals.isEmpty()){
                approvals = new ArrayList<>();

                List<EmployeeDTO> emps = _hcmService.getEmployees(uid, cid, formReq.getApproveEmpId());
                form.setResponsibleId(emps.get(0).getUserMappingId());
                if (form.getResponsibleId() == null) {
                    throw new BusinessException("invalid-approver");
                }
                approvals.add(ApprovalDTO.builder().responsibleId(form.getResponsibleId()).build());
                initRequest.getInitTask().setApprovals(approvals);
            }

            initRequest = requestApi.createRequest(cid, uid, formReq.getLocale(), "", initRequest);
            //update requestId for emp_form
            Long requestId = initRequest.getTask().getId();
            form.setRequestId(requestId);
            formInfo.put("requestId", requestId);
            result.put("task", formInfo);
            result.put("workflowRequest", initRequest);
        }

        return result;
    }



    public void valid(JobApplicationDTO dto){
        if (dto.getOrgId() == null){
            throw new BusinessException("invalid-org");
        }
        if (dto.getPositionId() == null){
            throw new BusinessException("invalid-position");
        }
        if (StringUtils.isEmpty(dto.getState())){
            throw new BusinessException("invalid-state");
        }
    }

    public JobApplicationDTO create(Long cid, String uid, JobApplicationDTO request) {
        valid(request);
        JobApplication obj = JobApplication.of(cid, uid, request);
        obj.setType("job");
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
        curr.setType("job");
        curr.setUpdateBy(uid);
        curr = _repo.save(curr);

        return toDTO(curr);
    }

    public EmployeeDTO createEmployee(Long cid, String uid, Long jobAppId, EmployeeRecruitingReq createEmp){
        JobApplication jobApplication = _repo.findByCompanyIdAndId(cid, jobAppId).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, jobAppId));
//        if(!Constants.JOB_APPLICATION_STATE_DONE.equals(jobApplication.getState())){
//            throw new BusinessException("job-application-is-in-process");
//        }
        Candidate candidate = _candidateRepo.findByCompanyIdAndId(cid, jobApplication.getCandidateId()).orElseThrow(() -> new EntityNotFoundException(Candidate.class, jobApplication.getCandidateId()));
        EmployeeResp empResp = _hcmService.createEmployee(uid, cid, createEmp);

        candidate.setState(Constants.CANDIDATE_STATE.HIRED.toString());
        candidate.setUpdateBy(uid);
        candidate.setEmployeeId(empResp.getEmployee().getId());
//        candidate.setApplyDate(createEmp.getCandicate().getApplyDate());

        jobApplication.setEmployeeId(empResp.getEmployee().getId());
        jobApplication.setUpdateBy(uid);
        jobApplication.setState("STAFF");
        _candidateRepo.save(candidate);
        _repo.save(jobApplication);

        OnboardOrderDTO onboardOrder = new OnboardOrderDTO();
        onboardOrder.setEmployeeId(empResp.getEmployee().getId());
        onboardOrder.setJobApplicationId(jobAppId);
        _onboardService.create(cid, uid, onboardOrder);

        return empResp.getEmployee();
    }
    public EmployeeDTO updateEmployee(Long cid, String uid, Long jobAppId, EmployeeRecruitingReq createEmp){
        JobApplication jobApplication = _repo.findByCompanyIdAndId(cid, jobAppId).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, jobAppId));
//        if(!Constants.JOB_APPLICATION_STATE_DONE.equals(jobApplication.getState())){
//            throw new BusinessException("job-application-is-in-process");
//        }
        Candidate candidate = _candidateRepo.findByCompanyIdAndId(cid, jobApplication.getCandidateId()).orElseThrow(() -> new EntityNotFoundException(Candidate.class, jobApplication.getCandidateId()));
        EmployeeResp empResp = _hcmService.updateEmployee(uid, cid, createEmp);

        candidate.setState(Constants.CANDIDATE_STATE.HIRED.toString());
        candidate.setUpdateBy(uid);
        candidate.setEmployeeId(empResp.getEmployee().getId());
//        candidate.setApplyDate(createEmp.getCandicate().getApplyDate());

        jobApplication.setEmployeeId(empResp.getEmployee().getId());
        jobApplication.setUpdateBy(uid);
        jobApplication.setState("STAFF");
        _candidateRepo.save(candidate);
        _repo.save(jobApplication);

        OnboardOrderDTO onboardOrder = new OnboardOrderDTO();
        onboardOrder.setEmployeeId(empResp.getEmployee().getId());
        onboardOrder.setJobApplicationId(jobAppId);
        long id  = createEmp.getEmployee().getId();
        _onboardService.update(cid, uid,id, onboardOrder);

        return empResp.getEmployee();
    }

    public JobApplicationDTO initByCandidate(Long cid, String uid, Long candidateId) {
        JobApplication currentJr = _repo.findByCompanyIdAndCandidateIdAndStatus(cid, candidateId, Constants.ENTITY_ACTIVE).orElse(new JobApplication());
        if(currentJr.isNew()){
            currentJr.setCandidateId(candidateId);
            currentJr.setCompanyId(cid);
            currentJr.setType("job");
            currentJr.setCreateBy(uid);
            currentJr.setUpdateBy(uid);
            currentJr.setState(Constants.JOB_APPLICATION_STATE.INIT.name());
            currentJr.setStatus(Constants.ENTITY_ACTIVE);
            _repo.save(currentJr);
        }
        return toDTOWithObj(cid,uid,currentJr);
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
            if(obj.getCvSourceId() != null){
                categoryIds.add(obj.getCvSourceId());
            }
            if (obj.getIntroduceById() != null) {
                obj.getIntroduceById().forEach(introduceBy -> {
                    empIds.add(Long.valueOf(introduceBy));
                });

            }
            if (obj.getEmployeeId() != null && obj.getEmployeeId() != 0) {
                empIds.add(obj.getEmployeeId());
            }
            if(obj.getOrgId() != null && obj.getOrgId() != 0){
                orgIds.add(obj.getOrgId());
            }
            dtos.add(toDTO(obj));
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
            if (dto.getIntroduceById() != null){
                List<EmployeeDTO> introduceByIds = new ArrayList<>();
                dto.getIntroduceById().stream().forEach(i -> {
                    if(!StringUtils.isEmpty(i)){
                       introduceByIds.add(mapEmp.get(Long.valueOf(i)));
                    }
                });
                dto.setIntroduceByObj(introduceByIds);
            }
            if (dto.getCvSourceId() != null){
                dto.setCvSourceObj(mapCategory.get(dto.getCvSourceId()));
            }
            if(!StringUtils.isEmpty(dto.getCreateBy())){
                dto.setCreateByObj((Map<String, Object>) mapperUser.get(dto.getCreateBy()));
            }
        }
        return dtos;

    }

    public JobApplicationDTO toDTOWithObj(Long cid, String uid, JobApplication obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    public JobApplicationDTO toDTO(JobApplication obj){
        JobApplicationDTO dto = MapperUtils.map(obj, JobApplicationDTO.class);
        return dto;
    }


}
