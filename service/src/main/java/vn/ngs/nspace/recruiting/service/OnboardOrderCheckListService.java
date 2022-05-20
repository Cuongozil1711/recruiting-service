///*
//package vn.ngs.nspace.recruiting.service;
//
//import org.springframework.stereotype.Service;
//import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
//import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
//import vn.ngs.nspace.lib.utils.CompareUtil;
//import vn.ngs.nspace.lib.utils.MapperUtils;
//import vn.ngs.nspace.recruiting.model.AssetCheckList;
//import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
//import vn.ngs.nspace.recruiting.repo.AssetCheckListRepo;
//import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
//import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
//
//import javax.transaction.Transactional;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class OnboardOrderCheckListService {
//    private final OnboardOrderCheckListRepo repo;
//    private final ExecuteHcmService _hcmService;
//    private final ExecuteConfigService _configService;
//    private final AssetCheckListRepo assetCheckListRepo;
//
//    public OnboardOrderCheckListService(OnboardOrderCheckListRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService, AssetCheckListRepo assetCheckListRepo) {
//        this.repo = repo;
//        _hcmService = hcmService;
//        _configService = configService;
//        this.assetCheckListRepo = assetCheckListRepo;
//    }
//
//    public void validateChangeState(String code, String oldState, String newState){
//
//    }
//
//    public List<OnboardOrderCheckListDTO> updateList(Long cid, String uid, Long onboardOrderID, List<OnboardOrderCheckListDTO> listDTOS){
//        List<OnboardOrderCheckList> checkLists = new ArrayList<>();
//        for(OnboardOrderCheckListDTO checkListDTO : listDTOS){
//            OnboardOrderCheckList checkList = repo.findByCompanyIdAndOnboardOrderIdAndId(cid, onboardOrderID, checkListDTO.getId()).orElseThrow(() -> new EntityNotFoundException(OnboardOrderCheckList.class, checkListDTO.getId()));
//            boolean haveUpdate = false;
//            if(!CompareUtil.compare(checkList.getState(), checkListDTO.getState())){
//                validateChangeState(checkListDTO.getCode(), checkList.getState(), checkListDTO.getState());
//                checkList.setState(checkListDTO.getState());
//                haveUpdate = true;
//            }
//            if(!CompareUtil.compare(checkList.getParticipantId(), checkListDTO.getParticipantId())){
//                checkList.setParticipantId(checkListDTO.getParticipantId());
//                haveUpdate = true;
//            }
//            if(!CompareUtil.compare(checkList.getResponsibleId(), checkListDTO.getResponsibleId())){
//                checkList.setResponsibleId(checkListDTO.getResponsibleId());
//                haveUpdate = true;
//            }
//            if(!CompareUtil.compare(checkList.getStartDate(), checkListDTO.getStartDate())){
//                checkList.setStartDate(checkListDTO.getStartDate());
//                haveUpdate = true;
//            }
//            if(!CompareUtil.compare(checkList.getDeadline(), checkListDTO.getDeadline())){
//                checkList.setDeadline(checkListDTO.getDeadline());
//                haveUpdate = true;
//            }
//            if(!CompareUtil.compare(checkList.getDescription(), checkListDTO.getDescription())){
//                checkList.setDescription(checkListDTO.getDescription());
//                haveUpdate = true;
//            }
//            if(haveUpdate){
//                checkList.setUpdateBy(uid);
//                checkList = repo.save(checkList);
//            }
//            checkLists.add(checkList);
//        }
//
//        return checkListToDTOs(cid, uid, checkLists);
//    }
//
//    public List<OnboardOrderCheckListDTO> checkListToDTOs(Long cid, String uid, List<OnboardOrderCheckList> objs){
//        List<OnboardOrderCheckListDTO> dtos = new ArrayList<>();
//        Set<Long> employeeIds = new HashSet<>();
//        Set<Long> orderIds = new HashSet<>();
//        Map<Long, EmployeeDTO> mapEmp = new HashMap<>();
//        objs.stream().forEach(o -> {
//            dtos.add(MapperUtils.map(o, OnboardOrderCheckListDTO.class));
//
//            if(o.getEmployeeId() != null && o.getEmployeeId() != 0){
//                employeeIds.add(o.getEmployeeId());
//            }
//
//            if(o.getResponsibleId() != null && o.getResponsibleId() != 0){
//                employeeIds.add(o.getResponsibleId());
//            }
//
//            if(o.getParticipantId() != null && o.getParticipantId() != 0){
//                employeeIds.add(o.getParticipantId());
//            }
//            if (o.getOnboardOrderId() != null && o.getOnboardOrderId() != 0){
//                orderIds.add(o.getOnboardOrderId());
//            }
//        });
//        List<AssetCheckList> assetChecklists = assetCheckListRepo.findByCompanyIdAndOnboardOrderIdIn(cid, orderIds);
//        Map<Long, List<AssetCheckList>> mapCheckLists = assetChecklists.stream().collect(Collectors.groupingBy(AssetCheckList::getOnboardOrderId));
//
//        if(!employeeIds.isEmpty()){
//            mapEmp = _hcmService.getMapEmployees(uid, cid, employeeIds);
//        }
//        for(OnboardOrderCheckListDTO dto : dtos){
//            dto.setEmployeeObj((dto.getEmployeeId() != null && dto.getEmployeeId() != 0) ? mapEmp.get(dto.getEmployeeId()) : null);
//            dto.setResponsibleObj((dto.getResponsibleId() != null && dto.getResponsibleId() != 0) ? mapEmp.get(dto.getResponsibleId()) : null);
//            dto.setParticipantObj((dto.getParticipantId() != null && dto.getParticipantId() != 0) ? mapEmp.get(dto.getParticipantId()) : null);
//
////            if (mapCheckLists.get(dto.getOnboardOrderId()) != null) {;
////                List<String> checkState = new ArrayList<>();
////                for (AssetCheckList asset : mapCheckLists.get(dto.getOnboardOrderId())){
////                    if (asset.getState().equals("notcomplete")){
////                        checkState.add(asset.getState());
////                    }
////                }
////                if (checkState != null && !checkState.isEmpty()){
////                    dto.setState("notcomplete");
////                }else {
////                    dto.setState("complete");
////                }
////
////            }else {
////                dto.setState("notcomplete");
////            }
//        }
//
//        return dtos;
//    }
//
//    public OnboardOrderCheckListDTO toDTO(OnboardOrderCheckList obj){
//        OnboardOrderCheckListDTO dto = MapperUtils.map(obj, OnboardOrderCheckListDTO.class);
//        return dto;
//    }
//}
//*/
