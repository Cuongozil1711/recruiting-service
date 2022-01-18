package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.JobRequirement;

import java.util.Optional;

public interface JobApplicationRepo extends BaseRepo<JobApplication, Long> {
    Optional<JobApplication> findByCompanyIdAndId(Long cid, Long id);
}

