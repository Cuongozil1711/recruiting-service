package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.data.UserData;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListTemplateService {
    private final InterviewCheckListTemplateRepo repo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public InterviewCheckListTemplateService(InterviewCheckListTemplateRepo repo, InterviewCheckListTemplateItemRepo itemRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(InterviewCheckListTemplateDTO dto){

        if (dto.getPositionId() == null){
            throw new BusinessException("invalid-position");
        }
        if (dto.getOrgId() == null){
            throw new BusinessException("invalid-org");
        }
//        if (dto.getStartDate() == null){
//            throw new BusinessException("invalid-startDate");
//        }
//        if (dto.getEndDate() == null){
//            throw new BusinessException("invalid-endDate");
//        }
    }

    public void validItem(InterviewCheckListTemplateItemDTO dto){
//        if (dto.getCheckListId() == null){
//            throw new BusinessException("invalid-checkList");
//        }
//        if (dto.getTemplateId() == null){
//            throw new BusinessException("invalid-template");
//        }
        if (StringUtils.isEmpty(dto.getOptionType())){
            throw new BusinessException("invalid-optionType");
        }
//        if (dto.getMinRating() == null){
//            throw new BusinessException("invalid-minRating");
//        }
//        if (dto.getMaxRating() == null){
//            throw new BusinessException("invalid-maxRating");
//        }
//        if (StringUtils.isEmpty(dto.getOptionValues())){
//            throw new BusinessException("invalid-OptionValues");
//        }
    }
    public InterviewCheckListTemplateDTO create(long cid, String uid, InterviewCheckListTemplateDTO dto) {
        valid(dto);
        // create template
        InterviewCheckListTemplate template = InterviewCheckListTemplate.of(cid, uid, dto);
        template.setStartDate(new Date());
        template.setCompanyId(cid);
        template.setCreateBy(uid);
        template.setUpdateBy(uid);
        template.setStatus(Constants.ENTITY_ACTIVE);

        template = repo.save(template);
        //create template item

        for(InterviewCheckListTemplateItemDTO itemDTO : dto.getItems()){
            itemDTO.setTemplateId(template.getId());
            createItem(cid, uid, itemDTO);
        }

        return toDTOs(cid, uid, Collections.singletonList(template)).get(0);
    }

    private void createItem(long cid, String uid, InterviewCheckListTemplateItemDTO itemDTO) {
        validItem(itemDTO);
        // create template
        InterviewCheckListTemplateItem item = InterviewCheckListTemplateItem.of(cid, uid, itemDTO);
        item.setCompanyId(cid);
        item.setCreateBy(uid);
        item.setUpdateBy(uid);
        item.setStatus(Constants.ENTITY_ACTIVE);

        item = itemRepo.save(item);
    }

    public InterviewCheckListTemplateDTO update(long cid, String uid, Long id, InterviewCheckListTemplateDTO dto) {
        valid(dto);
        InterviewCheckListTemplate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(ProfileCheckListTemplate.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);

        for(InterviewCheckListTemplateItemDTO itemDTO : dto.getItems()){
            if(itemDTO.getId() == null || itemDTO.getId() == 0l){
                createItem(cid, uid, itemDTO);
            }else{
                if(CompareUtil.compare(dto.getStatus(), Constants.ENTITY_INACTIVE)){
                    itemDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                itemDTO.setTemplateId(dto.getId());
                updateItem(cid, uid, itemDTO.getId(), itemDTO);
            }

        }

        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    private void updateItem(long cid, String uid, Long id, InterviewCheckListTemplateItemDTO itemDTO) {
        validItem(itemDTO);
        if(itemDTO.getId() != 0l && itemDTO.getId() != null){
            InterviewCheckListTemplateItem curr = itemRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewCheckListTemplateItem.class, id));
            MapperUtils.copyWithoutAudit(itemDTO, curr);
            curr.setUpdateBy(uid);
            curr = itemRepo.save(curr);
        }else{
            createItem(cid, uid, itemDTO);
        }
    }

    public List<InterviewCheckListTemplateDTO> toDTOs(long cid, String uid, List<InterviewCheckListTemplate> objs) {
        List<InterviewCheckListTemplateDTO> dtos = new ArrayList<>();
        Set<Long> templateIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();

        objs.forEach(o -> {
            if(o.getPositionId() != null){
                categoryIds.add(o.getPositionId());
            }

            if(o.getOrgId() != null && o.getOrgId() != 0){
                orgIds.add(o.getOrgId());
            }
            if(!StringUtils.isEmpty(o.getCreateBy())){
                userIds.add(o.getCreateBy());
            }

            templateIds.add(o.getId());
        });


        List<InterviewCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdInAndStatus(cid, templateIds, Constants.ENTITY_ACTIVE);
        items.forEach(i -> {
            if(i.getCheckListId() != null){
                categoryIds.add(i.getCheckListId());
            }
//            if (i.getTemplateId() != null){
//                categoryIds.add(i.getTemplateId());
//            }
        });
        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);

        for(InterviewCheckListTemplate obj : objs){
            InterviewCheckListTemplateDTO o = toDTO(obj);

            if(o.getPositionId() != null){
                o.setPositionObj(mapCategory.get(o.getPositionId()));
            }
            if(o.getOrgId() != null){
                o.setOrg( mapOrg.get(o.getOrgId()));
            }
            if(!StringUtils.isEmpty(o.getCreateBy())){
                o.setCreateByObj((Map<String, Object>) mapperUser.get(o.getCreateBy()));
            }
            List<InterviewCheckListTemplateItemDTO> itemDTOs = new ArrayList<>();
            items.stream().filter(i -> CompareUtil.compare(i.getTemplateId(), obj.getId()))
                    .collect(Collectors.toList()).stream().forEach(i -> {
                        InterviewCheckListTemplateItemDTO itemDTO = MapperUtils.map(i, InterviewCheckListTemplateItemDTO.class);
                        if(itemDTO.getCheckListId() != null){
                            itemDTO.setCheckListObj(mapCategory.get(itemDTO.getCheckListId()));
                        }
//                        if (itemDTO.getTemplateId() != null){
//                            itemDTO.setTemplateObj(mapCategory.get(itemDTO.getTemplateId()));
//                        }
                        itemDTOs.add(itemDTO);
                    });

            o.setItems(itemDTOs);
            dtos.add(o);
        }
        return dtos;
    }
    public InterviewCheckListTemplateDTO toDTO (InterviewCheckListTemplate obj){
        return MapperUtils.map(obj, InterviewCheckListTemplateDTO.class);
    }

    public void delete(long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            InterviewCheckListTemplate temp = repo.findByCompanyIdAndId(cid, i).orElse(new InterviewCheckListTemplate());
            if (!temp.isNew()) {
                temp.setStatus(vn.ngs.nspace.recruiting.share.dto.utils.Constants.ENTITY_INACTIVE);
                temp.setUpdateBy(uid);
                temp.setModifiedDate(new Date());

                repo.save(temp);
            }
        });

    }
}
