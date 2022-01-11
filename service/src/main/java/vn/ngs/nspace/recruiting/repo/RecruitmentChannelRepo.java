package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.RecruitmentChannel;

import java.util.List;
import java.util.Optional;

public interface RecruitmentChannelRepo extends BaseRepo<RecruitmentChannel,Long> {

    Optional<RecruitmentChannel> findByCompanyIdAndId(long cid, Long id);
    List<RecruitmentChannel> findByCompanyIdAndStatus(long cid, int status);
}

