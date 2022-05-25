package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.request.JobApplicationFilterRequest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

;

@Service
@Transactional
public class JobApplicationV2Service {

    private final JobApplicationRepo jobApplicationRepo;
    private final CandidateV2Service candidateService;
    private final InterviewResultV2Service resultV2Service;
    private final OnboardOrderRepo onboardOrderRepo;

    public JobApplicationV2Service(JobApplicationRepo jobApplicationRepo, CandidateV2Service candidateService, InterviewResultV2Service resultV2Service, OnboardOrderRepo onboardOrderRepo) {
        this.jobApplicationRepo = jobApplicationRepo;
        this.candidateService = candidateService;
        this.resultV2Service = resultV2Service;
        this.onboardOrderRepo = onboardOrderRepo;
    }

    public Page<JobApplicationDTO> getPage(String uid, Long cid, JobApplicationFilterRequest request, Pageable pageable) throws Exception {
        Date ageLess = DateUtil.addDate(new Date(), "year", -request.getAgeLess());
        Date applyDateFrom = request.getApplyDateFrom() != null ? request.getApplyDateFrom() : DateUtil.toDate("1000-01-01T00:00:00+0700", "yyyy-MM-dd'T'HH:mm:ssZ");
        Date applyDateTo = request.getApplyDateTo() != null ? request.getApplyDateTo() : DateUtil.toDate("5000-01-01T00:00:00+0700", "yyyy-MM-dd'T'HH:mm:ssZ");

        Page<JobApplication> page = jobApplicationRepo.getPage(cid, request.getSearch(), request.getStates(), request.getEducationLevel(), request.getLanguage(), applyDateFrom, applyDateTo, request.getGraduationFrom(), request.getGraduationTo(), request.getGender(), request.getApplyPosition(), request.getResource(), request.getExperience(), ageLess, pageable);

        List<JobApplicationDTO> jobApplicationDTOS = toDTOs(uid, cid, page.getContent());
        return new PageImpl<>(jobApplicationDTOS, page.getPageable(), page.getTotalElements());
    }

    private JobApplicationDTO create(String uid, Long cid, JobApplicationDTO jobApplicationDTO) {
        JobApplication exists = jobApplicationRepo.findByCandidateIdAndPlanningIdAndCompanyIdAndStatus(
                        jobApplicationDTO.getCandidateId(), jobApplicationDTO.getPlanningId(), cid, jobApplicationDTO.getStatus())
                .orElseThrow(() -> new EntityNotFoundException(JobApplication.class, jobApplicationDTO.getId()));

        if (!exists.isNew()) {
            throw new BusinessException("duplicate-data-with-this-id" + ":" + exists.getCandidateId());
        }

        JobApplication jobApplication = JobApplication.of(cid, uid, jobApplicationDTO);
        jobApplication.setCreateBy(uid);
        jobApplication.setUpdateBy(uid);
        jobApplication.setStatus(Constants.ENTITY_ACTIVE);
        jobApplication.setCompanyId(cid);

        jobApplicationRepo.save(jobApplication);

        return toDTO(uid, cid, jobApplication);
    }

    public JobApplicationDTO getById(String uid, Long cid, Long id) {
        JobApplication jobApplication = jobApplicationRepo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, id));

        return toDTO(uid, cid, jobApplication);
    }

    private JobApplicationDTO update(String uid, Long cid, JobApplicationDTO jobApplicationDTO) {
        return null;
    }

    public void remove(Long cid, String uid, List<Long> jobApplicationIds) {
       jobApplicationRepo.removeAllBy(cid, uid,jobApplicationIds);
       jobApplicationIds.forEach(
               e->{
                    JobApplication jobApplication = jobApplicationRepo.getOne(e);
                    CandidateDTO candidateDTO = candidateService.getById(uid,cid,jobApplication.getCandidateId());
                    candidateDTO.setState(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT.DENIED.name());
                   try {
                       candidateService.update(cid, uid, candidateDTO.getId(),candidateDTO);
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }
               }
       );
    }

    private JobApplicationDTO toDTO(String uid, Long cid, JobApplication jobApplication) {
        JobApplicationDTO jobApplicationDTO = MapperUtils.map(jobApplication, JobApplicationDTO.class);
        CandidateDTO candidateDTO = candidateService.getById(uid, cid, jobApplicationDTO.getCandidateId());
        jobApplicationDTO.setCandidateObj(candidateDTO);
        List<Long> interviewerIds = resultV2Service.getAllInterviewer(cid, uid, jobApplicationDTO.getCandidateId());
        jobApplicationDTO.setInterviewerIds(interviewerIds);
        Integer sumStateOnboardComplete = onboardOrderRepo.getSumStateComplete(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.COMPLETE.name(), jobApplication.getId());
        jobApplicationDTO.setSumStateOnboardComplete(sumStateOnboardComplete);
        return jobApplicationDTO;
    }

    private List<JobApplicationDTO> toDTOs(String uid, Long cid, List<JobApplication> jobApplications) {
        List<JobApplicationDTO> jobApplicationDTOS = new ArrayList<>();
        jobApplications.forEach(
                e -> {
                    jobApplicationDTOS.add(toDTO(uid, cid, e));
                }
        );

        return jobApplicationDTOS;
    }

    private void validate(JobApplicationDTO dto) {

    }
}
