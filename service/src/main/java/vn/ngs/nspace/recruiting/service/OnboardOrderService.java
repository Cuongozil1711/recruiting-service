package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OnboardOrderService {
    private final OnboardOrderRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public OnboardOrderService(OnboardOrderRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }

    /* logic validate data before insert model */
    public void valid(OnboardOrderDTO dto) throws BusinessException {

    }

    /* create object */
    public OnboardOrderDTO create(Long cid, String uid, OnboardOrderDTO request) throws BusinessException {
        valid(request);
        OnboardOrder order = OnboardOrder.of(cid, uid, request);
        order.setStatus(Constants.ENTITY_ACTIVE);
        order.setCreateBy(uid);
        order.setUpdateBy(uid);
        order.setCompanyId(cid);
        order = repo.save(order);

        return toDTOWithObj(cid, uid, order);
    }

    /* update by id object */
    public OnboardOrderDTO update(Long cid, String uid, Long id, OnboardOrderDTO request) throws BusinessException {
        valid(request);
        OnboardOrder curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        return toDTOWithObj(cid, uid, curr);
    }

    /* convert list model object to DTO before response */
    public List<OnboardOrderDTO> toDTOs(Long cid, String uid, List<OnboardOrder> objs){
        List<OnboardOrderDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();

        objs.forEach(obj -> {
            if(obj.getBuddy() != null){
                employeeIds.add(obj.getBuddy());
            }
            if(obj.getEmployeeId() != null){
                employeeIds.add(obj.getEmployeeId());
            }

            dtos.add(toDTO(obj));
        });

        Map<Long, EmployeeDTO> mapEmployee = _hcmService.getMapEmployees(uid, cid, employeeIds);
        for(OnboardOrderDTO dto : dtos){
            if(dto.getBuddy() != null){
                dto.setBuddyObj(mapEmployee.get(dto.getBuddy()));
            }
            if(dto.getEmployeeId() != null){
                dto.setEmployeeObj(mapEmployee.get(dto.getEmployeeId()));
            }
        }

        return dtos;
    }

    /* convert model object to DTO with data before response */
    public OnboardOrderDTO toDTOWithObj(Long cid, String uid, OnboardOrder obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    /* convert model object to DTO before response */
    public OnboardOrderDTO toDTO(OnboardOrder obj){
        OnboardOrderDTO dto = MapperUtils.map(obj, OnboardOrderDTO.class);
        return dto;
    }
}
