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

    public List<ProfileCheckListDTO> createByOnboardOrder(Long cid, String uid, OnboardOrder onboard){
        Date receiptDate = new Date();
        Long id = onboard.getId();
        JobApplication ja = onboardOrderRepo.getInfoOnboard(cid, id).orElseThrow(()-> new BusinessException("not found OnboardOder"));

        return createByPositionTitleContract(cid, uid, ja.getPositionId(), ja.getTitleId(), ja.getContractType(), ja.getEmployeeId(), receiptDate, "", 0l);
    }

    public List<ProfileCheckListDTO> createByPositionTitleContract(Long cid, String uid
            , Long positionId, Long titleId, String contractType
            , Long employeeId, Date receiptDate, String description, Long senderId ){
        List<ProfileCheckListDTO> profiles = new ArrayList<>();
        List<ProfileCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, titleId, contractType);

        ProfileCheckListTemplate template = templates.get(0);
        List<ProfileCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());
        for (ProfileCheckListTemplateItem item: items ) {

            ProfileCheckListDTO checkListDTO = new ProfileCheckListDTO();
            checkListDTO = MapperUtils.map(item, checkListDTO);
            checkListDTO.setEmployeeId(employeeId);
            checkListDTO.setReceiptDate(receiptDate);
            checkListDTO.setDescription(description);
            checkListDTO.setSenderId(senderId);

            profiles.add(create(cid, uid, checkListDTO));
        }
        return profiles;
    }

    public ProfileCheckListDTO create(Long cid, String uid, ProfileCheckListDTO request) throws BusinessException{
        valid(request);
        ProfileCheckList exists = repo.findByCompanyIdAndChecklistIdAndEmployeeIdAndStatus(cid, request.getChecklistId(), request.getEmployeeId(), Constants.ENTITY_ACTIVE).orElse(new ProfileCheckList());
        if(!exists.isNew()){
            return toDTO(exists);
        }
        ProfileCheckList obj = ProfileCheckList.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCompanyId(cid);
        obj.setUpdateBy(uid);
        obj.setCreateBy(uid);

        return toDTO(repo.save(obj));
    }

    public List<ProfileCheckListDTO> handOverProfile (Long cid, String uid, Long onboarOrderId, List<ProfileCheckListDTO> listDTOS) {
        List<ProfileCheckList> profileCheckLists = repo.findByCompanyIdAndOnboardOrderId(cid, onboarOrderId);
        List<Long> checkListIdOfDto = listDTOS.stream().map(dto -> dto.getChecklistId()).collect(Collectors.toList());
        List<Long> checkListIdExists = profileCheckLists.stream().map(dto -> dto.getChecklistId()).collect(Collectors.toList());

        List<Long> listCheckListIdForCreate = new ArrayList<>(checkListIdOfDto);

        listCheckListIdForCreate.removeAll(checkListIdExists); // loai bo danh sach profile da ton tai

        List<ProfileCheckList> listOfProfileCheckList = new ArrayList<>(); // Tao 1 array de luu tru ban ghi can luu vao Database

        // Tao moi danh sach ProfileCheckList
        for (Long checkListId : listCheckListIdForCreate) {
            ProfileCheckList profileCheckList = new ProfileCheckList();

            ProfileCheckListDTO dto = listDTOS.stream().filter(el -> el.getChecklistId() == checkListId).collect(Collectors.toList()).get(0);

            if (dto != null) {
                profileCheckList.setCompanyId(cid);
                profileCheckList.setChecklistId(checkListId);
                profileCheckList.setEmployeeId(dto.getEmployeeId());
                profileCheckList.setReceiptDate(dto.getReceiptDate());

                listOfProfileCheckList.add(profileCheckList);
            }
        }
        // Ket thuc tao moi

        // Update danh sach ProfileCheckList
        List<Long> listCheckListForUpdate = new ArrayList<>(checkListIdOfDto);

        listCheckListForUpdate.retainAll(checkListIdExists); // lay danh sach checkListId da ton tai

        for (Long checkListId : listCheckListForUpdate) {
            ProfileCheckList profileCheckList = profileCheckLists.stream().filter(el -> el.getChecklistId() == checkListId).collect(Collectors.toList()).get(0);

            ProfileCheckListDTO dto = listDTOS.stream().filter(el -> el.getChecklistId() == checkListId).collect(Collectors.toList()).get(0);
            if (profileCheckList != null) {
                profileCheckList.setReceiptDate(dto.getReceiptDate());

                listOfProfileCheckList.add(profileCheckList);
            }
        }
        // Ket thuc update

        // Luu vao Database
        if (listOfProfileCheckList != null && !listOfProfileCheckList.isEmpty()) {
            listOfProfileCheckList = repo.saveAll(listOfProfileCheckList);
        }

        return toDTOs(cid, uid, listOfProfileCheckList);

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
                for (ProfileCheckListTemplateItem lst: mapItems.get(template.getId())) {
                    ProfileCheckListTemplateItemDTO item = new ProfileCheckListTemplateItemDTO();
                    MapperUtils.copy(lst, item);
                    if(item != null){
                        dto.setItem(item);
                    }
                }
            }
        }

        return dtos;
    }

    public ProfileCheckListDTO toDTO(ProfileCheckList obj){
        return MapperUtils.map(obj, ProfileCheckListDTO.class);
    }
}
