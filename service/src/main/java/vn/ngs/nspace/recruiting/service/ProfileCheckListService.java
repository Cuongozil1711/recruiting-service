package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;


import javax.transaction.Transactional;
import java.util.*;

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
        if (dto.getChecklistId() == null){
            throw new BusinessException("invalid-checkList");
        }
        if (dto.getEmployeeId() == null){
            throw new BusinessException("invalid-employee");
        }
        if (dto.getReceiptDate() == null){
            throw new BusinessException("invalid-receiptDate");
        }
       if (dto.getSenderId() == null){
            throw new BusinessException("invalid-sender");
       }
        if (dto.getDescription() == null){
            throw new BusinessException("invalid-description");
        }
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

    public ProfileCheckListDTO handOverProfile (Long cid, String uid, Long checklistId, Long employeeId, Date reciptDate) {
        if(checklistId == null){
            throw new BusinessException("invalid-checklist");
        }
        if(employeeId == null){
            throw new BusinessException("invalid-employeeId");
        }
        ProfileCheckList curr = repo.getProfile(cid, checklistId, employeeId);
        curr.setReceiptDate(reciptDate);
        curr = repo.save(curr);
        return  toDTO(curr);
    }

    public ProfileCheckListDTO toDTO(ProfileCheckList obj){
        return MapperUtils.map(obj, ProfileCheckListDTO.class);
    }
}
