package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
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
        if (StringUtils.isEmpty(request.getName())){
            throw new BusinessException("invalid-name");
        }
        if (request.getCompletion() < 0 && request.getCompletion() > 100){
            throw new BusinessException("range completion 0-100");
        }
    }

    public void validItemChild(OnboardTrainingTemplateItemChildrenDTO request){
        if (StringUtils.isEmpty(request.getName())){
            throw new BusinessException("invalid-name");
        }
        if (request.getCompletion() < 0 && request.getCompletion() > 100){
            throw new BusinessException("range completion 0-100");
        }
    }
    public void validItemGrandChild(OnboardTrainingTemplateItemGrandChildDTO request){
        if (StringUtils.isEmpty(request.getName())){
            throw new BusinessException("invalid-name");
        }
        if (request.getCompletion() < 0 && request.getCompletion() > 100){
            throw new BusinessException("range completion 0-100");
        }
    }

    public List<Map<String, Object>>  grant(Long cid, String uid, Long templateId, List<Map<String, Object>> newDatas) throws BusinessException{
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemChildIds = new HashSet<>();
        OnboardTrainingTemplate template = repo.findByCompanyIdAndId(cid, templateId).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplate.class, templateId));
        if(template != null && !template.isNew()){
            for (Map<String, Object> data: newDatas) {

                Long positionId = MapUtils.getLong(data, "positionId", 0l);
                Long titileId = MapUtils.getLong(data, "titleId", 0l);
                Long orgId = MapUtils.getLong(data, "orgId", 0l);


                List<OnboardTrainingTemplate> existeds = repo.findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(cid, positionId, titileId, orgId, Constants.ENTITY_ACTIVE);
                if (existeds.size() >= 1){
                    for (OnboardTrainingTemplate existed: existeds) {
                        existed.setStatus(Constants.ENTITY_INACTIVE);
                        existed = repo.save(existed);

                        OnboardTrainingTemplateDTO dto = new OnboardTrainingTemplateDTO();
                        MapperUtils.copyWithoutAudit(template, dto);
                        dto.setPositionId(positionId);
                        dto.setTitleId(titileId);
                        dto.setOrgId(orgId);


                        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, templateId, Constants.ENTITY_ACTIVE);
                        Map<Long, List<OnboardTrainingTemplateItem>> mapItems = items.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItem::getTemplateId));
                        itemIds = items.stream().map(el -> el.getId()).collect(Collectors.toSet());

                        List<OnboardTrainingTemplateItemChildren> childrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdIn(cid, template.getId(), itemIds);
                        Map<Long, List<OnboardTrainingTemplateItemChildren>> mapItemChildrens = childrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemChildren::getItemId));
                        itemChildIds = childrens.stream().map(el -> el.getId()).collect(Collectors.toSet());

                        List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdInAndItemChildrenIdIn(cid, template.getId(), itemIds, itemChildIds);
                        Map<Long, List<OnboardTrainingTemplateItemGrandChild>> mapItemGrandChildrens = itemGrandChildrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemGrandChild::getItemChildrenId));

                        List<OnboardTrainingTemplateItemDTO> lstItemDTO = new ArrayList<>();
                        List<OnboardTrainingTemplateItemChildrenDTO> lstChilDTO = new ArrayList<>();

                        if (mapItems.get(templateId) != null){
                            List<OnboardTrainingTemplateItemDTO> lstItem = new ArrayList<>();
                            if (mapItems.get(templateId) != null){
                                for (OnboardTrainingTemplateItem lst: mapItems.get(templateId)) {
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
                                                }
                                                if(lstGrandChild != null){
                                                    child.setChildren(lstGrandChild);
                                                }
                                            }

                                        }
                                    }
                                }
                                dto.setChildren(lstItem);
                            }
                        }
                        create(cid, uid, dto);


                    }
                } else {
                    OnboardTrainingTemplateDTO dto = new OnboardTrainingTemplateDTO();
                    MapperUtils.copyWithoutAudit(template, dto);
                    dto.setPositionId(positionId);
                    dto.setTitleId(titileId);
                    dto.setOrgId(orgId);

                    List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, templateId, Constants.ENTITY_ACTIVE);
                    Map<Long, List<OnboardTrainingTemplateItem>> mapItems = items.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItem::getTemplateId));
                    itemIds = items.stream().map(el -> el.getId()).collect(Collectors.toSet());

                    List<OnboardTrainingTemplateItemChildren> childrens = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdIn(cid, template.getId(), itemIds);
                    Map<Long, List<OnboardTrainingTemplateItemChildren>> mapItemChildrens = childrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemChildren::getItemId));
                    itemChildIds = childrens.stream().map(el -> el.getId()).collect(Collectors.toSet());

                    List<OnboardTrainingTemplateItemGrandChild> itemGrandChildrens = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdInAndItemChildrenIdIn(cid, template.getId(), itemIds, itemChildIds);
                    Map<Long, List<OnboardTrainingTemplateItemGrandChild>> mapItemGrandChildrens = itemGrandChildrens.stream().collect(Collectors.groupingBy(OnboardTrainingTemplateItemGrandChild::getItemChildrenId));

                    List<OnboardTrainingTemplateItemDTO> lstItemDTO = new ArrayList<>();
                    List<OnboardTrainingTemplateItemChildrenDTO> lstChilDTO = new ArrayList<>();

                    if (mapItems.get(templateId) != null){
                        List<OnboardTrainingTemplateItemDTO> lstItem = new ArrayList<>();
                        if (mapItems.get(templateId) != null){
                            for (OnboardTrainingTemplateItem lst: mapItems.get(templateId)) {
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
                                            }
                                            if(lstGrandChild != null){
                                                child.setChildren(lstGrandChild);
                                            }
                                        }

                                    }
                                }
                            }
                            dto.setChildren(lstItem);
                        }
                    }
                    create(cid, uid, dto);
                }
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("positionId", template.getPositionId());
        data.put("titleId", template.getTitleId());
        data.put("orgId", template.getOrgId());
        newDatas.add(data);
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
        if (request.getChildren() != null && !request.getChildren().isEmpty()){
            for (OnboardTrainingTemplateItemDTO itemDTO: request.getChildren()){
                itemDTO.setTemplateId(template.getId());
                createItem(cid, uid, itemDTO);
            }
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
        if (request.getChildren() != null && !request.getChildren().isEmpty()){
            float sumCompletion = 0;
            for (OnboardTrainingTemplateItemChildrenDTO childrenDTO: request.getChildren()){
                sumCompletion += childrenDTO.getCompletion();
                childrenDTO.setTemplateId(item.getTemplateId());
                childrenDTO.setItemId(item.getId());
                createItemChildren(cid, uid, childrenDTO);
            }
        }

    }

    public void createItemChildren (Long cid, String uid, OnboardTrainingTemplateItemChildrenDTO request) throws BusinessException {
        validItemChild(request);
        OnboardTrainingTemplateItemChildren children = OnboardTrainingTemplateItemChildren.of(cid, uid, request);
        children.setCompanyId(cid);
        children.setCreateBy(uid);
        children.setUpdateBy(uid);
        children.setStatus(Constants.ENTITY_ACTIVE);

        children = childrenRepo.save(children);
        if (request.getChildren() != null && !request.getChildren().isEmpty()){
            for (OnboardTrainingTemplateItemGrandChildDTO grandChildDTO: request.getChildren()){
                grandChildDTO.setTemplateId(children.getTemplateId());
                grandChildDTO.setItemId(children.getItemId());
                grandChildDTO.setItemChildrenId(children.getId());
                createItemCGrandChild(cid, uid, grandChildDTO);
            }
        }
    }

    public void createItemCGrandChild (Long cid, String uid, OnboardTrainingTemplateItemGrandChildDTO request) throws BusinessException {
        validItemGrandChild(request);
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
            List<OnboardTrainingTemplateItem> lstItem = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, request.getId(), Constants.ENTITY_ACTIVE);
            List<Long> lstItemOfDto = request.getChildren().stream().map(dto -> dto.getId()).collect(Collectors.toList());
            List<Long> lstItemExists = lstItem.stream().map(el -> el.getId()).collect(Collectors.toList());

            List<Long> listCheckListIdForDelete = new ArrayList<>(lstItemExists);
            listCheckListIdForDelete.removeAll(lstItemOfDto);

            for (Long itemId: listCheckListIdForDelete) {
                OnboardTrainingTemplateItem item =  itemRepo.findByCompanyIdAndId(cid, itemId).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItem.class, itemId));
                item.setStatus(Constants.ENTITY_INACTIVE);
                item = itemRepo.save(item);
            }
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
        if(request.getId() != null && request.getId() != 0l){
            validItem(request);
            OnboardTrainingTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            if(request.getChildren() != null && !request.getChildren().isEmpty()) {
                List<OnboardTrainingTemplateItemChildren> lstItem = childrenRepo.findByCompanyIdAndTemplateIdAndItemIdAndStatus(cid, request.getTemplateId(), request.getId(), Constants.ENTITY_ACTIVE);
                List<Long> lstItemOfDto = request.getChildren().stream().map(dto -> dto.getId()).collect(Collectors.toList());
                List<Long> lstItemExists = lstItem.stream().map(el -> el.getId()).collect(Collectors.toList());

                List<Long> listCheckListIdForDelete = new ArrayList<>(lstItemExists);
                listCheckListIdForDelete.removeAll(lstItemOfDto);

                for (Long itemId: listCheckListIdForDelete) {
                    OnboardTrainingTemplateItemChildren item =  childrenRepo.findByCompanyIdAndId(cid, itemId).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItem.class, itemId));
                    item.setStatus(Constants.ENTITY_INACTIVE);
                    item = childrenRepo.save(item);
                }
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
        if(request.getId() != null  && request.getId() != 0l){
            validItemChild(request);
            OnboardTrainingTemplateItemChildren curr = childrenRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItemChildren.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            if(request.getChildren() != null && !request.getChildren().isEmpty()){

                List<OnboardTrainingTemplateItemGrandChild> lstItem = grandChildRepo.findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenIdAndStatus(cid, request.getTemplateId(), request.getItemId(), request.getId(), Constants.ENTITY_ACTIVE);
                List<Long> lstItemOfDto = request.getChildren().stream().map(dto -> dto.getId()).collect(Collectors.toList());
                List<Long> lstItemExists = lstItem.stream().map(el -> el.getId()).collect(Collectors.toList());

                List<Long> listCheckListIdForDelete = new ArrayList<>(lstItemExists);
                listCheckListIdForDelete.removeAll(lstItemOfDto);

                for (Long itemId: listCheckListIdForDelete) {
                    OnboardTrainingTemplateItemGrandChild item =  grandChildRepo.findByCompanyIdAndId(cid, itemId).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItemGrandChild.class, itemId));
                    item.setStatus(Constants.ENTITY_INACTIVE);
                    item = grandChildRepo.save(item);
                }

                for(OnboardTrainingTemplateItemGrandChildDTO grandChildDTO : request.getChildren()){
                    if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                        grandChildDTO.setStatus(Constants.ENTITY_INACTIVE);
                    }
                    grandChildDTO.setTemplateId(request.getTemplateId());
                    grandChildDTO.setItemId(request.getItemId());
                    grandChildDTO.setItemChildrenId(request.getId());
                    updateItemGrandChild(cid, uid, grandChildDTO.getId(), grandChildDTO);

                }
            }
            curr = childrenRepo.save(curr);
        }else {
            createItemChildren(cid, uid, request);
        }
    }

    public void updateItemGrandChild(Long cid, String uid, Long id, OnboardTrainingTemplateItemGrandChildDTO request) throws BusinessException{
        if(request.getId() != null && request.getId() != 0l){
            validItemGrandChild(request);
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
