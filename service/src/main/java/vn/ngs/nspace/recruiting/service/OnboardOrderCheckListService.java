package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardOrderCheckListService {
    private final OnboardOrderCheckListRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public OnboardOrderCheckListService(OnboardOrderCheckListRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public List<OnboardOrderCheckListDTO> changeState(Long cid, String uid, Long onboardOrderID, List<OnboardOrderCheckListDTO> listDTOS){
        List<OnboardOrderCheckList> lst = repo.findByCompanyIdAndOnboardOrderId(cid, onboardOrderID);
        List<String> lstCodeUpdate = lst.stream().map(dto -> dto.getCode()).collect(Collectors.toList());
        List<OnboardOrderCheckList> onboardOrderCheckLists = new ArrayList<>();
        for (String code: lstCodeUpdate){
            OnboardOrderCheckList onboardOrderCheckList = lst.stream().filter(el -> el.getCode() == code).collect(Collectors.toList()).get(0);

            OnboardOrderCheckListDTO dto = listDTOS.stream().filter(el -> el.getCode() == code).collect(Collectors.toList()).get(0);
            if (onboardOrderCheckList != null){
                onboardOrderCheckList.setState(dto.getState());

                onboardOrderCheckLists.add(onboardOrderCheckList);
            }
        }

        if (onboardOrderCheckLists != null && !onboardOrderCheckLists.isEmpty()){
            onboardOrderCheckLists = repo.saveAll(onboardOrderCheckLists);
        }

        return checkListToDTOs(cid, uid, onboardOrderCheckLists);
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

    public OnboardOrderCheckListDTO toDTO(OnboardOrderCheckList obj){
        OnboardOrderCheckListDTO dto = MapperUtils.map(obj, OnboardOrderCheckListDTO.class);
        return dto;
    }
}
