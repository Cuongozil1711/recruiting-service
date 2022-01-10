package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

@Service
@Transactional
@Log4j2
public class RecruitmentPlanOrderService {
    private final RecruitmentPlanOrderRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public RecruitmentPlanOrderService(RecruitmentPlanOrderRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }
    public void valid(RecruitmentPlanOrderDTO dto){
        if(StringUtils.isEmpty(dto.getCode())){
            throw new BusinessException("invalid-code");
        }
        if (StringUtils.isEmpty(dto.getType())){
            throw new BusinessException("invalid-type");
        }
        if (StringUtils.isEmpty(dto.getSolutionSuggestType())){
            throw new BusinessException("invalid-solutionSuggestType");
        }
        if (dto.getPositionId() == null){
            throw new BusinessException("invalid-positon");
        }
        if (dto.getLevelId() == null){
            throw new BusinessException("invalid-level");
        }
        if (dto.getPic() == null){
            throw new BusinessException("invalid-pic");
        }
        if (dto.getQuantity() == null){
            throw new BusinessException("invalid-quantity");
        }
        if (dto.getOrgId() == null){
            throw new BusinessException("invalid-org");
        }
        if (dto.getStartDate() == null){
            throw new BusinessException("invalid-startDate");
        }
        if (dto.getDeadline() == null){
            throw new BusinessException("invalid-deadline");
        }
    }
    public List<RecruitmentPlanOrderDTO> create(Long cid, String uid, List<RecruitmentPlanOrderDTO> dtos) throws BusinessException{
        List<RecruitmentPlanOrderDTO> list = new ArrayList<>();
        for(RecruitmentPlanOrderDTO dto : dtos){
            list.add(create(cid, uid, dto));
        }
        return list;
    }

    public RecruitmentPlanOrderDTO create(Long cid, String uid, RecruitmentPlanOrderDTO dto) throws BusinessException {
        valid(dto);
        RecruitmentPlanOrder recruitmentPlanOrder = RecruitmentPlanOrder.of(cid, uid, dto);
        recruitmentPlanOrder.setCreateBy(uid);
        recruitmentPlanOrder.setCompanyId(cid);
        repo.save(recruitmentPlanOrder);
        return toDTO(recruitmentPlanOrder);
    }

    public RecruitmentPlanOrderDTO update(Long cid, Long id, RecruitmentPlanOrderDTO recruitmentPlanOrderDTO) {
        valid(recruitmentPlanOrderDTO);
        RecruitmentPlanOrder curr = repo.findByCompanyIdAndId(cid, id).orElse(new RecruitmentPlanOrder());
        MapperUtils.copyWithoutAudit(recruitmentPlanOrderDTO,curr);
        curr = repo.save(curr);
        return toDTO(curr);
    }

    public List<RecruitmentPlanOrderDTO> toDTOs(Long cid, String uid, List<RecruitmentPlanOrder> objs){
        List<RecruitmentPlanOrderDTO> dtos = new ArrayList<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> empIds = new HashSet<>();

        objs.forEach(obj -> {
            if(obj.getOrgId() != null){
                orgIds.add(obj.getOrgId());
            }
            if(obj.getPositionId() != null){
                categoryIds.add(obj.getPositionId());
            }
            if(obj.getTitleId() != null){
                categoryIds.add(obj.getTitleId());
            }
            if(obj.getLevelId() != null){
                categoryIds.add(obj.getLevelId());
            }
            if (obj.getPic() != null){
                empIds.add(obj.getPic());
            }
            if (obj.getSupporterId() != null){
                empIds.add(obj.getSupporterId());
            }
            dtos.add(toDTO(obj));
        });

        List<OrgResp> orgs = _hcmService.getOrgResp(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
        dtos.stream().map(dto -> {
            if(dto.getPositionId() != null){
                dto.setPosition(mapCategory.get(dto.getPositionId()));
            }
            if(dto.getTitleId() != null){
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
            if(dto.getLevelId() != null){
                dto.setLevelObj(mapCategory.get(dto.getLevelId()));
            }
           if(dto.getOrgId() != null){
               OrgResp org = orgs.stream().filter(o -> CompareUtil.compare(o.getId(), dto.getOrgId())).findAny().orElse(new OrgResp());
               dto.setOrgResp(org);
           }
            if (dto.getPic() != null){
                EmployeeDTO emp = employees.stream().filter(e -> CompareUtil.compare(e.getId(),dto.getPic())).findAny().orElse(new EmployeeDTO());
                dto.setEmployeeDTO(emp);
            }
            if (dto.getSupporterId() != null){
                EmployeeDTO employeeDTO = employees.stream().filter(emp -> CompareUtil.compare(emp.getId(),dto.getSupporterId())).findAny().orElse(new EmployeeDTO());
                dto.setEmployeeDTO(employeeDTO);
            }
           return dto;
        });

        return dtos;
    }

    public RecruitmentPlanOrderDTO toDTO(RecruitmentPlanOrder obj){
        return MapperUtils.map(obj, RecruitmentPlanOrderDTO.class);
    }
}
