package vn.ngs.nspace.recruiting.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplateItem;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;


import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProfileCheckListService {
    private final ProfileCheckListRepo repo;
    private final ProfileCheckListTemplateRepo repoTemplate;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final ProfileCheckListTemplateRepo templateRepo;

    public ProfileCheckListService(ProfileCheckListRepo repo, ProfileCheckListTemplateRepo templateRepo, ProfileCheckListTemplateRepo repoTemplate, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.templateRepo = templateRepo;
        this.repoTemplate = repoTemplate;
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

    public List<ProfileCheckListDTO> createByPositionTitleContract(Long cid, String uid
            , Long positionId, Long titleId, String contractType
            , Long employeeId, Date receiptDate, String description, Long senderId ){
        List<ProfileCheckListDTO> profiles = new ArrayList<>();
        List<ProfileCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, titleId, contractType);
        for (ProfileCheckListTemplate template: templates ) {
            ProfileCheckListDTO checkListDTO = new ProfileCheckListDTO();
            checkListDTO = MapperUtils.map(template, checkListDTO);
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

    public ProfileCheckListDTO toDTO(ProfileCheckList obj){
        return MapperUtils.map(obj, ProfileCheckListDTO.class);
    }
}
