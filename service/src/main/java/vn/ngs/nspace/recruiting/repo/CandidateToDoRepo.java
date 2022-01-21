package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateTodo;

import java.util.Optional;

public interface CandidateToDoRepo extends BaseRepo<CandidateTodo,Long> {
    Optional<CandidateTodo> findByCompanyIdAndId(Long cid, Long id);

//    Page<CandidateTodo> search(Long cid, String search, Long candidateId, Long responsibleId, Pageable pageable);
}
