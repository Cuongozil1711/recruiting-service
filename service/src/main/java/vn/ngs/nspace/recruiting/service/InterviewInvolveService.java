package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.request.OrgReq;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.repo.InterviewInvolveRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;
import vn.ngs.nspace.recruiting.utils.Constants;

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
        if (dto.getOrgId() == null){
            throw new BusinessException("invalid-org");
        }
        if(dto.getPositionId() == null){
            throw new BusinessException("invalid-position");
        }
        if(dto.getTitleId() == null){
            throw new BusinessException("invalid-title");
        }
        if(dto.getInterviewerId() == null){
            throw new BusinessException("invalid-interviewer");
        }

    }

    /* create object */
    public InterviewInvolveDTO create(Long cid, String uid, InterviewInvolveDTO dto) throws BusinessException {
        valid(dto);
        InterviewInvolve involve = InterviewInvolve.of(cid, uid, dto);
        involve.setStatus(Constants.ENTITY_ACTIVE);
        involve.setCreateBy(uid);
        involve.setUpdateBy(uid);
        involve.setCompanyId(cid);
        involve = repo.save(involve);

        return toDTOWithObj(cid, uid, involve);
    }

    /* update by id object */
    public InterviewInvolveDTO update(Long cid, String uid, Long id, InterviewInvolveDTO dto) throws BusinessException {
        valid(dto);
        InterviewInvolve curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewInvolve.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        return toDTOWithObj(cid, uid, curr);
    }

    /* convert list model object to DTO before response */
    public List<InterviewInvolveDTO> toDTOs(Long cid, String uid, List<InterviewInvolve> objs){
        List<InterviewInvolveDTO> dtos = new ArrayList<>();

        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();

        objs.forEach(obj -> {
            if(obj.getOrgId() != null){
                orgIds.add(obj.getOrgId());
            }
            if(obj.getInterviewerId() != null){
                obj.getInterviewerId().forEach(interviewer -> {
                    employeeIds.add(Long.valueOf(interviewer));
                });

            }
            if(obj.getSupporterId() != null){
                employeeIds.add(obj.getSupporterId());
            }
            if(obj.getPositionId() != null){
                categoryIds.add(obj.getPositionId());
            }
            if(obj.getTitleId() != null){
                categoryIds.add(obj.getTitleId());
            }

            dtos.add(toDTO(obj));
        });

        Map<Long, EmployeeDTO> mapEmployee = _hcmService.getMapEmployees(uid, cid, employeeIds);
        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(InterviewInvolveDTO dto : dtos){
            if(dto.getInterviewerId() != null){
                List<EmployeeDTO> interviewerIds = new ArrayList<>();
                employeeIds.forEach(empId -> interviewerIds.add(mapEmployee.get(empId)));
                dto.setInterviewerObj(interviewerIds);
            }
            if(dto.getSupporterId() != null){
                dto.setSupporterObj(mapEmployee.get(dto.getSupporterId()));
            }
            if(dto.getOrgId() != null){
                dto.setOrg(mapOrg.get(dto.getOrgId()));
            }
            if(dto.getPositionId() != null){
                dto.setPositionObj(mapCategory.get(dto.getPositionId()));
            }
            if(dto.getTitleId() != null){
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
        }

        return dtos;
    }

    /* convert model object to DTO with data before response */
    public InterviewInvolveDTO toDTOWithObj(Long cid, String uid, InterviewInvolve involve){
        return toDTOs(cid, uid, Collections.singletonList(involve)).get(0);
    }

    /* convert model object to DTO before response */
    public InterviewInvolveDTO toDTO(InterviewInvolve involve){
        InterviewInvolveDTO dto = MapperUtils.map(involve, InterviewInvolveDTO.class);
        return dto;
    }

    public List<InterviewInvolveDTO> applyInvolves(Long cid, String uid, List<InterviewInvolveDTO> dtos) {
        List<InterviewInvolveDTO> list = new ArrayList<>();
        for (InterviewInvolveDTO dto : dtos){
            if (dto.getId() != null) {
                list.add(update(cid,uid, dto.getId(),dto));
            } else {
                list.add(create(cid,uid,dto));
            }
        }
        return list;
    }
}
