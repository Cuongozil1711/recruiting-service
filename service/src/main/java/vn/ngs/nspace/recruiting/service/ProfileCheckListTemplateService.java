package vn.ngs.nspace.recruiting.service;

import io.netty.util.Constant;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateDTO;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCheckListTemplateService.class);

    public ProfileCheckListTemplateService(ProfileCheckListTemplateRepo repo, ProfileCheckListTemplateItemRepo itemRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(ProfileCheckListTemplateDTO dto){
//        if (StringUtils.isEmpty(dto.getName())){
//            throw new BusinessException("invalid-name");
//        }
//        if (dto.getPositionId() == null){
//            throw new BusinessException("invalid-position");
//        }
//        if (dto.getTitleId() == null){
//            throw new BusinessException("invalid-title");
//        }
//        if (StringUtils.isEmpty(dto.getContractType())){
//            throw new BusinessException("invalid-contractType");
//        }

    }

    public void validItem(ProfileCheckListTemplateItemDTO dto){
//        if (dto.getChecklistId() == null){
//            throw new BusinessException("invalid-checkList");
//        }
//        if (dto.getTemplateId() == null){
//            throw new BusinessException("invalid-template");
//        }
//        if (StringUtils.isEmpty(dto.getDescription())){
//            throw new BusinessException("invalid-description");
//        }

    }

    public List<Map<String, Object>> grant(Long cid, String uid, Long templateId, List<Map<String, Object>> newDatas) throws BusinessException{
        ProfileCheckListTemplate template = repo.findByCompanyIdAndId(cid, templateId).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, templateId));
        for (Map<String, Object> data: newDatas) {
            Long positionId = MapUtils.getLong(data, "positionId", 0l);
            Long titileId = MapUtils.getLong(data, "titleId", 0l);
            String contractType = MapUtils.getString(data, "contractType", "#");

            List<ProfileCheckListTemplate> existeds = repo.findByCompanyIdAndPositionIdAndTitleIdAndStatus(cid, positionId, titileId, Constants.ENTITY_ACTIVE);
            if(existeds.size() >= 1){ // da ton tai voi vi tri vai tro nay
                for (ProfileCheckListTemplate existed: existeds){
                    existed.setStatus(Constants.ENTITY_INACTIVE);
                    existed = repo.save(existed);

                    ProfileCheckListTemplateDTO dto = new ProfileCheckListTemplateDTO();
                    MapperUtils.copyWithoutAudit(template, dto);
                    dto.setPositionId(positionId);
                    dto.setTitleId(titileId);

                    List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, templateId, Constants.ENTITY_ACTIVE);
                    List<ProfileCheckListTemplateItemDTO> itemDTOS = new ArrayList<>();
                    for (ProfileCheckListTemplateItem item: items) {
                        ProfileCheckListTemplateItemDTO itemDTO = new ProfileCheckListTemplateItemDTO();
                        MapperUtils.copyWithoutAudit(item, itemDTO);
                        itemDTOS.add(itemDTO);
                    }
                    dto.setItems(itemDTOS);
                    create(cid, uid, dto);

                }
            }
            else{ // neu chua co
                ProfileCheckListTemplateDTO dto = new ProfileCheckListTemplateDTO();
                MapperUtils.copyWithoutAudit(template, dto);
                dto.setPositionId(positionId);
                dto.setTitleId(titileId);

                List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, template.getId(), Constants.ENTITY_ACTIVE);
                List<ProfileCheckListTemplateItemDTO> itemDTOS = new ArrayList<>();
                MapperUtils.copyWithoutAudit(items, itemDTOS);
                dto.setItems(itemDTOS);
                create(cid, uid, dto);
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("positionId", template.getPositionId());
        data.put("titleId", template.getTitleId());
        data.put("contractType", template.getContractType());
        newDatas.add(data);
        return newDatas;
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

        List<ProfileCheckListTemplateItem> lstItem = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, request.getId(), Constants.ENTITY_ACTIVE);
        List<Long> lstItemOfDto = request.getItems().stream().map(dto -> dto.getId()).collect(Collectors.toList());
        List<Long> lstItemExists = lstItem.stream().map(el -> el.getId()).collect(Collectors.toList());

        List<Long> listCheckListIdForDelete = new ArrayList<>(lstItemExists);
        listCheckListIdForDelete.removeAll(lstItemOfDto);

        for (Long itemId: listCheckListIdForDelete) {
            ProfileCheckListTemplateItem item =  itemRepo.findByCompanyIdAndId(cid, itemId).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplateItem.class, itemId));
            item.setStatus(Constants.ENTITY_INACTIVE);
            item = itemRepo.save(item);
        }
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

    public ProfileCheckListTemplateDTO updateStatus(Long cid, String uid, Long id, ProfileCheckListTemplateDTO request) throws BusinessException{
        valid(request);
        ProfileCheckListTemplate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr.setStatus(request.getStatus() == null ? Constants.ENTITY_ACTIVE : request.getStatus());
        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    public void updateItem(Long cid, String uid, Long id, ProfileCheckListTemplateItemDTO request) throws BusinessException{
        validItem(request);
        if(request.getId() != null || request.getId() == 0l){
            ProfileCheckListTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            curr.setStatus(request.getStatus() == null ? Constants.ENTITY_ACTIVE : request.getStatus());
            curr = itemRepo.save(curr);
        }else{
            createItem(cid, uid, request);
        }
    }
                    
    public void delete(Long cid, String uid, List<Long> ids){
        for (Long id: ids){
            ProfileCheckListTemplate temp = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, id));
            temp.setUpdateBy(uid);
            temp.setStatus(Constants.ENTITY_INACTIVE);
            repo.save(temp);
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
