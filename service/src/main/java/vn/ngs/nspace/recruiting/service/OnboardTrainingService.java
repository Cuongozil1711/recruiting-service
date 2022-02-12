package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemChildrenDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemGrandChildDTO;
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
    private final OnboardTrainingTemplateRepo templateRepo;
    private final OnboardTrainingTemplateItemRepo itemRepo;
    private final OnboardTrainingTemplateItemChildrenRepo childrenRepo;
    private final OnboardTrainingTemplateItemGrandChildRepo grandChildRepo;

    public OnboardTrainingService (OnboardTrainingRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService, OnboardOrderRepo onboardOrderRepo, OnboardTrainingTemplateRepo templateRepo, OnboardTrainingTemplateItemRepo itemRepo, OnboardTrainingTemplateItemChildrenRepo childrenRepo, OnboardTrainingTemplateItemGrandChildRepo grandChildRepo){
        this.repo = repo;
        this.hcmService = hcmService;
        this.configService = configService;
        this.onboardOrderRepo = onboardOrderRepo;
        this.templateRepo = templateRepo;
        this.itemRepo= itemRepo;
        this.childrenRepo = childrenRepo;
        this.grandChildRepo = grandChildRepo;
    }

    public List<OnboardTrainingDTO> createByOnboardOrder(Long cid, String uid, Long onboardOrderId){
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, onboardOrderId).orElseThrow(()-> new BusinessException("not found OnboardOder"));
        return createByPositionTitle(cid, uid, ja.getPositionId(), ja.getTitleId(), ja.getEmployeeId(), onboardOrderId);
    }

    public List<OnboardTrainingDTO> createByPositionTitle(Long cid, String uid, Long positionId, Long titleId, Long employeeId, Long onboardOrderId){
        List<OnboardTrainingDTO> lstDTO = new ArrayList<>();

        List<OnboardTrainingTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, titleId);
        OnboardTrainingTemplate template = templates.get(0);

        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());

        for (OnboardTrainingTemplateItem item: items) {
            OnboardTrainingDTO trainingDTO = new OnboardTrainingDTO();
            trainingDTO.setOnboardOrderId(onboardOrderId);
            trainingDTO.setItemId(item.getId());
            trainingDTO.setEmployeeId(employeeId);
            lstDTO.add(create(cid, uid, trainingDTO));

            List<OnboardTrainingTemplateItemChildren> childrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemId(cid, template.getId(), item.getId());
            for (OnboardTrainingTemplateItemChildren children: childrens){
                trainingDTO.setItemChildId(children.getId());
                trainingDTO.setEmployeeId(employeeId);
                lstDTO.add(create(cid, uid, trainingDTO));
                List<OnboardTrainingTemplateItemGrandChild> grandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenId(cid, template.getId(), item.getId(), children.getId());
                for (OnboardTrainingTemplateItemGrandChild grandChild: grandChildrens) {
                    trainingDTO.setEmployeeId(employeeId);
                    trainingDTO.setItemGrandChildId(grandChild.getId());
                    lstDTO.add(create(cid, uid, trainingDTO));
                }
            }

            lstDTO.add(create(cid, uid, trainingDTO));
        }
        return lstDTO;
    }

    public OnboardTrainingDTO create(Long cid, String uid, OnboardTrainingDTO dto) throws BusinessException{
        OnboardTraining exists = repo.findByCompanyIdAndEmployeeIdAndStatusAndItemIdAndItemChildIdAndItemGrandChildId(cid, dto.getEmployeeId(), Constants.ENTITY_ACTIVE, dto.getItemId(), dto.getItemChildId(), dto.getItemGrandChildId()).orElse(new OnboardTraining());
        if(exists.isNew()){
            OnboardTraining obj = OnboardTraining.of(cid, uid, dto);
            obj.setStatus(Constants.ENTITY_ACTIVE);
            obj.setCompanyId(cid);
            obj.setUpdateBy(uid);
            obj.setCreateBy(uid);
            repo.save(obj);
            return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
        }
        else {
            return toDTOs(cid, uid, Collections.singletonList(exists)).get(0);
        }

    }



    public List<OnboardTrainingDTO> toDTOs(Long cid, String uid, List<OnboardTraining> objs){
        List<OnboardTrainingDTO> dtos = new ArrayList<>();
        Set<Long> itemGrandChildId = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemChildIds = new HashSet<>();

        OnboardTraining ot = objs.get(0);
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, ot.getOnboardOrderId()).orElseThrow(()-> new BusinessException("not found OnboardOder"));
        List<OnboardTrainingTemplate> templates = templateRepo.searchConfigTemplate(cid, ja.getPositionId(), ja.getTitleId());
        OnboardTrainingTemplate template = templates.get(0);

        objs.forEach(o -> {
            dtos.add(toDTO(o));
        });

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

        if (template.getId() != null){
            for (OnboardTrainingDTO dto: dtos) {
                if (dto.getItemId() != null && dto.getItemChildId() == null && dto.getItemGrandChildId() == null){
                    if (mapItems.get(template.getId()) != null){
                        for (OnboardTrainingTemplateItem lst: mapItems.get(template.getId())) {
                            OnboardTrainingTemplateItemDTO item = new OnboardTrainingTemplateItemDTO();
                            MapperUtils.copy(lst, item);
                            if(item != null && lst.getId().equals(dto.getItemId())){
                                dto.setItem(item);
                            }
                        }
                    }

                }

                if(dto.getItemId() != null && dto.getItemChildId() != null && dto.getItemGrandChildId() == null){
                    if(itemIds != null){
                        for (Long idChild: itemIds) {
                            if(mapItemChildrens.get(idChild) != null){
                                for (OnboardTrainingTemplateItemChildren lst: mapItemChildrens.get(idChild)) {
                                    OnboardTrainingTemplateItemChildrenDTO item = new OnboardTrainingTemplateItemChildrenDTO();
                                    MapperUtils.copy(lst, item);
                                    if(item != null && lst.getId().equals(dto.getItemChildId())){
                                        dto.setItemChildren(item);
                                    }
                                }

                            }
                        }
                    }
                }

                if(dto.getItemId() != null && dto.getItemChildId() != null && dto.getItemGrandChildId() != null){
                    if(itemGrandChildrens != null){
                        for (Long idGrandChild: itemChildIds) {
                            if(mapItemGrandChildrens.get(idGrandChild) != null){
                                for (OnboardTrainingTemplateItemGrandChild lst: mapItemGrandChildrens.get(idGrandChild)) {
                                    OnboardTrainingTemplateItemGrandChildDTO item = new OnboardTrainingTemplateItemGrandChildDTO();
                                    MapperUtils.copy(lst, item);
                                    if(item != null && lst.getId().equals(dto.getItemGrandChildId())){
                                        dto.setItemGrandChild(item);
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
        return dtos;
    }

    public OnboardTrainingDTO toDTO(OnboardTraining obj){
        return MapperUtils.map(obj, OnboardTrainingDTO.class);
    }
}
