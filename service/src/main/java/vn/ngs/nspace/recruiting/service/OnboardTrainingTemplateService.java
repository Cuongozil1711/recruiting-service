package vn.ngs.nspace.recruiting.service;

import org.camunda.bpm.engine.BadUserRequestException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
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
public class OnboardTrainingTemplateService {
    private final OnboardTrainingTemplateRepo repo;
    private final OnboardTrainingTemplateItemRepo itemRepo;
    private final OnboardTrainingTemplateItemChildrenRepo childrenRepo;
    private final OnboardTrainingTemplateItemGrandChildRepo grandChildRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;


    public OnboardTrainingTemplateService(OnboardTrainingTemplateRepo repo, OnboardTrainingTemplateItemRepo itemRepo, OnboardTrainingTemplateItemChildrenRepo childrenRepo, OnboardTrainingTemplateItemGrandChildRepo grandChildRepo,  ExecuteHcmService _hcmService, ExecuteConfigService _configService, OnboardContractRepo _contactRepo){
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.childrenRepo = childrenRepo;
        this.grandChildRepo = grandChildRepo;
        this._hcmService = _hcmService;
        this._configService = _configService;

    }

    public void valid(OnboardTrainingTemplateDTO request){

    }

    public void validItem(OnboardTrainingTemplateItemDTO request){

    }

