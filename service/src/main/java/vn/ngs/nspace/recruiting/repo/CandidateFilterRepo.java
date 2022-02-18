package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.share.dto.CandidateFilterDTO;

import java.util.List;
import java.util.Optional;

public interface CandidateFilterRepo extends BaseRepo<CandidateFilter,Long> {

    Optional<CandidateFilter> findByCompanyIdAndId(long cid, Long id);

    List<CandidateFilter> findByCompanyIdAndStatus(Long cid, Integer status);
}

