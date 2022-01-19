package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplateItem;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileCheckListTemplateService {
    private final ProfileCheckListTemplateRepo repo;
    private final ProfileCheckListTemplateItemRepo itemRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public ProfileCheckListTemplateService(ProfileCheckListTemplateRepo repo, ProfileCheckListTemplateItemRepo itemRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(ProfileCheckListTemplateDTO dto){
    }

    public void validItem(ProfileCheckListTemplateItemDTO dto){
    }

    public ProfileCheckListTemplateDTO create(Long cid, String uid, ProfileCheckListTemplateDTO request) throws BusinessException {
        valid(request);
        // create template
        ProfileCheckListTemplate template = ProfileCheckListTemplate.of(cid, uid, request);
        template.setCompanyId(cid);
        template.setCreateBy(uid);
        template.setUpdateBy(uid);
        template.setStatus(Constants.ENTITY_ACTIVE);

        template = repo.save(template);
        //create template item
        for(ProfileCheckListTemplateItemDTO itemDTO : request.getItems()){
            itemDTO.setTemplateId(template.getId());
            createItem(cid, uid, itemDTO);
        }

        return toDTOs(cid, uid, Collections.singletonList(template)).get(0);
    }

    public void createItem(Long cid, String uid, ProfileCheckListTemplateItemDTO request) throws BusinessException {
        validItem(request);
        // create template
        ProfileCheckListTemplateItem item = ProfileCheckListTemplateItem.of(cid, uid, request);
        item.setCompanyId(cid);
        item.setCreateBy(uid);
        item.setUpdateBy(uid);
        item.setStatus(Constants.ENTITY_ACTIVE);

        item = itemRepo.save(item);
    }

    public ProfileCheckListTemplateDTO update(Long cid, String uid, Long id, ProfileCheckListTemplateDTO request) throws BusinessException{
        valid(request);
        ProfileCheckListTemplate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);

        for(ProfileCheckListTemplateItemDTO itemDTO : request.getItems()){
            if(CompareUtil.compare(request.getStatus(), Constants.ENTITY_INACTIVE)){
                itemDTO.setStatus(Constants.ENTITY_INACTIVE);
            }
            itemDTO.setTemplateId(request.getId());
            updateItem(cid, uid, itemDTO.getId(), itemDTO);
        }

        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    public void updateItem(Long cid, String uid, Long id, ProfileCheckListTemplateItemDTO request) throws BusinessException{
        validItem(request);
        if(request.getId() != 0l && request.getId() != null){
            ProfileCheckListTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            curr = itemRepo.save(curr);
        }else{
            createItem(cid, uid, request);
        }
    }

    public List<ProfileCheckListTemplateDTO> toDTOs(Long cid, String uid, List<ProfileCheckListTemplate> objs){
        List<ProfileCheckListTemplateDTO> dtos = new ArrayList<>();
        Set<Long> templateIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        objs.forEach(o -> {
            if(o.getPositionId() != null){
                categoryIds.add(o.getPositionId());
            }
            if(o.getTitleId() != null){
                categoryIds.add(o.getTitleId());
            }
            if(o.getContractTypeId() != null){
                categoryIds.add(o.getContractTypeId());
            }
            templateIds.add(o.getId());
        });

        List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdInAndStatus(cid, templateIds, Constants.ENTITY_ACTIVE);
        items.forEach(i -> {
            if(i.getChecklistId() != null){
                categoryIds.add(i.getChecklistId());
            }
        });

        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(ProfileCheckListTemplate obj : objs){
            ProfileCheckListTemplateDTO o = toDTO(obj);

            if(o.getPositionId() != null){
                o.setPositionObj(mapCategory.get(o.getPositionId()));
            }
            if(o.getTitleId() != null){
                o.setTitleObj(mapCategory.get(o.getTitleId()));
            }
            if(o.getContractTypeId() != null){
                o.setContractTypeObj(mapCategory.get(o.getContractTypeId()));
            }

            List<ProfileCheckListTemplateItemDTO> itemDTOs = new ArrayList<>();
            items.stream().filter(i -> CompareUtil.compare(i.getTemplateId(), obj.getId()))
                    .collect(Collectors.toList()).stream().forEach(i -> {
                        ProfileCheckListTemplateItemDTO itemDTO = MapperUtils.map(i, ProfileCheckListTemplateItemDTO.class);
                        if(itemDTO.getChecklistId() != null){
                            itemDTO.setCheckListObj(mapCategory.get(itemDTO.getChecklistId()));
                        }
                        itemDTOs.add(itemDTO);
                    });

            o.setItems(itemDTOs);
            dtos.add(o);
        }
        return dtos;
    }

    public ProfileCheckListTemplateDTO toDTO(ProfileCheckListTemplate obj){
        return MapperUtils.map(obj, ProfileCheckListTemplateDTO.class);
    }
}
