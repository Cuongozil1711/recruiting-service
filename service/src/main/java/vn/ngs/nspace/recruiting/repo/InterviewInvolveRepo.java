package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewInvolve;
import vn.ngs.nspace.recruiting.model.OnboardOrder;

import java.util.List;
import java.util.Optional;

public interface InterviewInvolveRepo extends BaseRepo<InterviewInvolve,Long> {

    Optional<InterviewInvolve> findByCompanyIdAndId(long cid, Long id);

    @Query(value = " select p " +
            " from InterviewInvolve p " +
            " where (p.companyId = :companyId)" +
            " and (p.interviewId = :interviewId or :interviewId = -1) " +
            " and (p.orgId = :orgId or :orgId = -1) " +
            " and (p.positionId = :positionId or :positionId = -1) " +
            " and (p.interviewerId = :interviewerId or :interviewerId = -1) " +
            " and (p.supporterId = :supporterId or :supporterId = -1) ")
    Page<InterviewInvolve> search(@Param("companyId") Long cid
            , @Param("interviewId") Long interviewId
            , @Param("orgId") Long orgId
            , @Param("positionId") Long positionId
            , @Param("interviewerId") Long interviewerId
            , @Param("supporterId") Long supporterId
            , Pageable pageable);

    @Query(value = " select p " +
            " from InterviewInvolve p " +
            " where (p.companyId = :companyId)" +
            " and (p.interviewId = :interviewId or coalesce(p.interviewId, 0)  = 0) " +
            " and (p.orgId = :orgId or coalesce(p.orgId, 0)  = 0) " +
            " and (p.positionId = :positionId or coalesce(p.positionId, 0)  = 0)" +
            " order by  coalesce(p.interviewId, 0) desc " +
            "           , coalesce(p.positionId, 0) desc " +
            "           , coalesce(p.orgId, 0) desc ")
    List<InterviewInvolve> readConfig(@Param("companyId") Long cid
            , @Param("interviewId") Long interviewId
            , @Param("orgId") Long orgId
            , @Param("positionId") Long positionId);
}

