package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.CandidateTodo;

import java.util.Optional;

public interface CandidateToDoRepo extends BaseRepo<CandidateTodo,Long> {
    Optional<CandidateTodo> findByCompanyIdAndId(Long cid, Long id);


    @Query(value = "select c.*, can.full_name" +
            " from recruiting_service.candidate_todo c" +
            " left join recruiting_service.candidate can on c.candidate_id = can.id" +
            " where (c.company_id = :companyId)" +
            " and (c.title = :title or :title = 'all')" +
            " and (c.candidate_id = :candidateId or :candidateId = -1)" +
            " and (c.responsible_id = :responsibleId or :responsibleId = -1)" +
            " and (c.status = 1)", nativeQuery = true)
    Page<CandidateTodo> search(
            @Param("companyId") Long cid
            ,@Param("title") String title
            ,@Param("candidateId") Long candidateId
            ,@Param("responsibleId") Long responsibleId
            , Pageable pageable);
}
