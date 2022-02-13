package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.share.dto.*;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardTrainingService {
    private final OnboardTrainingRepo repo;
    private final ExecuteHcmService hcmService;
    private final ExecuteConfigService configService;
    private final OnboardOrderRepo onboardOrderRepo;
    private final OnboardTrainingItemRepo itemTranningRepo;
    private final OnboardTrainingTemplateRepo templateRepo;
    private final OnboardTrainingTemplateItemRepo itemRepo;
    private final OnboardTrainingTemplateItemChildrenRepo childrenRepo;
    private final OnboardTrainingTemplateItemGrandChildRepo grandChildRepo;

    public OnboardTrainingService (OnboardTrainingRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService, OnboardOrderRepo onboardOrderRepo, OnboardTrainingItemRepo itemTranningRepo, OnboardTrainingTemplateRepo templateRepo, OnboardTrainingTemplateItemRepo itemRepo, OnboardTrainingTemplateItemChildrenRepo childrenRepo, OnboardTrainingTemplateItemGrandChildRepo grandChildRepo){
        this.repo = repo;
        this.hcmService = hcmService;
        this.configService = configService;
        this.onboardOrderRepo = onboardOrderRepo;
        this.itemTranningRepo = itemTranningRepo;
        this.templateRepo = templateRepo;
        this.itemRepo= itemRepo;
        this.childrenRepo = childrenRepo;
        this.grandChildRepo = grandChildRepo;
    }

    public OnboardTrainingDTO createByOnboardOrder (Long cid, String uid, Long onboardOrderId){
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, onboardOrderId).orElseThrow(()-> new BusinessException("not found OnboardOder"));
        return createByPositionTitle(cid, uid, ja.getPositionId(), ja.getTitleId(), ja.getEmployeeId(), onboardOrderId);
    }

    public OnboardTrainingDTO createByPositionTitle(Long cid, String uid, Long positionId, Long titleId, Long employeeId, Long onboardOrderId){
        OnboardTraining training = repo.findByCompanyIdAndOnboardOrderId(cid, onboardOrderId).orElse(new OnboardTraining());
        if (!training.isNew()){
            return toDTOs(cid, uid, Collections.singletonList(training)).get(0);
        }
        training.setCompanyId(cid);
        training.setCreateBy(uid);
        training.setUpdateBy(uid);
        training.setStatus(Constants.ENTITY_ACTIVE);
        training.setEmployeeId(employeeId);
        training.setOnboardOrderId(onboardOrderId);

        training = repo.save(training);
        List<OnboardTrainingTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, titleId);
        OnboardTrainingTemplate template = templates.get(0);

        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());

        for (OnboardTrainingTemplateItem item: items) {

            OnboardTrainingItemDTO trainingDTO = new OnboardTrainingItemDTO();
            trainingDTO.setOnboardTrainingId(training.getId());
            trainingDTO.setItemId(item.getId());
            createItem(cid, uid, trainingDTO);

            List<OnboardTrainingTemplateItemChildren> childrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemId(cid, template.getId(), item.getId());
            for (OnboardTrainingTemplateItemChildren children: childrens){
                trainingDTO.setItemChildId(children.getId());
                createItem(cid, uid, trainingDTO);
                List<OnboardTrainingTemplateItemGrandChild> grandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenId(cid, template.getId(), item.getId(), children.getId());
                for (OnboardTrainingTemplateItemGrandChild grandChild: grandChildrens) {
                    trainingDTO.setItemGrandChildId(grandChild.getId());
                    createItem(cid, uid, trainingDTO);
                }
            }

        }
        return toDTOs(cid, uid, Collections.singletonList(training)).get(0);
    }

    public void createItem(Long cid, String uid, OnboardTrainingItemDTO dto) throws BusinessException{
        OnboardTrainingItem exists = itemTranningRepo.findByCompanyIdAndOnboardTrainingIdAndStatusAndItemIdAndItemChildIdAndItemGrandChildId(cid, dto.getOnboardTrainingId(), Constants.ENTITY_ACTIVE, dto.getItemId(), dto.getItemChildId(), dto.getItemGrandChildId()).orElse(new OnboardTrainingItem());
        if(exists.isNew()){
            OnboardTrainingItem obj = OnboardTrainingItem.of(cid, uid, dto);
            obj.setStatus(Constants.ENTITY_ACTIVE);
            obj.setCompanyId(cid);
            obj.setUpdateBy(uid);
            obj.setCreateBy(uid);
            itemTranningRepo.save(obj);
        }
    }




    public OnboardTrainingDTO update(Long cid, String uid, Long onboardId, OnboardTrainingDTO dto) throws BusinessException{
        dto.setOnboardOrderId(onboardId);

        OnboardTraining training = repo.findByCompanyIdAndId(cid, dto.getId()).orElseThrow(() -> new EntityNotFoundException(OnboardTraining.class, dto.getId()));
        MapperUtils.copyWithoutAudit(dto, training);
        training.setUpdateBy(uid);
        for (OnboardTrainingItemDTO itemDTO: dto.getItems()) {
            if(CompareUtil.compare(dto.getStatus(), Constants.ENTITY_INACTIVE)){
                itemDTO.setStatus(Constants.ENTITY_INACTIVE);
            }
            itemDTO.setOnboardTrainingId(training.getId());
            updateItem(cid, uid, itemDTO.getId(), itemDTO);
        }
        training = repo.save(training);

        return toDTOs(cid, uid, Collections.singletonList(training)).get(0);
    }

    public void updateItem(Long cid, String uid, Long id, OnboardTrainingItemDTO request) throws BusinessException{
            OnboardTrainingItem curr = itemTranningRepo.findByCompanyIdAndId(cid, request.getId()).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            curr = itemTranningRepo.save(curr);
    }

    public List<OnboardTrainingDTO> toDTOs(Long cid, String uid, List<OnboardTraining> objs){
        List<OnboardTrainingDTO> dtos = new ArrayList<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> employeeIdsForOnboard = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemChildIds = new HashSet<>();
        Set<Long> onboardTraningIds = new HashSet<>();
        OnboardTraining ot = objs.get(0);
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, ot.getOnboardOrderId()).orElseThrow(()-> new BusinessException("not found OnboardOder"));
        List<OnboardTrainingTemplate> templates = templateRepo.searchConfigTemplate(cid, ja.getPositionId(), ja.getTitleId());
        OnboardTrainingTemplate template = templates.get(0);

        objs.forEach(o -> {
            if(o.getEmployeeId() != null){
                employeeIdsForOnboard.add(o.getEmployeeId());
            }
            if(o.getId() != null){
                onboardTraningIds.add(o.getId());
            }
            dtos.add(toDTO(o));
        });
        List<OnboardTrainingItem> trainingItems = itemTranningRepo.findByCompanyIdAndOnboardTrainingIdIn(cid, onboardTraningIds);
        Map<Long, List<OnboardTrainingItem>> mapItemTranings = trainingItems.stream().collect(Collectors.groupingBy(OnboardTrainingItem::getOnboardTrainingId));

        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());
        Map<Long, List<OnboardTrainingTemplateItem>> mapItems = items.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItem::getTemplateId));
        itemIds = items.stream().map(el -> el.getId()).collect(Collectors.toSet());

        List<OnboardTrainingTemplateItemChildren> childrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdIn(cid, template.getId(), itemIds);
        Map<Long, List<OnboardTrainingTemplateItemChildren>> mapItemChildrens = childrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemChildren::getItemId));
        itemChildIds = childrens.stream().map(el -> el.getId()).collect(Collectors.toSet());

        List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdInAndItemChildrenIdIn(cid, template.getId(), itemIds, itemChildIds);
        Map<Long, List<OnboardTrainingTemplateItemGrandChild>> mapItemGrandChildrens = itemGrandChildrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemGrandChild::getItemChildrenId));
        employeeIds = itemGrandChildrens.stream().map(el -> el.getEmployeeId()).collect(Collectors.toSet());

        List<EmployeeDTO> employeeDTOS = hcmService.getEmployees(uid, cid, employeeIds);
        Map<Long, EmployeeDTO> mapEmployee = hcmService.getMapEmployees(uid, cid, employeeIdsForOnboard);
        if (template.getId() != null){
            for (OnboardTrainingDTO dto: dtos) {
                if(dto.getEmployeeId() != null){
                    dto.setEmployeeObj(mapEmployee.get(dto.getEmployeeId()));
                }
                if(mapItemTranings.get(dto.getId()) != null){
                    List<OnboardTrainingItem> itemTranings = new ArrayList<>();
                    List<OnboardTrainingItemDTO> lstTraningDTO = new ArrayList<>();
                    for (OnboardTrainingItem itemTraning: mapItemTranings.get(dto.getId())) {
                        OnboardTrainingItemDTO trainingItemDTO = new OnboardTrainingItemDTO();
                        MapperUtils.copy(itemTraning, trainingItemDTO);
                        if (trainingItemDTO != null){
                            lstTraningDTO.add(trainingItemDTO);
                        }
                    }
                    for (OnboardTrainingItemDTO itemDTO: lstTraningDTO) {
                        if (itemDTO.getItemId() != null && itemDTO.getItemChildId() == null && itemDTO.getItemGrandChildId() == null){
                            if (mapItems.get(template.getId()) != null){
                                for (OnboardTrainingTemplateItem lst: mapItems.get(template.getId())) {
                                    OnboardTrainingTemplateItemDTO item = new OnboardTrainingTemplateItemDTO();
                                    MapperUtils.copy(lst, item);
                                    if(item != null && lst.getId().equals(itemDTO.getItemId())){
                                        itemDTO.setItem(item);
                                    }
                                }
                            }
                        }
                        if(itemDTO.getItemId() != null && itemDTO.getItemChildId() != null && itemDTO.getItemGrandChildId() == null){
                            if(itemIds != null){
                                for (Long idChild: itemIds) {
                                    if(mapItemChildrens.get(idChild) != null){
                                        for (OnboardTrainingTemplateItemChildren lst: mapItemChildrens.get(idChild)) {
                                            OnboardTrainingTemplateItemChildrenDTO item = new OnboardTrainingTemplateItemChildrenDTO();
                                            MapperUtils.copy(lst, item);
                                            if(item != null && lst.getId().equals(itemDTO.getItemChildId())){
                                                itemDTO.setItemChildren(item);
                                            }
                                        }

                                    }
                                }
                            }
                        }

                        if(itemDTO.getItemId() != null && itemDTO.getItemChildId() != null && itemDTO.getItemGrandChildId() != null){
                            if(itemGrandChildrens != null){
                                for (Long idGrandChild: itemChildIds) {
                                    if(mapItemGrandChildrens.get(idGrandChild) != null){
                                        for (OnboardTrainingTemplateItemGrandChild lst: mapItemGrandChildrens.get(idGrandChild)) {
                                            OnboardTrainingTemplateItemGrandChildDTO item = new OnboardTrainingTemplateItemGrandChildDTO();
                                            MapperUtils.copy(lst, item);
                                            if(item != null && lst.getId().equals(itemDTO.getItemGrandChildId())){
                                                itemDTO.setItemGrandChild(item);
                                                if(item.getEmployeeId() != null){
                                                    item.setEmployeeObj(employeeDTOS.stream().filter(e -> {
                                                        return CompareUtil.compare(e.getId(), item.getEmployeeId());
                                                    }).findAny().orElse(null) );
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    dto.setItems(lstTraningDTO);
                }

            }
        }
        return dtos;
    }

    public OnboardTrainingDTO toDTO(OnboardTraining obj){
        return MapperUtils.map(obj, OnboardTrainingDTO.class);
    }

}
