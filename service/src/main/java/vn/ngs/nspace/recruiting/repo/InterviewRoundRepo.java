package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewRound;

import java.util.List;
import java.util.Optional;

public interface InterviewRoundRepo extends BaseRepo<InterviewRound,Long> {
    Optional<InterviewRound> findByCompanyIdAndId(long cid, Long id);
    Optional<InterviewRound> findByCompanyIdAndCode(long cid, String code);
    List<InterviewRound> findByCompanyId(long cid);
}

