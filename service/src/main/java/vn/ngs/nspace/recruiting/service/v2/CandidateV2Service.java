package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.handler.NoticeEvent;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteStorateService;
import vn.ngs.nspace.recruiting.share.dto.*;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.CandidateFilterRequest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CandidateV2Service {

    private final CandidateRepo candidateRepo;
    private final NoticeEvent noticeEvent;
    private final ExecuteConfigService configService;
    private final ExecuteStorateService storageService;
    private final OnboardOrderV2Service onboardOrderV2Service;
    private final JobApplicationRepo jobApplicationRepo;
    private final OnboardOrderRepo onboardOrderRepo;
    private final OnboardOrderCheckListRepo checkListRepo;

//    private final InterviewResultV2Service resultV2Service;


    public CandidateV2Service(CandidateRepo candidateRepo, NoticeEvent noticeEvent, ExecuteConfigService configService, ExecuteStorateService storageService, OnboardOrderV2Service onboardOrderV2Service, JobApplicationRepo jobApplicationRepo, OnboardOrderRepo onboardOrderRepo, OnboardOrderCheckListRepo checkListRepo) {
        this.candidateRepo = candidateRepo;
        this.noticeEvent = noticeEvent;
        this.configService = configService;
        this.storageService = storageService;
//        this.resultV2Service = resultV2Service;
        this.onboardOrderV2Service = onboardOrderV2Service;
        this.jobApplicationRepo = jobApplicationRepo;
        this.onboardOrderRepo = onboardOrderRepo;
        this.checkListRepo = checkListRepo;
    }

    /**
     * get list candidate with page and filter
     *
     * @param cid
     * @param request
     * @param pageable
     * @return
     * @throws Exception
     */

    public Page<CandidateDTO> getPage(String uid, Long cid, CandidateFilterRequest request, Pageable pageable) throws Exception {
        Date ageLess = DateUtil.addDate(new Date(), "year", -request.getAgeLess());
        Date applyDateFrom = request.getApplyDateFrom() != null ? request.getApplyDateFrom() : DateUtil.toDate("1000-01-01T00:00:00+0700", "yyyy-MM-dd'T'HH:mm:ssZ");
        Date applyDateTo = request.getApplyDateTo() != null ? request.getApplyDateTo() : DateUtil.toDate("5000-01-01T00:00:00+0700", "yyyy-MM-dd'T'HH:mm:ssZ");

        Page<Candidate> page = candidateRepo.getPage(cid, request.getSearch(), request.getStates(), request.getEducationLevel(), request.getLanguage(), applyDateFrom, applyDateTo, request.getGraduationFrom(), request.getIsBlacklist(), request.getGraduationTo(), request.getGender(), request.getApplyPosition(), request.getResource(), request.getExperience(), ageLess, pageable);
        List<CandidateDTO> candidateDTOS = toDTOs(cid, uid, page.getContent());

        return new PageImpl<CandidateDTO>(candidateDTOS, page.getPageable(), page.getTotalElements());
    }

    public Map<String, Object> getCount(Long cid) {
        Map<String, Object> countAll = candidateRepo.countAll(cid);

        return countAll;
    }

    /**
     * find by id and companyId
     *
     * @param cid
     * @param id
     * @return
     */

    public CandidateDTO getById(String uid, Long cid, Long id) {
        Candidate candidate = candidateRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));

        return toDTO(uid, cid, candidate);
    }

    /**
     * update object
     *
     * @param cid
     * @param uid
     * @param id
     * @param dto
     * @return
     * @throws Exception
     */

    public CandidateDTO update(Long cid, String uid, Long id, CandidateDTO dto) throws Exception {
        validate(dto);

        Candidate current = candidateRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));

        MapperUtils.copyWithoutAudit(dto, current);
        current.setUpdateBy(uid);

        candidateRepo.save(current);

        if (dto.getState().equalsIgnoreCase(Constants.HCM_RECRUITMENT.ONBOARDED.name())) {
            onboardOrderV2Service.creates(cid, uid, current.getId());

        }

        return toDTO(uid, cid, current);
    }

    /**
     * delete object
     *
     * @param cid Long
     * @param uid String
     * @param ids List<Long>
     */

    public void delete(Long cid, String uid, List<Long> ids) {
        List<Candidate> candidates = candidateRepo.findByArrayId(cid, ids);
        candidates.forEach(e -> {
            e.setStatus(Constants.ENTITY_INACTIVE);
            e.setUpdateBy(uid);
            e.setModifiedDate(new Date());

            candidateRepo.save(e);
        });
    }

    /**
     * validate object
     *
     * @param dto
     * @throws Exception
     */

    private void validate(CandidateDTO dto) throws Exception {
//        if (dto.getCode().isEmpty() || dto.getCode() == null) {
//            throw new Exception("");
//        }
    }

    public CandidateDTO create(Long cid, String uid, CandidateDTO dto) throws Exception {
        validate(dto);
        Candidate exists = candidateRepo.findByCompanyIdAndPhoneAndStatus(cid, dto.getPhone(), Constants.ENTITY_ACTIVE).orElse(new Candidate());
        if (!exists.isNew()) {
            throw new BusinessException("duplicate-data-with-this-phone" + ":" + dto.getPhone());
        }
        Candidate candidate = Candidate.of(cid, uid, dto);
        candidate.setStatus(Constants.ENTITY_ACTIVE);
        candidate.setCreateBy(uid);
        candidate.setUpdateBy(uid);
        candidate.setIsBlacklist(Constants.IS_NOT_BLACK_LIST);
        candidate.setCompanyId(cid);

        candidate = candidateRepo.save(candidate);

        return toDTO(uid, cid, candidate);
    }

    public List<CandidateDTO> create(Long cid, String uid, List<CandidateDTO> candidateDTOS) throws Exception {
        List<CandidateDTO> data = new ArrayList<>();
        for (CandidateDTO dto : candidateDTOS) {
            data.add(create(cid, uid, dto));
        }

        return data;
    }

    /**
     * convert object to dto
     *
     * @param candidate
     * @return
     */

    private CandidateDTO toDTO(String uid, Long cid, Candidate candidate) {
        CandidateDTO dto = MapperUtils.map(candidate, CandidateDTO.class);

        if (candidate.getInterviewResultId() != null) {
//            InterviewResultDTO interviewResultDTO = resultV2Service.getByInterviewResultId(cid, uid, candidate.getInterviewResultId());
//            dto.setInterviewResultDTO(interviewResultDTO);
        }

        dto.setApplyPositionIdObj(getCategory(cid, uid, candidate.getApplyPositionId()));
        return dto;
    }

    private Map<String, Object> getCategory(Long cid, String uid, Long applyPositionId) {
        Map<Long, Map<String, Object>> mapCategory = configService.getCategoryByIds(uid, cid, Set.of(applyPositionId));
        return mapCategory.get(applyPositionId);
    }

    /**
     * convert list object to list dto
     *
     * @param cid
     * @param uid
     * @param candidates
     * @return
     */

    public List<CandidateDTO> toDTOs(Long cid, String uid, List<Candidate> candidates) {
        List<CandidateDTO> candidateDTOS = new ArrayList<>();

        candidates.forEach(e -> {
                    candidateDTOS.add(toDTO(uid, cid, e));
                }
        );

        return candidateDTOS;
    }

    public JobApplicationOnboardDTO getJobApplicationOnboard(Long cid, String uid, Long candidateId) {
        JobApplication jobApplication = jobApplicationRepo.findByStatusCompanyIdCandidateId(candidateId, cid);
        if (jobApplication == null) return null;
        JobApplicationDTO jobApplicationDTO = toDTO(uid, cid, jobApplication);

        List<OnboardOrder> checkLists = onboardOrderRepo.getALlByJobApplication(cid, jobApplication.getId());

        List<OnboardOrderDTO> orderDTOS = new ArrayList<>();
        checkLists.forEach(
                e -> {
                    orderDTOS.add(toOnboardOrderDTO(cid,e));
                }
        );

        return new JobApplicationOnboardDTO(jobApplicationDTO, orderDTOS);
    }

    public OnboardOrderDTO toOnboardOrderDTO(Long cid, OnboardOrder onboardOrder) {
        OnboardOrderDTO onboardOrderDTO = MapperUtils.map(onboardOrder, OnboardOrderDTO.class);

        OnboardOrderCheckList checkList = checkListRepo.findByCompanyIdAndId(cid, onboardOrderDTO.getOnboardOrderId())
                .orElseThrow(()->new EntityNotFoundException(OnboardOrderCheckList.class,onboardOrderDTO.getOnboardOrderId()));

        OnboardOrderCheckListDTO checkListDTO = MapperUtils.map(checkList, OnboardOrderCheckListDTO.class);
        onboardOrderDTO.setOnboardOrderCheckListDTO(checkListDTO);

        return onboardOrderDTO;
    }

    public JobApplicationOnboardDTO updateJobApplicationOnboard(Long cid, String uid, JobApplicationOnboardDTO dto) {
        List<OnboardOrderDTO> onboardOrderDTOS = dto.getOnboardOrderDTOS();

        onboardOrderDTOS.forEach(
                e->{
                    OnboardOrder order = onboardOrderRepo.findByCompanyIdAndId(cid, e.getId())
                            .orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, e.getId()));

                    MapperUtils.copyWithoutAudit(e, order);
                    order.setUpdateBy(uid);

                    onboardOrderRepo.save(order);
                }
        );

        return getJobApplicationOnboard(cid, uid, dto.getJobApplicationDTO().getCandidateId());
    };

    /**
     *  convert JobApplication to JobApplicationDTO
     * @param uid
     * @param cid
     * @param jobApplication
     * @return
     */
    private JobApplicationDTO toDTO(String uid, Long cid, JobApplication jobApplication) {
        JobApplicationDTO jobApplicationDTO = MapperUtils.map(jobApplication, JobApplicationDTO.class);
        CandidateDTO candidateDTO = getById(uid, cid, jobApplicationDTO.getCandidateId());
        jobApplicationDTO.setCandidateObj(candidateDTO);
        return jobApplicationDTO;
    }

}
