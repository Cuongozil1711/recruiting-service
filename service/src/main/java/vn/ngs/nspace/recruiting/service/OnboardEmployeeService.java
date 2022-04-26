package vn.ngs.nspace.recruiting.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OnboardEmployeeService {

    private final CandidateRepo candidateRepo;

    public OnboardEmployeeService(CandidateRepo candidateRepo) {
        this.candidateRepo = candidateRepo;
    }

    public Page<CandidateDTO> filterEmployeeOnboard(
            Long cid
            , String uid
            , OnboardEmployeeFilterRequest request
            , Pageable pageable
    ) {
        String name, code, state;
        Long orgRecruitingId, gender;

        code = request.getCode();
        name = request.getName();
        state = request.getState();
        gender = request.getGender();
        orgRecruitingId = request.getOrgRecruitingId();

        Page<Candidate> candidatePage = candidateRepo.filterCandidateOnboard(code, name, gender, orgRecruitingId, state, Constants.HCM_RECRUITMENT.STAFF.toString(), pageable);

        List<CandidateDTO> candidateDTOS = new ArrayList<>();

        candidatePage.getContent();
        candidateDTOS = toDTOs(candidatePage.getContent());

        return new PageImpl(candidateDTOS, candidatePage.getPageable(), candidatePage.getTotalElements());
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
