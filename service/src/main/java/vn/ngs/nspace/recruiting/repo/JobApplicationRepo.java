package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobApplicationRepo extends BaseRepo<JobApplication, Long> {
    Optional<JobApplication> findByCompanyIdAndId(Long cid, Long id);

    Optional<JobApplication> findByCompanyIdAndCandidateIdAndStatus(long cid, long candidateId, int status);


}

