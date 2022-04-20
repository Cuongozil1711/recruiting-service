package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.share.dto.*;
import vn.ngs.nspace.task.core.data.UserData;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Log4j2
public class InterviewResultService {
    private final InterviewResultRepo _repo;
    private final InterviewCheckListRepo checkListRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final JobApplicationRepo _repoJob;

    public InterviewResultService(InterviewResultRepo repo, InterviewCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService, InterviewCheckListTemplateRepo templateRepo, InterviewCheckListTemplateRepo templateRepo1, InterviewCheckListTemplateItemRepo itemRepo, JobApplicationRepo _repoJob) {
        _repo = repo;
        this.checkListRepo = checkListRepo;
        _hcmService = hcmService;
        _configService = configService;
        this.templateRepo = templateRepo1;
        this.itemRepo = itemRepo;
        this._repoJob = _repoJob;
    }


    public void valid(InterviewResultDTO dto) {
//        if (dto.getCandidateId() == null) {
//            throw new BusinessException("invalid-candidateId");
//        }
//        if (dto.getInterviewDate() == null) {
//            throw new BusinessException("invalid-date");
//        }
//        if (StringUtils.isEmpty(dto.getContent())) {
//            throw new BusinessException("invalid-content");
//        }
//        if (StringUtils.isEmpty(dto.getState())) {
//            throw new BusinessException("invalid-state");
//        }

//          if(dto.getContent() == null){
//              throw new BusinessException("invalid-content");
//          }
//          if (dto.getState() == null){
//              throw new BusinessException("invalid-state");
//          }
    }


    public List<InterviewResultDTO> createByCandidateId(Long cid, String uid, Long candidateId, Set<Long> ListInterViewerId){
        JobApplication ja = _repoJob.findByCompanyIdAndCandidateIdAndStatus(cid, candidateId,Constants.ENTITY_ACTIVE).orElseThrow(()-> new EntityNotFoundException(JobApplication.class, candidateId));
        return createByPositionAndOrg(cid, uid, candidateId, ja.getPositionId(), ja.getOrgId(), ja.getTitleId(), ListInterViewerId);
    }

