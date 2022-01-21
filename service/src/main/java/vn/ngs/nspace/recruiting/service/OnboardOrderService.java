package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OnboardOrderService {
    private final OnboardOrderRepo repo;
    private final OnboardOrderCheckListRepo checkListRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public OnboardOrderService(OnboardOrderRepo repo, OnboardOrderCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.checkListRepo = checkListRepo;
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
            if(obj.getMentorId() != null){
                employeeIds.add(obj.getMentorId());
            }
            dtos.add(toDTO(obj));
        });

        Map<Long, EmployeeDTO> mapEmployee = _hcmService.getMapEmployees(uid, cid, employeeIds);
        for(OnboardOrderDTO dto : dtos){
            if(dto.getBuddy() != null){
                dto.setBuddyObj(mapEmployee.get(dto.getBuddy()));
            }if(dto.getMentorId() != null){
                dto.setBuddyObj(mapEmployee.get(dto.getMentorId()));
            }
            if(dto.getEmployeeId() != null){
                dto.setEmployeeObj(mapEmployee.get(dto.getEmployeeId()));
            }
        }

        return dtos;
    }

    public List<OnboardOrderCheckListDTO> getOnboardOrderCheckList(Long cid, String uid, Long onboardOrderId){
        OnboardOrder onboard = repo.findByCompanyIdAndId(cid, onboardOrderId).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, onboardOrderId));
        List<OnboardOrderCheckList> checkList =  checkListRepo.findByCompanyIdAndOnboardOrderIdAndCodeIn(cid, onboardOrderId, Constants.onboardCheckList);
        List<OnboardOrderCheckList> finalCheckList = new ArrayList<>();
        for(String checkCode : Constants.onboardCheckList){
            OnboardOrderCheckList exists =
                    checkList.stream().filter(c -> {return CompareUtil.compare(checkCode, c.getCode());})
                            .findAny().orElse(new OnboardOrderCheckList());
            if(exists.isNew()){
                exists.setCompanyId(cid);
                exists.setCreateBy(uid);
                exists.setUpdateBy(uid);
                exists.setCode(checkCode);
                exists.setStatus(Constants.ENTITY_ACTIVE);
                exists.setEmployeeId(onboard.getEmployeeId());

                exists.setState(Constants.CMD_PENDING);
                exists = checkListRepo.save(exists);
            }
            finalCheckList.add(exists);
        }
        return checkListToDTOs(cid, uid, finalCheckList);
    }

    public List<OnboardOrderCheckListDTO> checkListToDTOs(Long cid, String uid, List<OnboardOrderCheckList> objs){
        List<OnboardOrderCheckListDTO> dtos = new ArrayList<>();
        Set<Long> employeeIds = new HashSet<>();
        Map<Long, EmployeeDTO> mapEmp = new HashMap<>();
        objs.stream().forEach(o -> {
            dtos.add(MapperUtils.map(o, OnboardOrderCheckListDTO.class));

            if(o.getEmployeeId() != null && o.getEmployeeId() != 0){
                employeeIds.add(o.getEmployeeId());
            }

            if(o.getResponsibleId() != null && o.getResponsibleId() != 0){
                employeeIds.add(o.getResponsibleId());
            }

            if(o.getParticipantId() != null && o.getParticipantId() != 0){
                employeeIds.add(o.getParticipantId());
            }
        });
        if(!employeeIds.isEmpty()){
            mapEmp = _hcmService.getMapEmployees(uid, cid, employeeIds);
        }
        for(OnboardOrderCheckListDTO dto : dtos){
            dto.setEmployeeObj((dto.getEmployeeId() != null && dto.getEmployeeId() != 0) ? mapEmp.get(dto.getEmployeeId()) : null);
            dto.setResponsibleObj((dto.getResponsibleId() != null && dto.getResponsibleId() != 0) ? mapEmp.get(dto.getResponsibleId()) : null);
            dto.setParticipantObj((dto.getParticipantId() != null && dto.getParticipantId() != 0) ? mapEmp.get(dto.getParticipantId()) : null);
        }

        return dtos;
    }

    public OnboardOrderDTO createBuddyByOnbodrdId(Long cid, String uid, OnboardOrderDTO request){
        OnboardOrder order = OnboardOrder.of(cid, uid, request);
        order = repo.save(order);
        return toDTOs(cid, uid, Arrays.asList(order)).get(0);
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
