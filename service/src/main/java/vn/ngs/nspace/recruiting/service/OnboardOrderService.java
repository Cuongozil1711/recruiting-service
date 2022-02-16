package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.ContractDTO;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.OnboardContractRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardContractDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardOrderService {
    private final OnboardOrderRepo repo;
    private final OnboardOrderCheckListRepo checkListRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final OnboardContractRepo _contactRepo;

    public OnboardOrderService(OnboardOrderRepo repo, OnboardOrderCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService, OnboardContractRepo _contactRepo) {
        this.repo = repo;
        this.checkListRepo = checkListRepo;
        _hcmService = hcmService;
        _configService = configService;
        this._contactRepo= _contactRepo;
    }

    /* logic validate data before insert model */
    public void valid(OnboardOrderDTO dto) throws BusinessException {
//        if(dto.getBuddy() == null){
//            throw new BusinessException("invalid-buddy");
//        }
//        if(dto.getMentorId() == null){
//            throw new BusinessException("invalid-mentor");
//        }
        if (dto.getEmployeeId() == null){
            throw new BusinessException("invalid-employee");
        }
    }

    /* create object */
    public OnboardOrderDTO create(Long cid, String uid, OnboardOrderDTO request) throws BusinessException {
        valid(request);
        OnboardOrder order = OnboardOrder.of(cid, uid, request);
        order.setStatus(Constants.ENTITY_ACTIVE);
        order.setCreateBy(uid);
        order.setUpdateBy(uid);
        order.setCompanyId(cid);
        order.setCreateDate(new Date());
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
        Set<Long> orgIds = new HashSet<>();
        Set<Long> orderIds = new HashSet<>();
        Set<Long> contractIds = new HashSet<>();
        objs.forEach(obj -> {
            if(obj.getEmployeeId() != null){
                employeeIds.add(obj.getEmployeeId());
            }
            if(obj.getBuddy() != null){
                employeeIds.add(obj.getBuddy());
            }
            if(obj.getMentorId() != null){
                employeeIds.add(obj.getMentorId());
            }
            if(obj.getId() != null){
                orderIds.add(obj.getId());
            }
            if(obj.getJobApplicationId() != null){
                JobApplication ja = repo.getInfoOnboard(cid, obj.getId()).orElseThrow(()-> new BusinessException("not found OnboardOder"));
                if(ja.getPositionId() != null){
                    categoryIds.add(ja.getPositionId());
                }
                if(ja.getTitleId() != null){
                    categoryIds.add(ja.getTitleId());
                }
                if(ja.getOrgId() != null){
                    orgIds.add(ja.getOrgId());
                }
            }

            dtos.add(toDTO(obj));
        });

        List<OnboardContract> contacts = _contactRepo.findByCompanyIdAndOnboardOrderIdIn(cid, orderIds);
        Map<Long, List<OnboardContract>> mapContacts = contacts.stream().collect(Collectors.groupingBy(OnboardContract::getOnboardOrderId));
        contractIds = contacts.stream().map(el -> el.getContractId()).collect(Collectors.toSet());

        List<OnboardOrderCheckList> orderCheckLists = checkListRepo.findByCompanyIdAndOnboardOrderIdIn(cid, orderIds);
        Map<Long, List<OnboardOrderCheckList>> mapCheckLists = orderCheckLists.stream().collect(Collectors.groupingBy(OnboardOrderCheckList::getOnboardOrderId));

        List<OrgResp> orgs = _hcmService.getOrgResp(uid, cid, orgIds);

        Map<Long, EmployeeDTO> mapEmployee = _hcmService.getMapEmployees(uid, cid, employeeIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        for(OnboardOrderDTO dto : dtos){
            if(dto.getBuddy() != null){
                dto.setBuddyObj(mapEmployee.get(dto.getBuddy()));
            }if(dto.getMentorId() != null){
                dto.setMentorObj(mapEmployee.get(dto.getMentorId()));
            }
            if(dto.getEmployeeId() != null){
                dto.setEmployeeObj(mapEmployee.get(dto.getEmployeeId()));
            }
            if(dto.getJobApplicationId() != null){
                JobApplication ja = repo.getInfoOnboard(cid, dto.getId()).orElseThrow(()-> new BusinessException("not found JopAplication"));
                dto.setPositionObj(mapCategory.get(ja.getPositionId()));
                dto.setTitleObj(mapCategory.get(ja.getTitleId()));
                if(ja.getOrgId() != null){
                    OrgResp org = orgs.stream().filter(o -> CompareUtil.compare(o.getId(), ja.getOrgId())).findAny().orElse(new OrgResp());
                    dto.setOrgResp(org);
                }
                dto.setContractType(ja.getContractType());
            }

            if (mapContacts.get(dto.getId()) != null && contractIds != null){
                for (Long contractId: contractIds){
                    dto.setContract( _hcmService.getContract( uid, cid, contractId));
                }
            }

            if(mapCheckLists.get(dto.getId()) != null){
                OnboardOrder order = new OnboardOrder();
                for (OnboardOrderCheckList checkList: mapCheckLists.get(dto.getId()) ){
                        if (CompareUtil.compare(checkList.getState(), "complete") ){
                            MapperUtils.copyWithoutAudit(dto, order);
                            order.setUpdateBy(uid);
                            order.setState("complete");
                            order = repo.save(order);
                            break;
                        }
                        if (CompareUtil.compare(checkList.getState(), "notcomplete")) {
                            MapperUtils.copyWithoutAudit(dto, order);
                            order.setUpdateBy(uid);
                            order.setState("notcomplete");
                            order = repo.save(order);
                            break;
                        }
                }
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
                exists.setOnboardOrderId(onboardOrderId);
                exists.setState("notcomplete");

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


    /* update Buddy and Mentor by Onboard ID */
    public OnboardOrderDTO updateBuddyByOnboardId(Long cid, String uid, Long id, Long buddy, Long mentorId){
        if(cid == null){
            throw new BusinessException("invalid-cid");
        }
        if (id == null){
            throw new BusinessException("invalid-id");
        }
        if (buddy == null){
            throw new BusinessException("invalid-buddy");
        }
        if (mentorId == null){
            throw new BusinessException("invalid-mentorId");
        }
        OnboardOrder curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, id));
        curr.setUpdateBy(uid);
        curr.setBuddy(buddy);
        curr.setMentorId(mentorId);
        curr = repo.save(curr);

        return toDTOWithObj(cid, uid, curr);
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