    public List<InterviewResultDTO> createByPositionAndOrg(Long cid, String uid, Long candidateId, Long position, Long orgId, Long titleId, Set<Long> ListInterViewerId){
        List<InterviewCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, position, orgId, titleId);
        List<InterviewResult> lstResult = new ArrayList<>();
        if (templates.size() == 0){
            throw new BusinessException("has-no-template-for-position");
        }
        InterviewCheckListTemplate template = templates.get(0);
        List<InterviewCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());
        for (Long interViewerId: ListInterViewerId) {
            InterviewCheckListDTO checkListDTO = new InterviewCheckListDTO();
            InterviewResult interviewResult = new InterviewResult();
            interviewResult.setCompanyId(cid);
            interviewResult.setCreateBy(uid);
            interviewResult.setCandidateId(candidateId);
            interviewResult.setInterviewerId(interViewerId);
            interviewResult.setStatus(Constants.ENTITY_ACTIVE);
            interviewResult = _repo.save(interviewResult);
            for (InterviewCheckListTemplateItem item: items) {
                checkListDTO.setInterviewResultId(interviewResult.getId());
                checkListDTO.setItemId(item.getId());
                createCheckList(cid, uid, checkListDTO);
            }
            lstResult.add(interviewResult);
        }
        return toDTOs(cid, uid, lstResult);
    }

    public void createCheckList(Long cid, String uid, InterviewCheckListDTO request) throws BusinessException {
//        valid(request);
        InterviewCheckList exists = checkListRepo.findByCompanyIdAndId(cid, request.getId()).orElse(new InterviewCheckList());
        if(exists.isNew()){
            InterviewCheckList obj = InterviewCheckList.of(cid, uid, request);
            obj.setStatus(Constants.ENTITY_ACTIVE);
            obj.setCompanyId(cid);
            obj.setUpdateBy(uid);
            obj.setCreateBy(uid);

            obj = checkListRepo.save(obj);
        }

    }
    public InterviewResultDTO create(Long cid, String uid, InterviewResultDTO dto) {
        valid(dto);
        InterviewResult interviewResult = InterviewResult.of(cid, uid, dto);
        interviewResult.setCompanyId(cid);
        interviewResult.setCreateBy(uid);
        interviewResult.setStatus(Constants.ENTITY_ACTIVE);

        interviewResult = _repo.save(interviewResult);

        return toDTO(interviewResult);
    }


    private InterviewResultDTO toDTO(InterviewResult interviewResult) {
        InterviewResultDTO dto = MapperUtils.map(interviewResult, InterviewResultDTO.class);
        return dto;
    }


    public InterviewResultDTO update(Long cid, String uid, Long id, InterviewResultDTO dto) {
        valid(dto);
        InterviewResult curr = _repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr.setStatus(Constants.ENTITY_ACTIVE);
        curr = _repo.save(curr);

        if(dto.getCheckLists() != null){
            for (InterviewCheckListDTO checkListDTO: dto.getCheckLists()) {
                checkListDTO.setInterviewResultId(dto.getId());
                updateCheckList(cid, uid, checkListDTO.getId(), checkListDTO);
            }
        }
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);

    }

    public void updateCheckList(Long cid, String uid, Long id, InterviewCheckListDTO request) throws BusinessException{
//        validItem(request);
        if(request.getId() != 0l && request.getId() != null){
            InterviewCheckList curr = checkListRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewCheckList.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setStatus(Constants.ENTITY_ACTIVE);
            curr.setUpdateBy(uid);
            curr = checkListRepo.save(curr);
        }else{
            createCheckList(cid, uid, request);
        }
    }


    public List<InterviewCheckListTemplateItemDTO> getInterviewCheckList(Long cid, String uid, Long ids, InterviewResultDTO dto) {
//        InterviewResult interviewResult = _repo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));
        List<InterviewCheckListTemplateItem> checkLists = itemRepo.findByCompanyIdAndId(cid, ids);
        List<InterviewCheckListTemplateItem> finalList = new ArrayList<>();
        InterviewCheckListTemplateItem exist = checkLists.stream().filter(c -> {
                    return CompareUtil.compare(ids, c.getId());
                })
                .findAny().orElse(new InterviewCheckListTemplateItem());
        if (exist.isNew()) {
            exist.setCompanyId(cid);
            exist.setCreateBy(uid);
            exist.setStatus(Constants.ENTITY_ACTIVE);
            exist = itemRepo.save(exist);

        }
        finalList.add(exist);
        return checkListDTOS(cid, uid, finalList);
    }

    public List<InterviewResultDTO> toDTOs(Long cid, String uid, List<InterviewResult> objs) {

        Set<String> createBy = new HashSet<>();
        Set<String> createDate = new HashSet<>();

        List<InterviewResultDTO> dtos = new ArrayList<>();
        if(objs.size() == 0){
            InterviewResultDTO dto = new InterviewResultDTO();
            return dtos;
        }
        Set<Long> empIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();
        Set<Long> resultIds = new HashSet<>();

        objs.forEach(obj -> {
            if (obj.getInterviewerId() != null && obj.getInterviewerId() != 0) {
                empIds.add(obj.getInterviewerId());
            }
            if(!StringUtils.isEmpty(obj.getCreateBy())){
                userIds.add(obj.getCreateBy());
            }
            if(obj.getId() != null){
                resultIds.add(obj.getId());
            }
            createBy.add(obj.getCreateBy());
            createDate.add(String.valueOf(obj.getCreateDate()));
            dtos.add(toDTO(obj));
        });

        InterviewResult result = objs.get(0);


        Map<Long, EmployeeDTO> mapEmp = _hcmService.getMapEmployees(uid, cid, empIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);
        for (InterviewResultDTO dto : dtos) {
            if (dto.getInterviewerId() != null && dto.getInterviewerId() != 0) {
                dto.setInterviewerIdObj(mapEmp.get(dto.getInterviewerId()));
            }
            if(!StringUtils.isEmpty(dto.getCreateBy())){
                dto.setCreateByObj((Map<String, Object>) mapperUser.get(dto.getCreateBy()));
            }

        }
        return dtos;
    }

    public InterviewCheckListTemplateItemDTO interviewCheckListTemplateItemDTO (InterviewCheckListTemplateItem interviewCheckListTemplateItem){
        InterviewCheckListTemplateItemDTO dto = MapperUtils.map(interviewCheckListTemplateItem, InterviewCheckListTemplateItemDTO.class);
            return dto;
        }

        public List<InterviewCheckListTemplateItemDTO> checkListDTOS (Long cid, String uid, List < InterviewCheckListTemplateItem > objs){
            List<InterviewCheckListTemplateItemDTO> dtos = new ArrayList<>();
            objs.forEach(obj -> {
                dtos.add(interviewCheckListTemplateItemDTO(obj));
            });
            return dtos;
        }

        public void delete (Long cid, String uid, List < Long > ids){
            ids.stream().forEach(i -> {
                InterviewResult interviewResult = _repo.findByCompanyIdAndId(cid, i).orElse(new InterviewResult());
                if (!interviewResult.isNew()) {
                    interviewResult.setStatus(vn.ngs.nspace.recruiting.share.dto.utils.Constants.ENTITY_INACTIVE);
                    interviewResult.setUpdateBy(uid);
                    interviewResult.setModifiedDate(new Date());

                    _repo.save(interviewResult);
                }
            });
        }



}
