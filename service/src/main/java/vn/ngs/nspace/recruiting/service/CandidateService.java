package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.handler.NoticeEvent;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.repo.CandidateFilterRepo;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewInvolveRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CandidateService {
    private final CandidateRepo repo;
    private final CandidateFilterRepo filterRepo;
    private final ExecuteHcmService _hcmService;
    private final NoticeEvent _noticeEvent;
    private final InterviewInvolveRepo _interviewInvolve;
    private final ExecuteConfigService _configService;
    private final ExecuteStorateService _storageService;

    public CandidateService(CandidateRepo repo, CandidateFilterRepo filterRepo, ExecuteHcmService hcmService, NoticeEvent noticeEvent, InterviewInvolveRepo interviewInvolve, ExecuteConfigService configService, ExecuteStorateService sorateService) {
        this.repo = repo;
        this.filterRepo = filterRepo;
        _hcmService = hcmService;
        _noticeEvent = noticeEvent;
        _interviewInvolve = interviewInvolve;
        _configService = configService;
        _storageService = sorateService;
    }

    /* logic validate data before insert model */
    public void valid(CandidateDTO dto) throws BusinessException {
//        if(StringUtils.isEmpty(dto.getFullName())){
//            throw new BusinessException("Name-not-valid");
//        }
//        if (dto.getBirthDate() == null) {
//            throw new BusinessException("invalid-birthDate");
//        }
//        if (dto.getGender() == null) {
//            throw new BusinessException("invalid-gender");
//        }
//        if (StringUtils.isEmpty(dto.getPhone())) {
//            throw new BusinessException("invalid-phone");
//        }
//        if (StringUtils.isEmpty(dto.getEmail())) {
//            throw new BusinessException("invalid-email");
//        }
//        if (dto.getApplyPositionId() == null) {
//            throw new BusinessException("invalid-position");
//        }
//        if(dto.getCvSourceId() == null){
//            throw new BusinessException("Cv-cannot-be-empty");
//        }
    }

    /* create list object */
    public List<CandidateDTO> create(Long cid, String uid, List<CandidateDTO> dtos) throws BusinessException {
        List<CandidateDTO> data = new ArrayList<>();
        for(CandidateDTO dto : dtos){
            data.add(create(cid, uid, dto));
        }
        return data;
    }

    // count all states and status = 1
    public CandidateDTO countAllStatesAndStatus(Long cid ) {
        Map<String, Object> countAllStatesAndStatus = repo.countAllStates(cid);

        Candidate countAll = new Candidate();
        countAll.setCountInit(countAllStatesAndStatus.get("init") != null ? Long.parseLong(countAllStatesAndStatus.get("init").toString()) : 0L);
        countAll.setCountRecruited(countAllStatesAndStatus.get("recruited") != null ? Long.parseLong(countAllStatesAndStatus.get("recruited").toString()) : 0L);
        countAll.setCountArchive(countAllStatesAndStatus.get("archive") != null ? Long.parseLong(countAllStatesAndStatus.get("archive").toString()) : 0L);
        countAll.setCountInterviewed(countAllStatesAndStatus.get("interviewed") != null ? Long.parseLong(countAllStatesAndStatus.get("interviewed").toString()) : 0L);
        countAll.setCountApproved(countAllStatesAndStatus.get("approved") != null ? Long.parseLong(countAllStatesAndStatus.get("approved").toString()) : 0L);
        countAll.setCountOnboard(countAllStatesAndStatus.get("appointment") != null ? Long.parseLong(countAllStatesAndStatus.get("appointment").toString()) : 0L);
        countAll.setCountAppointment(countAllStatesAndStatus.get("onboard") != null ? Long.parseLong(countAllStatesAndStatus.get("onboard").toString()) : 0L);
        countAll.setCountStaff(countAllStatesAndStatus.get("staff") != null ? Long.parseLong(countAllStatesAndStatus.get("staff").toString()) : 0L);
        countAll.setCountDenied(countAllStatesAndStatus.get("denied") != null ? Long.parseLong(countAllStatesAndStatus.get("denied").toString()) : 0L);

        return Candidate.countAllStates(countAll);
    }
    public void uploadFile(String requestUserId, long companyId, MultipartFile file) throws IllegalStateException, IOException{
        _storageService.uploadFile(requestUserId,companyId,file);
    }

    /* create object */
    public CandidateDTO create(Long cid, String uid, CandidateDTO dto) throws BusinessException {
        valid(dto);
        Candidate exists = repo.findByCompanyIdAndPhoneAndStatus(cid, dto.getPhone(), Constants.ENTITY_ACTIVE).orElse(new Candidate());
        if(!exists.isNew()){
            throw new BusinessException("duplicate-data-with-this-phone"+":"+dto.getPhone());
        }
        Candidate candidate = Candidate.of(cid, uid, dto);
        candidate.setStatus(Constants.ENTITY_ACTIVE);
        candidate.setCreateBy(uid);
        candidate.setUpdateBy(uid);
        candidate.setCompanyId(cid);
        candidate = repo.save(candidate);

        return toDTO(candidate);
    }

    /* update by id object */
    public CandidateDTO update(Long cid, String uid, Long id, CandidateDTO dto) throws Exception {
        valid(dto);
        Candidate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);
    // sent noity and email
        if (curr.getInvolveId() != null){
            Optional<InterviewInvolve> getInterviewId = _interviewInvolve.findByCompanyIdAndId(cid,curr.getInvolveId());
            List<String> interviewId = new ArrayList<>();
            if (getInterviewId.get().getInterviewerId() !=null){
                interviewId.addAll(getInterviewId.get().getInterviewerId());
            }
            String code = "HCM-Recruting";
            String action = "NTD";
            String application = "recruiting-service";
//            _noticeEvent.send(cid,uid,code,action,Constants.NOITY_TYPE_INVITED_INTERVIEW, Collections.singleton(application));
        }


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
        Set<String> createBy = new HashSet<>();

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
            if (obj.getCvSourceId() != null){
                categoryIds.add(obj.getCvSourceId());
            }
            if(obj.getTitleId() !=null){
                categoryIds.add(obj.getTitleId());
            }
            createBy.add(obj.getCreateBy());

            dtos.add(toDTO(obj));
        });

        Map<String, Map<String, Object>> mapTerritory = _configService.getTerritories(uid, cid, territoryCodes);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(CandidateDTO dto : dtos){
            List<Map<String, Object>> _a = repo.countPositionApply();

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
            if (dto.getCvSourceId() != null){
                dto.setCvSourceObj(mapCategory.get(dto.getCvSourceId()));
            }
            if (dto.getTitleId() != null){
                dto.setTitleObj(mapCategory.get(dto.getTitleId()));
            }
            if (_a != null) {
                dto.setCountPositionApply(_a);
            }
        }

        return dtos;
    }
    //gui phe duyet hoi dong
    public List<String> sendApproval (Long cid,String uid , Map<String,Object>payload){
        Long involveId = MapUtils.getLong(payload,"involveId", Long.valueOf(-1));
        Optional<InterviewInvolve> getInterviewId = _interviewInvolve.findByCompanyIdAndId(cid,involveId);
        List<String> interviewId = new ArrayList<>();
        if (getInterviewId.get().getInterviewerId() !=null){
            interviewId.addAll(getInterviewId.get().getInterviewerId());
        }

        return interviewId;
    }


    //filter
    public Page<Candidate> filterByStates(Long cid,String uid, Map<String, Object> payload, Pageable pageable) throws Exception {
        List<String> states = Arrays.asList("#");
        if (payload.get("states") != null && !((List<String>) payload.get("states")).isEmpty()){
            states = (List<String>) payload.get("states");
        }
        List<Long> educationLevel =  Arrays.asList(-1L);
        if (payload.get("educationLevel") != null && !((List<String>) payload.get("educationLevel")).isEmpty()){
            educationLevel = (List<Long>) payload.get("educationLevel");
        }
        else {
            educationLevel =  Arrays.asList(-1L);
        }
        List<Long> language =  Arrays.asList(-1L);
        if (payload.get("language") != null && !((List<String>) payload.get("language")).isEmpty()){
            language = (List<Long>) payload.get("language");
        }
        else {
            language =  Arrays.asList(-1L);
        }
        String dmin= "1000-01-01T00:00:00+0700";
        String dmax="5000-01-01T00:00:00+0700";
        Date defautValueDateStart = DateUtil.toDate(dmin,"yyyy-MM-dd'T'HH:mm:ssZ");
        Date defautValueDateEnd = DateUtil.toDate(dmax,"yyyy-MM-dd'T'HH:mm:ssZ");

        Date applyDateFrom = MapUtils.getDate(payload,"applyDateFrom",defautValueDateStart);
        Date applyDateTo = MapUtils.getDate(payload,"applyDateTo",defautValueDateEnd);
        Integer graduationFrom = MapUtils.getInteger(payload,"graduationFrom",0);
        Integer graduationTo = MapUtils.getIntValue(payload,"graduationTo",9999);
        String experience = MapUtils.getString(payload, "experience","#");
        Long gender = MapUtils.getLong(payload,"gender", -1L);
        Long applyPosition = MapUtils.getLong(payload,"applyPosition", -1L);
        Long resource = MapUtils.getLong(payload,"resource", -1L);
        String search = MapUtils.getString(payload, "search","#").toLowerCase();

        Integer ageLess = MapUtils.getInteger(payload,"ageLess", 1000);
        Date yearLess = null;
            yearLess = DateUtil.addDate(new Date(), "year",-ageLess);
        Page<Candidate> CandidateStates = repo.fillterStates(cid,search,states,educationLevel,language,applyDateFrom,applyDateTo,graduationFrom,graduationTo,gender,applyPosition,resource,experience,yearLess,pageable);
        List<CandidateDTO> data = toDTOs(cid, uid, CandidateStates.getContent());
        return new PageImpl(data, CandidateStates.getPageable(), CandidateStates.getTotalElements());
    }
    //candidate
    public Page<Candidate> filterCandidate(Long cid,String uid, Map<String, Object> payload, Pageable pageable) throws Exception {
        List<String> states = Arrays.asList("#");
        if (payload.get("states") != null && !((List<String>) payload.get("states")).isEmpty()){
            states = (List<String>) payload.get("states");
        }
        Page<Candidate> filterCandidateStates = repo.filterCandidate(cid,states,pageable);
        List<CandidateDTO> data = toDTOs(cid, uid, filterCandidateStates.getContent());
        return new PageImpl(data, filterCandidateStates.getPageable(), filterCandidateStates.getTotalElements());
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