    public List<Map<String, Object>> grant(Long cid, String uid, Long templateId, List<Map<String, Object>> newDatas) throws BusinessException{
        OnboardTrainingTemplate template = repo.findByCompanyIdAndId(cid, templateId).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplate.class, templateId));
        for (Map<String, Object> data: newDatas) {
            Long positionId = MapUtils.getLong(data, "positionId", 0l);
            Long titileId = MapUtils.getLong(data, "titleId", 0l);
            Long orgId = MapUtils.getLong(data, "orgId", 0l);

            List<OnboardTrainingTemplate> existeds = repo.findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(cid, positionId, titileId, orgId, Constants.ENTITY_ACTIVE);
            if (existeds.size() >= 1){
                for (OnboardTrainingTemplate existed: existeds) {
                    existed.setStatus(Constants.ENTITY_INACTIVE);
                    existed = repo.save(existed);
                    template.setPositionId(positionId);
                    template.setTitleId(titileId);
                    OnboardTrainingTemplateDTO dto = new OnboardTrainingTemplateDTO();
                    MapperUtils.copyWithoutAudit(template, dto);

                    List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, templateId, Constants.ENTITY_ACTIVE);
                    if(!items.isEmpty()){

                        List<OnboardTrainingTemplateItemDTO> itemDTOS = new ArrayList<>();
                        List<OnboardTrainingTemplateItemChildrenDTO> itemChildrenDTOS = new ArrayList<>();
                        List<OnboardTrainingTemplateItemGrandChildDTO> grandChildDTOS = new ArrayList<>();

                        for (OnboardTrainingTemplateItem item: items) {
                            OnboardTrainingTemplateItemDTO itemDTO = new OnboardTrainingTemplateItemDTO();
                            MapperUtils.copyWithoutAudit(item, itemDTO);
                            itemDTOS.add(itemDTO);
                            List<OnboardTrainingTemplateItemChildren> itemChildrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdAndStatus(cid, templateId, item.getId(), Constants.ENTITY_ACTIVE);
                            if (!itemChildrens.isEmpty()){
                                for (OnboardTrainingTemplateItemChildren itemChildren: itemChildrens) {
                                    OnboardTrainingTemplateItemChildrenDTO childrenDTO = new OnboardTrainingTemplateItemChildrenDTO();
                                    MapperUtils.copyWithoutAudit(itemChildren, childrenDTO);
                                    itemChildrenDTOS.add(childrenDTO);
                                    List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenIdAndStatus(cid, templateId, item.getId(), itemChildren.getId(), Constants.ENTITY_ACTIVE);
                                    if (!itemGrandChildrens.isEmpty()){

                                        for (OnboardTrainingTemplateItemGrandChild itemGrandChild: itemGrandChildrens) {
                                            OnboardTrainingTemplateItemGrandChildDTO itemGrandChildDTO = new OnboardTrainingTemplateItemGrandChildDTO();
                                            MapperUtils.copyWithoutAudit(itemGrandChild, itemGrandChildDTO);
                                            grandChildDTOS.add(itemGrandChildDTO);
                                        }
                                        childrenDTO.setChildren(grandChildDTOS);
                                    }
                                }
                                itemDTO.setChildren(itemChildrenDTOS);
                            }
                        }
                        dto.setChildren(itemDTOS);

                    }
                    create(cid, uid, dto);
                }

            } else {
                OnboardTrainingTemplateDTO dto = new OnboardTrainingTemplateDTO();
                List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, templateId, Constants.ENTITY_ACTIVE);
                if(!items.isEmpty()){

                    List<OnboardTrainingTemplateItemDTO> itemDTOS = new ArrayList<>();
                    List<OnboardTrainingTemplateItemChildrenDTO> itemChildrenDTOS = new ArrayList<>();
                    List<OnboardTrainingTemplateItemGrandChildDTO> grandChildDTOS = new ArrayList<>();

                    for (OnboardTrainingTemplateItem item: items) {
                        OnboardTrainingTemplateItemDTO itemDTO = new OnboardTrainingTemplateItemDTO();
                        MapperUtils.copyWithoutAudit(item, itemDTO);
                        itemDTOS.add(itemDTO);
                        List<OnboardTrainingTemplateItemChildren> itemChildrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdAndStatus(cid, templateId, item.getId(), Constants.ENTITY_ACTIVE);
                        if (!itemChildrens.isEmpty()){
                            for (OnboardTrainingTemplateItemChildren itemChildren: itemChildrens) {
                                OnboardTrainingTemplateItemChildrenDTO childrenDTO = new OnboardTrainingTemplateItemChildrenDTO();
                                MapperUtils.copyWithoutAudit(itemChildren, childrenDTO);
                                itemChildrenDTOS.add(childrenDTO);
                                List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenIdAndStatus(cid, templateId, item.getId(), itemChildren.getId(), Constants.ENTITY_ACTIVE);
                                if (!itemGrandChildrens.isEmpty()){

                                    for (OnboardTrainingTemplateItemGrandChild itemGrandChild: itemGrandChildrens) {
                                        OnboardTrainingTemplateItemGrandChildDTO itemGrandChildDTO = new OnboardTrainingTemplateItemGrandChildDTO();
                                        MapperUtils.copyWithoutAudit(itemGrandChild, itemGrandChildDTO);
                                        grandChildDTOS.add(itemGrandChildDTO);
                                    }
                                    childrenDTO.setChildren(grandChildDTOS);
                                }
                            }
                            itemDTO.setChildren(itemChildrenDTOS);
                        }
                    }
                    dto.setChildren(itemDTOS);
                }
                create(cid, uid, dto);
            }
        }
        return newDatas;
    }

    public OnboardTrainingTemplateDTO create(Long cid, String uid, OnboardTrainingTemplateDTO request) throws BusinessException {
        valid(request);

        // create template
        OnboardTrainingTemplate template = OnboardTrainingTemplate.of(cid, uid, request);
        template.setCompanyId(cid);
        template.setCreateBy(uid);
        template.setUpdateBy(uid);
        template.setStatus(Constants.ENTITY_ACTIVE);

        template = repo.save(template);

        for (OnboardTrainingTemplateItemDTO itemDTO: request.getChildren()){
            itemDTO.setTemplateId(template.getId());
            createItem(cid, uid, itemDTO);
        }
        return toDTOs(cid, uid, Collections.singletonList(template)).get(0);
    }

    public void createItem(Long cid, String uid, OnboardTrainingTemplateItemDTO request) throws BusinessException {
        validItem(request);
        // create template
        OnboardTrainingTemplateItem item = OnboardTrainingTemplateItem.of(cid, uid, request);
        item.setCompanyId(cid);
        item.setCreateBy(uid);
        item.setUpdateBy(uid);
        item.setStatus(Constants.ENTITY_ACTIVE);

        item = itemRepo.save(item);

        for (OnboardTrainingTemplateItemChildrenDTO childrenDTO: request.getChildren()){
            childrenDTO.setTemplateId(item.getTemplateId());
            childrenDTO.setItemId(item.getId());
            createItemChildren(cid, uid, childrenDTO);
        }
    }

    public void createItemChildren (Long cid, String uid, OnboardTrainingTemplateItemChildrenDTO request) throws BusinessException {
        OnboardTrainingTemplateItemChildren children = OnboardTrainingTemplateItemChildren.of(cid, uid, request);
        children.setCompanyId(cid);
        children.setCreateBy(uid);
        children.setUpdateBy(uid);
        children.setStatus(Constants.ENTITY_ACTIVE);

        children = childrenRepo.save(children);

        for (OnboardTrainingTemplateItemGrandChildDTO grandChildDTO: request.getChildren()){
            grandChildDTO.setTemplateId(children.getTemplateId());
            grandChildDTO.setItemId(children.getItemId());
            grandChildDTO.setItemChildrenId(children.getId());
            createItemCGrandChild(cid, uid, grandChildDTO);
        }
    }

    public void createItemCGrandChild (Long cid, String uid, OnboardTrainingTemplateItemGrandChildDTO request) throws BusinessException {
        OnboardTrainingTemplateItemGrandChild grandChild = OnboardTrainingTemplateItemGrandChild.of(cid, uid, request);
        grandChild.setCompanyId(cid);
        grandChild.setCreateBy(uid);
        grandChild.setUpdateBy(uid);
        grandChild.setStatus(Constants.ENTITY_ACTIVE);

        grandChild = grandChildRepo.save(grandChild);
    }

    public OnboardTrainingTemplateDTO update(Long cid, String uid, Long id, OnboardTrainingTemplateDTO request) throws BusinessException{
        OnboardTrainingTemplate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplate.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        if(request.getChildren() != null && !request.getChildren().isEmpty()) {
            for (OnboardTrainingTemplateItemDTO itemDTO : request.getChildren()) {
                if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)) {
                    itemDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                itemDTO.setTemplateId(request.getId());
                updateItem(cid, uid, itemDTO.getId(), itemDTO);
            }
        }
        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    public void updateItem(Long cid, String uid, Long id, OnboardTrainingTemplateItemDTO request) throws BusinessException{
        if(request.getId() != 0l && request.getId() != null){
            OnboardTrainingTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            if(request.getChildren() != null && !request.getChildren().isEmpty()) {
                for (OnboardTrainingTemplateItemChildrenDTO childrenDTO : request.getChildren()) {
                    if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)) {
                        childrenDTO.setStatus(Constants.ENTITY_INACTIVE);
                    }
                    childrenDTO.setTemplateId(request.getTemplateId());
                    childrenDTO.setItemId(request.getId());
                    updateItemChildren(cid, uid, childrenDTO.getId(), childrenDTO);
                }
            }
            curr = itemRepo.save(curr);
        }else {
            createItem(cid, uid, request);
        }
    }

    public void updateItemChildren(Long cid, String uid, Long id, OnboardTrainingTemplateItemChildrenDTO request) throws BusinessException{
        if(request.getId() != 0l && request.getId() != null){
            OnboardTrainingTemplateItemChildren curr = childrenRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItemChildren.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            if(request.getChildren() != null && !request.getChildren().isEmpty()){
                for(OnboardTrainingTemplateItemGrandChildDTO grandChildDTO : request.getChildren()){
                    if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                        grandChildDTO.setStatus(Constants.ENTITY_INACTIVE);
                    }
                    grandChildDTO.setTemplateId(request.getTemplateId());
                    grandChildDTO.setItemId(request.getItemId());
                    grandChildDTO.setItemChildrenId(request.getItemId());
                    updateItemGrandChild(cid, uid, grandChildDTO.getId(), grandChildDTO);
                }
            }
            curr = childrenRepo.save(curr);
        }else {
            createItemChildren(cid, uid, request);
        }
    }

    public void updateItemGrandChild(Long cid, String uid, Long id, OnboardTrainingTemplateItemGrandChildDTO request) throws BusinessException{
        if(request.getId() != 0l && request.getId() != null){
            OnboardTrainingTemplateItemGrandChild curr = grandChildRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItemGrandChild.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            curr = grandChildRepo.save(curr);
        } else {
            createItemCGrandChild(cid, uid, request);
        }
    }

    public void delete(Long cid, String uid, List<Long> ids){
        for (Long id: ids){
            OnboardTrainingTemplate temp = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplate.class, id));
            temp.setUpdateBy(uid);
            temp.setStatus(Constants.ENTITY_INACTIVE);
            repo.save(temp);
        }
    }

    public List<OnboardTrainingTemplateDTO> toDTOs(Long cid, String uid, List<OnboardTrainingTemplate> objs){
        List<OnboardTrainingTemplateDTO> dtos = new ArrayList<>();
        Set<Long> templateIds = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemChildIds = new HashSet<>();

        objs.forEach(o -> {
            if(o.getPositionId() != null){
                categoryIds.add(o.getPositionId());
            }
            if(o.getTitleId() != null){
                categoryIds.add(o.getTitleId());
            }
            if(o.getOrgId() != null){
                orgIds.add(o.getOrgId());
            }
            if(o.getLevelId() != null){
                categoryIds.add(o.getLevelId());
            }


            templateIds.add(o.getId());
        });



        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdInAndStatus(cid, templateIds, Constants.ENTITY_ACTIVE);
        Map<Long, List<OnboardTrainingTemplateItem>> mapItems = items.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItem::getTemplateId));

        itemIds = items.stream().map(el -> el.getId()).collect(Collectors.toSet());


        List<OnboardTrainingTemplateItemChildren> itemChildrens = childrenRepo.findByCompanyIdAndTemplateIdInAndItemIdInAndStatus(cid, templateIds, itemIds, Constants.ENTITY_ACTIVE);
        Map<Long, List<OnboardTrainingTemplateItemChildren>> mapItemChildrens = itemChildrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemChildren::getItemId));

        itemChildIds = itemChildrens.stream().map(el -> el.getId()).collect(Collectors.toSet());

        List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdInAndItemIdInAndItemChildrenIdInAndStatus(cid, templateIds, itemIds, itemChildIds, Constants.ENTITY_ACTIVE);
        Map<Long, List<OnboardTrainingTemplateItemGrandChild>> mapItemGrandChildrens = itemGrandChildrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemGrandChild::getItemChildrenId));

        employeeIds = itemGrandChildrens.stream().map(el -> el.getEmployeeId()).collect(Collectors.toSet());

        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        List<EmployeeDTO> employeeDTOS = _hcmService.getEmployees(uid, cid, employeeIds);
        List<OrgResp> orgs = _hcmService.getOrgResp(uid, cid, orgIds);
        for (OnboardTrainingTemplate obj: objs){
            OnboardTrainingTemplateDTO o = toDTO(obj);
            if(o.getPositionId() != null){
                o.setPositionObj(mapCategory.get(o.getPositionId()));
            }
            if(o.getTitleId() != null){
                o.setTitleObj(mapCategory.get(o.getTitleId()));
            }

            if(o.getOrgId() != null){
                OrgResp org = orgs.stream().filter(el -> CompareUtil.compare(el.getId(), o.getOrgId())).findAny().orElse(new OrgResp());
                o.setOrgResp(org);
            }
            if(o.getLevelId() != null){
                o.setLevelObj(mapCategory.get(o.getLevelId()));
            }
            if (mapItems.get(o.getId()) != null){
                List<OnboardTrainingTemplateItemDTO> lstItem = new ArrayList<>();
                if (mapItems.get(o.getId()) != null){
                    for (OnboardTrainingTemplateItem lst: mapItems.get(o.getId())) {
                        OnboardTrainingTemplateItemDTO item = new OnboardTrainingTemplateItemDTO();
                        lstItem.add(MapperUtils.copy(lst, item));
                    }
                }

                if(lstItem != null){
                    for (OnboardTrainingTemplateItemDTO it: lstItem) {
                        List<OnboardTrainingTemplateItemChildrenDTO> lstChild = new ArrayList<>();
                        if(mapItemChildrens.get(it.getId()) != null){
                            for (OnboardTrainingTemplateItemChildren lst: mapItemChildrens.get(it.getId())) {
                                OnboardTrainingTemplateItemChildrenDTO item = new OnboardTrainingTemplateItemChildrenDTO();
                                lstChild.add(MapperUtils.copy(lst, item));
                            }
                            if(lstChild != null){
                                it.setChildren(lstChild);
                            }
                        }
                        if(lstChild != null){
                            for (OnboardTrainingTemplateItemChildrenDTO child: lstChild) {
                                List<OnboardTrainingTemplateItemGrandChildDTO> lstGrandChild = new ArrayList<>();
                                if(mapItemGrandChildrens.get(child.getId()) != null){
                                    for (OnboardTrainingTemplateItemGrandChild lst: mapItemGrandChildrens.get(child.getId())) {
                                        OnboardTrainingTemplateItemGrandChildDTO item = new OnboardTrainingTemplateItemGrandChildDTO();
                                        lstGrandChild.add(MapperUtils.copy(lst, item));
                                        if(item.getEmployeeId() != null){
                                            item.setEmployeeObj(employeeDTOS.stream().filter(e -> {
                                                return CompareUtil.compare(e.getId(), item.getEmployeeId());
                                            }).findAny().orElse(null) );
                                        }
                                    }
                                    if(lstGrandChild != null){
                                        child.setChildren(lstGrandChild);
                                    }
                                }

                            }
                        }
                    }
                    o.setChildren(lstItem);
                }
            }

            dtos.add(o);
        }
        return dtos;
    }

    public OnboardTrainingTemplateDTO toDTO(OnboardTrainingTemplate obj){
        return MapperUtils.map(obj, OnboardTrainingTemplateDTO.class);
    }
}
