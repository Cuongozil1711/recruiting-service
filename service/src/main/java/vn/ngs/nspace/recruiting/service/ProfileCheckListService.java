package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;


import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileCheckListService {
    private final ProfileCheckListRepo repo;
    private final ProfileCheckListTemplateRepo templateRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final ProfileCheckListTemplateItemRepo itemRepo;
    private final OnboardOrderRepo onboardOrderRepo;

    public ProfileCheckListService(ProfileCheckListRepo repo, ProfileCheckListTemplateRepo templateRepo, ProfileCheckListTemplateItemRepo itemRepo, OnboardOrderRepo onboardOrderRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.templateRepo = templateRepo;
        this.itemRepo = itemRepo;
        this.onboardOrderRepo = onboardOrderRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(ProfileCheckListDTO dto){
//        if (dto.getChecklistId() == null){
//            throw new BusinessException("invalid-checkList");
//        }
//        if (dto.getEmployeeId() == null){
//            throw new BusinessException("invalid-employee");
//        }
//        if (dto.getReceiptDate() == null){
//            throw new BusinessException("invalid-receiptDate");
//        }
//       if (dto.getSenderId() == null){
//            throw new BusinessException("invalid-sender");
//       }
//        if (dto.getDescription() == null){
//            throw new BusinessException("invalid-description");
//        }
    }

    public List<ProfileCheckListDTO> createByOnboardOrder(Long cid, String uid, Long id){

        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, id).orElseThrow(()-> new BusinessException("not found OnboardOder"));

        return createByPositionTitleContract(cid, uid, ja.getPositionId(), ja.getTitleId(), ja.getContractType(), id);
    }

    public List<ProfileCheckListDTO> createByPositionTitleContract(Long cid, String uid
            , Long positionId, Long titleId, String contractType, Long onboarOrderId){
        List<ProfileCheckListDTO> profiles = new ArrayList<>();
        List<ProfileCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, titleId, contractType);
        if(templates.size() == 0){
            throw new BusinessException("invalid-profile-template");
        }
        ProfileCheckListTemplate template = templates.get(0);
        List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());

            ProfileCheckListDTO checkListDTO = new ProfileCheckListDTO();


            profiles.add(create(cid, uid,onboarOrderId, checkListDTO));

        return profiles;
    }

    public ProfileCheckListDTO create(Long cid, String uid, Long onboarOrderId, ProfileCheckListDTO request) throws BusinessException{
        valid(request);
        ProfileCheckList exists = repo.findByCompanyIdAndOnboardOrderIdAndStatus(cid, onboarOrderId, Constants.ENTITY_ACTIVE).orElse(new ProfileCheckList());
        if(!exists.isNew()){
            return toDTOs(cid, uid, Collections.singletonList(exists)).get(0);
        }
        ProfileCheckList obj = ProfileCheckList.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCompanyId(cid);
        obj.setUpdateBy(uid);
        obj.setCreateBy(uid);
        obj.setOnboardOrderId(onboarOrderId);
        repo.save(obj);
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    public List<ProfileCheckListDTO> handOverProfile (Long cid, String uid, Long onboarOrderId, List<ProfileCheckListDTO> listDTOS) {
        List<ProfileCheckList> lstProfile = new ArrayList<>();
        for (ProfileCheckListDTO dto: listDTOS) {
            if(dto.getId() != null){
                ProfileCheckList curr = repo.findByCompanyIdAndId(cid, dto.getId()).orElse(new ProfileCheckList());
                MapperUtils.copyWithoutAudit(dto, curr);
                curr.setOnboardOrderId(onboarOrderId);
                curr.setReceiptDate(dto.getReceiptDate());
                curr.setSenderId(dto.getSenderId());
                curr.setCompanyId(cid);
                curr.setUpdateBy(uid);
                curr.setStatus(dto.getStatus() == null ? Constants.ENTITY_ACTIVE : dto.getStatus());

                repo.save(curr);
                lstProfile.add(curr);
            }
            else {
                ProfileCheckList pf = ProfileCheckList.of(cid, uid, dto);
                pf.setStatus(Constants.ENTITY_ACTIVE);
                pf.setCreateBy(uid);
                pf.setCompanyId(cid);
                pf.setOnboardOrderId(onboarOrderId);
                repo.save(pf);
                lstProfile.add(pf);
            }

        }
        return toDTOs(cid, uid, lstProfile);
    }

    public List<ProfileCheckListDTO> toDTOs(Long cid, String uid, List<ProfileCheckList> objs){
        List<ProfileCheckListDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();

        ProfileCheckList pr = objs.get(0);
        ProfileCheckListTemplate template = new ProfileCheckListTemplate();
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, pr.getOnboardOrderId()).orElseThrow(()-> new BusinessException("not found OnboardOder"));
        List<ProfileCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, ja.getPositionId(), ja.getTitleId(), ja.getContractType());
        if(templates.size() != 0){
            template = templates.get(0);
        }

        objs.forEach(o -> {
            if(o.getChecklistId() != null){
                categoryIds.add(o.getChecklistId());
            }
            if(o.getEmployeeId() != null){
                employeeIds.add(o.getEmployeeId());
            }
            if(o.getSenderId() != null){
                employeeIds.add(o.getSenderId());
            }

            dtos.add(toDTO(o));
        });

        List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateIdAndStatus(cid, template.getId(), Constants.ENTITY_ACTIVE);
        Map<Long, List<ProfileCheckListTemplateItem>> mapItems = items.stream().collect(Collectors.groupingBy(ProfileCheckListTemplateItem::getTemplateId));

        List<EmployeeDTO> employeeDTOS = _hcmService.getEmployees(uid, cid, employeeIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(ProfileCheckListDTO dto: dtos){
            if(dto.getChecklistId() != null){
                dto.setCheckListObj(mapCategory.get(dto.getChecklistId()));
            }
            if(dto.getEmployeeId() != null){
                dto.setEmployeeObj(employeeDTOS.stream().filter(e -> {
                    return CompareUtil.compare(e.getId(), dto.getEmployeeId());
                }).findAny().orElse(null) );
            }
            if(dto.getSenderId() != null){
                dto.setSenderObj(employeeDTOS.stream().filter(e -> {
                    return CompareUtil.compare(e.getId(), dto.getSenderId());
                }).findAny().orElse(null) );
            }

            if (mapItems.get(template.getId()) != null){
                List<ProfileCheckListTemplateItemDTO> lstDTO = new ArrayList<>();
                for (ProfileCheckListTemplateItem lst: mapItems.get(template.getId())) {
                    ProfileCheckListTemplateItemDTO item = new ProfileCheckListTemplateItemDTO();
                    MapperUtils.copy(lst, item);
                    if(item != null){
                        lstDTO.add(item);
                    }
                }
                dto.setItems(lstDTO);
            }
        }

        return dtos;
    }

    public ProfileCheckListDTO toDTO(ProfileCheckList obj){
        return MapperUtils.map(obj, ProfileCheckListDTO.class);
    }
}
