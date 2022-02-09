package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.model.InterviewResult;

import java.util.Optional;

public interface InterviewResultRepo extends BaseRepo<InterviewResult, Long> {
    Optional<InterviewResult> findByCompanyIdAndId(Long cid, Long id);



    @Query(value = "select cl " +
            " from InterviewResult i" +
            " left join InterviewCheckList cl on cl.interviewResultId = i.id" +
            " where (i.companyId = :companyId)" +
            " and (i.id = :id)")
    Optional<InterviewCheckList> getInterviewResult(@Param("companyId") Long cid
            ,@Param("id") Long id);

    @Query(value = "select i " +
            " from InterviewResult i " +
            " where (i.companyId = :companyId)" +
            " and (i.candidateId = :candidateId or :candidateId = -1)" +
            " and (i.name = :name or :name = 'all')" +
            " and (i.state = :state or :state = 'all')")
    Page<InterviewResult> search(@Param("companyId") Long cid
            ,@Param("candidateId") Long candidateId
            ,@Param("name") String name
            ,@Param("state") String state, Pageable pageable);
}