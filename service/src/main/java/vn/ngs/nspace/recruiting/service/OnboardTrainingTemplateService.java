package vn.ngs.nspace.recruiting.service;

import org.camunda.bpm.engine.BadUserRequestException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateItemChildrenRepo;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateItemGrandChildRepo;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateRepo;
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

    public OnboardTrainingTemplateService(OnboardTrainingTemplateRepo repo, OnboardTrainingTemplateItemRepo itemRepo, OnboardTrainingTemplateItemChildrenRepo childrenRepo, OnboardTrainingTemplateItemGrandChildRepo grandChildRepo,  ExecuteHcmService _hcmService, ExecuteConfigService _configService){
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

    public OnboardTrainingTemplateDTO create(Long cid, String uid, OnboardTrainingTemplateDTO request) throws BusinessException {
        valid(request);

        // create template
        OnboardTrainingTemplate template = OnboardTrainingTemplate.of(cid, uid, request);
        template.setCompanyId(cid);
        template.setCreateBy(uid);
        template.setUpdateBy(uid);
        template.setStatus(Constants.ENTITY_ACTIVE);

        template = repo.save(template);

        for (OnboardTrainingTemplateItemDTO itemDTO: request.getItems()){
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

        for(OnboardTrainingTemplateItemDTO itemDTO : request.getItems()){
            if(CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                itemDTO.setStatus(Constants.ENTITY_INACTIVE);
            }
            itemDTO.setTemplateId(request.getId());
            updateItem(cid, uid, itemDTO.getId(), itemDTO);
        }

        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    public void updateItem(Long cid, String uid, Long id, OnboardTrainingTemplateItemDTO request) throws BusinessException{
        if(request.getId() != 0l && request.getId() != null){
            OnboardTrainingTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardTrainingTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);

            for(OnboardTrainingTemplateItemChildrenDTO childrenDTO : request.getChildren()){
                if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                    childrenDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                childrenDTO.setTemplateId(request.getTemplateId());
                childrenDTO.setItemId(request.getId());
                updateItemChildren(cid, uid, childrenDTO.getId(), childrenDTO);
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

            for(OnboardTrainingTemplateItemGrandChildDTO grandChildDTO : request.getChildren()){
                if (CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                    grandChildDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                grandChildDTO.setTemplateId(request.getTemplateId());
                grandChildDTO.setItemId(request.getItemId());
                grandChildDTO.setItemChildrenId(request.getItemId());
                updateItemGrandChild(cid, uid, grandChildDTO.getId(), grandChildDTO);
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
                                    }
                                    if(lstGrandChild != null){
                                        child.setChildren(lstGrandChild);
                                    }
                                }

                            }
                        }
                    }
                    o.setItems(lstItem);
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
