package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;

import java.util.Optional;

public interface CandidateFilterRepo extends BaseRepo<CandidateFilter,Long> {

    Optional<CandidateFilter> findByCompanyIdAndId(long cid, Long id);
}

