package vn.ngs.nspace.recruiting.service;

import io.netty.util.Constant;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
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
            String positionToConvert = String.valueOf(data.get("positionId"));
            String titileToConvert = String.valueOf(data.get("titileId"));
            Long positionId = Long.parseLong(positionToConvert);
            Long titileId = Long.parseLong(titileToConvert);
            ProfileCheckListTemplate obj = repo.findByCompanyIdAndPositionIdAndTitleIdAndStatus(cid, positionId, titileId, Constants.ENTITY_ACTIVE).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, templateId));
            if(obj != null){
                obj.setStatus(Constants.ENTITY_INACTIVE);
                obj = repo.save(obj);
                ProfileCheckListTemplateDTO dto = new ProfileCheckListTemplateDTO();
                MapperUtils.copyWithoutAudit(obj, dto);
                create(cid, uid, dto);
            }
        }
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
