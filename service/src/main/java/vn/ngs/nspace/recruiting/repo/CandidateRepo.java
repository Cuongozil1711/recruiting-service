package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;

import java.util.Optional;

public interface CandidateRepo extends BaseRepo<Candidate,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
}

