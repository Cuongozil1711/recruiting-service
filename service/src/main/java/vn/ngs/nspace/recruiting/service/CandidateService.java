package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.repo.CandidateFilterRepo;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CandidateService {
    private final CandidateRepo repo;
    private final CandidateFilterRepo filterRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public CandidateService(CandidateRepo repo, CandidateFilterRepo filterRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.filterRepo = filterRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    /* logic validate data before insert model */
    public void valid(CandidateDTO dto) throws BusinessException {
        if(StringUtils.isEmpty(dto.getFullName())){
            throw new BusinessException("Name-not-valid");
        }
        if (dto.getBirthDate() == null) {
            throw new BusinessException("invalid-birthDate");
        }
        if (dto.getGender() == null) {
            throw new BusinessException("invalid-gender");
        }
        if (StringUtils.isEmpty(dto.getPhone())) {
            throw new BusinessException("invalid-phone");
        }
        if (StringUtils.isEmpty(dto.getEmail())) {
            throw new BusinessException("invalid-email");
        }
        if (dto.getApplyPositionId() == null) {
            throw new BusinessException("invalid-position");
        }
        if(dto.getCvSourceId() == null){
            throw new BusinessException("Cv-cannot-be-empty");
        }


    }

    /* create list object */
    public List<CandidateDTO> create(Long cid, String uid, List<CandidateDTO> dtos) throws BusinessException {
        List<CandidateDTO> data = new ArrayList<>();
        for(CandidateDTO dto : dtos){
            data.add(create(cid, uid, dto));
        }
        return data;
    }

    /* create object */
    public CandidateDTO create(Long cid, String uid, CandidateDTO dto) throws BusinessException {
        valid(dto);
        Candidate candidate = Candidate.of(cid, uid, dto);
        candidate.setStatus(Constants.ENTITY_ACTIVE);
        candidate.setCreateBy(uid);
        candidate.setUpdateBy(uid);
        candidate.setCompanyId(cid);
        candidate = repo.save(candidate);

        return toDTO(candidate);
    }

    /* update by id object */
    public CandidateDTO update(Long cid, String uid, Long id, CandidateDTO dto) throws BusinessException {
        valid(dto);
        Candidate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        return toDTOWithObj(cid, uid, curr);
    }

    /* update by id object */
    public CandidateFilter updateFilter(Long cid, String uid, CandidateFilter request) throws BusinessException {
        if(request.getId() != null && request.getId() != 0l){
            request = filterRepo.findByCompanyIdAndId(cid, request.getId()).orElse(new CandidateFilter());
        }
        CandidateFilter obj = CandidateFilter.of(cid, uid, request);
        if(obj.isNew()){
            obj.setCreateBy(uid);
        }
        obj.setUpdateBy(uid);
        return filterRepo.save(obj);
    }

    /* convert list model object to DTO before response */
    public List<CandidateDTO> toDTOs(Long cid, String uid, List<Candidate> objs){
        List<CandidateDTO> dtos = new ArrayList<>();
        Set<String> territoryCodes = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();

        objs.forEach(obj -> {
            if(!StringUtils.isEmpty(obj.getWardCode())){
                territoryCodes.add(obj.getWardCode());
            }
            if(!StringUtils.isEmpty(obj.getDistrictCode())){
                territoryCodes.add(obj.getDistrictCode());
            }
            if(!StringUtils.isEmpty(obj.getProvinceCode())){
                territoryCodes.add(obj.getProvinceCode());
            }
            if(!StringUtils.isEmpty(obj.getCountryCode())){
                territoryCodes.add(obj.getCountryCode());
            }
            if(obj.getEducationLevel() != null){
                categoryIds.add(obj.getEducationLevel());
            }
            if(obj.getGender() != null){
                categoryIds.add(obj.getGender());
            }
            if(obj.getApplyPositionId() != null){
                categoryIds.add(obj.getApplyPositionId());
            }

            dtos.add(toDTO(obj));
        });

        Map<String, Map<String, Object>> mapTerritory = _configService.getTerritories(uid, cid, territoryCodes);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(CandidateDTO dto : dtos){
            if(!StringUtils.isEmpty(dto.getWardCode())){
                dto.setWardCodeObj(mapTerritory.get(dto.getWardCode()));
            }
            if(!StringUtils.isEmpty(dto.getDistrictCode())){
                dto.setDistrictCodeObj(mapTerritory.get(dto.getDistrictCode()));
            }
            if(!StringUtils.isEmpty(dto.getProvinceCode())){
                dto.setProvinceCodeObj(mapTerritory.get(dto.getProvinceCode()));
            }
            if(!StringUtils.isEmpty(dto.getCountryCode())){
                dto.setCountryCodeObj(mapTerritory.get(dto.getCountryCode()));
            }
            if(dto.getEducationLevel() != null){
                dto.setEducateLevelObj(mapCategory.get(dto.getEducationLevel()));
            }
            if(dto.getGender() != null){
                dto.setGenderObj(mapCategory.get(dto.getGender()));
            }
            if (dto.getApplyPositionId() != null){
                dto.setApplyPositionIdObj(mapCategory.get(dto.getApplyPositionId()));
            }
        }

        return dtos;
    }

    /* convert model object to DTO with data before response */
    public CandidateDTO toDTOWithObj(Long cid, String uid, Candidate candidate){
        return toDTOs(cid, uid, Collections.singletonList(candidate)).get(0);
    }

    /* convert model object to DTO before response */
    public CandidateDTO toDTO(Candidate candidate){
        CandidateDTO dto = MapperUtils.map(candidate, CandidateDTO.class);
        return dto;
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            Candidate candidate = repo.findByCompanyIdAndId(cid, i).orElse(new Candidate());
            if(!candidate.isNew()){
                candidate.setStatus(Constants.ENTITY_INACTIVE);
                candidate.setUpdateBy(uid);
                candidate.setModifiedDate(new Date());

                repo.save(candidate);
            }
        });
    }

}
