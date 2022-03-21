package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.InterviewInvolveRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewObjDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class InterviewInvolveService {
    private final InterviewInvolveRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public InterviewInvolveService(InterviewInvolveRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {

        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }

    /* logic validate data before insert model */
    public void valid(InterviewInvolveDTO dto) throws BusinessException {
        if (dto.getOrgId() == null) {
            throw new BusinessException("invalid-org");
        }
        if (dto.getPositionId() == null) {
            throw new BusinessException("invalid-position");
        }
        if (dto.getTitleId() == null) {
            throw new BusinessException("invalid-title");
        }
        if (dto.getInterviewerId() == null) {
            throw new BusinessException("invalid-interviewer");
        }

    }

    /* create list object */
    public List<InterviewInvolveDTO> create(Long cid, String uid, List<InterviewInvolveDTO> request) throws BusinessException {
        List<InterviewInvolveDTO> dtos = new ArrayList<>();
        for (InterviewInvolveDTO dto : request) {
            dtos.add(create(cid, uid, dto));
        }
        return dtos;
    }

    /* create object */
    public InterviewInvolveDTO create(Long cid, String uid, InterviewInvolveDTO dto) throws BusinessException {
        valid(dto);
        List<InterviewInvolve> exists = repo.findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(cid, dto.getPositionId(), dto.getTitleId(), dto.getOrgId(), Constants.ENTITY_ACTIVE);
        if(exists == null || exists.isEmpty()){
            InterviewInvolve involve = InterviewInvolve.of(cid, uid, dto);
            involve.setStatus(Constants.ENTITY_ACTIVE);
            involve.setCreateBy(uid);
            involve.setUpdateBy(uid);
            involve.setCompanyId(cid);
            involve = repo.save(involve);

            return toDTOWithObj(cid, uid, involve);
        }
        else{
            throw new BusinessException("involve-existed");

//            InterviewInvolve involve = exists.get(0);
//            List<Map<String,Object>> curDes = involve.getInterviewDescription();
//            curDes.addAll(new ArrayList<>(dto.getInterviewDescription()));
//            involve.setInterviewDescription(new ArrayList<>(new HashSet<>(curDes)));
//            List<String> currInterviewIds = involve.getInterviewerId();
//            currInterviewIds.addAll(new ArrayList<>(dto.getInterviewerId()));
//            involve.setInterviewerId(new ArrayList<>(new HashSet<>(currInterviewIds)));
//            involve = repo.save(involve);
//
//            return toDTOWithObj(cid, uid, involve);
        }
    }

    /* update by id object */
    public InterviewInvolveDTO update(Long cid, String uid, Long id, InterviewInvolveDTO dto) throws BusinessException {
        valid(dto);
        InterviewInvolve curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewInvolve.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr.setStatus(dto.getStatus() != null ? dto.getStatus() : Constants.ENTITY_INACTIVE);
        curr = repo.save(curr);
        try{
            repo.findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(cid, dto.getPositionId(), dto.getTitleId(), dto.getOrgId(),Constants.ENTITY_ACTIVE);
        }catch (IncorrectResultSizeDataAccessException ex){
            throw new BusinessException("involve-existed");
        }

        return toDTOWithObj(cid, uid, curr);
    }

    /* convert list model object to DTO before response */
    public List<InterviewInvolveDTO> toDTOs(Long cid, String uid, List<InterviewInvolve> objs) {
        List<InterviewInvolveDTO> dtos = new ArrayList<>();

        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();

        objs.forEach(obj -> {
            if (obj.getOrgId() != null) {
                orgIds.add(obj.getOrgId());
            }
            if (obj.getInterviewerId() != null) {
                obj.getInterviewerId().forEach(interviewer -> {
                    employeeIds.add(Long.valueOf(interviewer));
                });

            }
            if (obj.getSupporterId() != null) {
                employeeIds.add(obj.getSupporterId());
            }
            if (obj.getPositionId() != null) {
                categoryIds.add(obj.getPositionId());
            }
            if (obj.getTitleId() != null) {
                categoryIds.add(obj.getTitleId());
            }

            dtos.add(toDTO(obj));
        });

        Map<Long, EmployeeDTO> mapEmployee = _hcmService.getMapEmployees(uid, cid, employeeIds);
        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for (InterviewInvolveDTO dto : dtos) {
            if (dto.getInterviewerId() != null) {
                List<EmployeeDTO> interviewerIds = new ArrayList<>();
                dto.getInterviewerId().stream().forEach(i -> {
                    if(!StringUtils.isEmpty(i)){
                        interviewerIds.add(mapEmployee.get(Long.valueOf(i)));
                    }
                });
                dto.setInterviewerObj(interviewerIds);
            }
            if (dto.getSupporterId() != null) {
                dto.setSupporterObj(mapEmployee.get(dto.getSupporterId()));
            }
            if (dto.getOrgId() != null) {
                dto.setOrg(mapOrg.get(dto.getOrgId()));
            }
            if (dto.getPositionId() != null) {
                dto.setPositionObj(mapCategory.get(dto.getPositionId()));
            }
            if (dto.getTitleId() != null) {
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
        }

        return dtos;
    }

    /* convert model object to DTO with data before response */
    public InterviewInvolveDTO toDTOWithObj(Long cid, String uid, InterviewInvolve involve) {
        return toDTOs(cid, uid, Collections.singletonList(involve)).get(0);
    }
    public Set<InterviewObjDTO> toDTOWithObject(Long cid, String uid, InterviewInvolve involve) {
        InterviewInvolveDTO item= toDTOs(cid, uid, Collections.singletonList(involve)).get(0);
        Set<InterviewObjDTO> interviewerObj = new HashSet<>();
        for (EmployeeDTO itemObj : item.getInterviewerObj()) {
            InterviewObjDTO iObj = new InterviewObjDTO();
            iObj.setKey(itemObj.getId().toString());
            iObj.setValue(itemObj.getFullName().toString());
            interviewerObj.add(iObj);
        }
        return interviewerObj;
    }
    /* convert model object to DTO before response */
    public InterviewInvolveDTO toDTO(InterviewInvolve involve) {
        InterviewInvolveDTO dto = MapperUtils.map(involve, InterviewInvolveDTO.class);
        return dto;
    }

    public List<Map<String, Object>> applyInvolves(Long cid, String uid, Long involveId, List<Map<String, Object>> newDatas) throws BusinessException{
        InterviewInvolve template = repo.findByCompanyIdAndId(cid, involveId).orElseThrow(() -> new EntityNotFoundException(InterviewInvolve.class, involveId));

        for (Map<String, Object> data: newDatas) {
            Long positionId = MapUtils.getLong(data, "positionId", 0l);
            Long titileId = MapUtils.getLong(data, "titleId", 0l);
            Long orgId = MapUtils.getLong(data, "orgId", 0l);
            List<InterviewInvolve> exist = repo.findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(cid, positionId, titileId, orgId, Constants.ENTITY_ACTIVE);
            if (exist.size() >= 1) {
                throw new BusinessException("involve-existed");
//                for (InterviewInvolve existed : exist) {
//                    existed.setStatus(Constants.ENTITY_INACTIVE);
//                    existed = repo.save(existed);
//
//                    InterviewInvolveDTO dto = new InterviewInvolveDTO();
//                    MapperUtils.copyWithoutAudit(template, dto);
//                    dto.setPositionId(positionId);
//                    dto.setTitleId(titileId);
//                    dto.setOrgId(orgId);
//                    create(cid, uid, dto);
//                }
            } else {
                InterviewInvolveDTO dto = new InterviewInvolveDTO();
                MapperUtils.copyWithoutAudit(template, dto);
                dto.setPositionId(positionId);
                dto.setTitleId(titileId);
                dto.setOrgId(orgId);
                create(cid, uid, dto);
            }

        }
        Map<String, Object> data = new HashMap<>();
        data.put("positionId", template.getPositionId());
        data.put("titleId", template.getTitleId());
        newDatas.add(data);
        return newDatas;
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            InterviewInvolve involve = repo.findByCompanyIdAndId(cid, i).orElse(new InterviewInvolve());
            if(!involve.isNew()){
                involve.setStatus(Constants.ENTITY_INACTIVE);
                involve.setUpdateBy(uid);
                involve.setModifiedDate(new Date());

                repo.save(involve);
            }
        });
    }
}
