package vn.ngs.nspace.recruiting.service;

import org.camunda.bpm.engine.BadUserRequestException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplate;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplateItem;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplateItem;
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
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public OnboardTrainingTemplateService(OnboardTrainingTemplateRepo repo, OnboardTrainingTemplateItemRepo itemRepo, ExecuteHcmService _hcmService, ExecuteConfigService _configService){
        this.repo = repo;
        this.itemRepo = itemRepo;
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
        OnboardTrainingTemplate template = new OnboardTrainingTemplate();
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
    }

    public List<OnboardTrainingTemplateDTO> toDTOs(Long cid, String uid, List<OnboardTrainingTemplate> objs){
        List<OnboardTrainingTemplateDTO> dtos = new ArrayList<>();
        Set<Long> templateIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        objs.forEach(o -> {
            if(o.getPositionId() != null){
                categoryIds.add(o.getPositionId());
            }
            if(o.getTitleId() != null){
                categoryIds.add(o.getTitleId());
            }

            templateIds.add(o.getId());
        });

        List<OnboardTrainingTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdInAndStatus(cid, templateIds, Constants.ENTITY_ACTIVE);
        items.forEach(i -> {
            if(i.getEemployeeId() != null){
                employeeIds.add(i.getEemployeeId());
            }
        });

        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        List<EmployeeDTO> employeeDTOS = _hcmService.getEmployees(uid, cid, employeeIds);

        for (OnboardTrainingTemplate obj: objs){
            OnboardTrainingTemplateDTO o = toDTO(obj);
            if(o.getPositionId() != null){
                o.setPositionObj(mapCategory.get(o.getPositionId()));
            }
            if(o.getTitleId() != null){
                o.setTitleObj(mapCategory.get(o.getTitleId()));
            }

            List<OnboardTrainingTemplateItemDTO> itemDTOs = new ArrayList<>();
            items.stream().filter(i -> CompareUtil.compare(i.getTemplateId(), obj.getId()))
                    .collect(Collectors.toList()).stream().forEach(i -> {
                        OnboardTrainingTemplateItemDTO itemDTO = MapperUtils.map(i, OnboardTrainingTemplateItemDTO.class);
                        if(itemDTO.getEmployeeId() != null){
                            itemDTO.setEmployeeObj(employeeDTOS.stream().filter(element -> element.getId() == itemDTO.getEmployeeId()).collect(Collectors.toList()).get(0));
                        }
                        itemDTOs.add(itemDTO);
                    });

            o.setItems(itemDTOs);
            dtos.add(o);
        }
        return dtos;
    }

    public OnboardTrainingTemplateDTO toDTO(OnboardTrainingTemplate obj){
        return MapperUtils.map(obj, OnboardTrainingTemplateDTO.class);
    }
}
