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
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.task.core.data.UserData;

import java.util.*;

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

    public InterviewResultService(InterviewResultRepo repo, InterviewCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService, InterviewCheckListTemplateRepo templateRepo, InterviewCheckListTemplateRepo templateRepo1, InterviewCheckListTemplateItemRepo itemRepo) {
        _repo = repo;
        this.checkListRepo = checkListRepo;
        _hcmService = hcmService;
        _configService = configService;
        this.templateRepo = templateRepo1;
        this.itemRepo = itemRepo;
    }


    public void valid(InterviewResultDTO dto) {
        if (dto.getCandidateId() == null) {
            throw new BusinessException("invalid-candidateId");
        }
        if (dto.getInterviewDate() == null) {
            throw new BusinessException("invalid-date");
        }
        if (StringUtils.isEmpty(dto.getContent())) {
            throw new BusinessException("invalid-content");
        }
        if (StringUtils.isEmpty(dto.getState())) {
            throw new BusinessException("invalid-state");
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
        curr = _repo.save(curr);

        return toDTO(curr);

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
        List<InterviewResultDTO> dtos = new ArrayList<>();
        Set<Long> empIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();
        objs.forEach(obj -> {
            if (obj.getInterviewerId() != null && obj.getInterviewerId() != 0) {
                empIds.add(obj.getInterviewerId());
            }
            if(!StringUtils.isEmpty(obj.getCreateBy())){
                userIds.add(obj.getCreateBy());
            }
            dtos.add(toDTO(obj));
        });
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
