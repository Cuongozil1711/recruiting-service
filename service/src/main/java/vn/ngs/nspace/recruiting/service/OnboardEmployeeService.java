package vn.ngs.nspace.recruiting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.spec.OnboardEmployeeFilterSpecification;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class OnboardEmployeeService {

    private final CandidateRepo candidateRepo;
    private final JobApplicationRepo jobApplicationRepo;

    public OnboardEmployeeService(CandidateRepo candidateRepo,JobApplicationRepo _jobApplicationRepo) {
        this.candidateRepo = candidateRepo;
        jobApplicationRepo=_jobApplicationRepo;
    }

    public Page<CandidateDTO> filterEmployeeOnboard(
            Long cid
            , String uid
            , OnboardEmployeeFilterRequest request
            , Pageable pageable
    ) {
        OnboardEmployeeFilterSpecification specification = new OnboardEmployeeFilterSpecification(request);

        Page<Candidate> candidatePage = candidateRepo.findAll(specification, pageable);

        List<CandidateDTO> candidateDTOS = new ArrayList<>();

        candidatePage.getContent();
        candidateDTOS = toDTOs(candidatePage.getContent());

        return new PageImpl(candidateDTOS, candidatePage.getPageable(), candidatePage.getTotalElements());
    }
    /**
     * changeStateJob
     * @param cid
     * @param uid
     * @param id
     * @param state
     * @return
     */
    public JobApplication changeStateJobApplication(
            Long cid
            , String uid
            , Long id
            ,String state
    ) {
       if(state.isEmpty()) state = Constants.HCM_RECRUITMENT_ONBOARD.ONBOARDING.name();
        JobApplication jobApplication = candidateRepo.findState(cid,id, Constants.HCM_RECRUITMENT.STAFF.name()).orElseThrow(() -> new EntityNotFoundException(JobApplication.class, id));
        jobApplication.setState( state);
        jobApplication = jobApplicationRepo.save(jobApplication);
        return jobApplication;
    }
    public List<CandidateDTO> toDTOs(List<Candidate> candidates) {
        Set<Long> ids = new HashSet<>();
        List<CandidateDTO> candidateDTOS = new ArrayList<>();
        candidates.forEach(o -> {
            ids.add(o.getId());
        });
        for (Candidate obj : candidates) {
            CandidateDTO o = toDTO(obj);
            candidateDTOS.add(o);
        }
        return candidateDTOS;
    }

    public CandidateDTO toDTO(Candidate candidate) {
        return MapperUtils.map(candidate, CandidateDTO.class);
    }
}
