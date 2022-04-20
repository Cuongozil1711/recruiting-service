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
    List<InterviewInvolve> findByCompanyIdAndPositionIdAndTitleIdAndOrgIdAndStatus(Long cid, Long positionId, Long titleId, Long orgId, Integer status);

    @Query(value = " select p.* " +
            " from recruiting_service.Interview_Involve p " +
            " where (p.company_Id = :companyId)" +
            " and (p.interview_Id = :interviewId or :interviewId = -1) " +
            " and (p.org_Id = :orgId or :orgId = -1) " +
            " and (p.position_Id = :positionId or :positionId = -1) " +
            " and (p.title_Id = :titleId or :titleId = -1)" +
            " and (p.room_id = :roomId or :roomId = -1)" +
            " and (p.level_id = :levelId or :levelId = -1)" +
            " and (p.group_id = :groupId or :groupId = -1)" +
            " and (:interviewerId = any(p.interviewer_Id) or :interviewerId = '#') " +
            " and (p.supporter_Id = :supporterId or :supporterId = -1) " +
            " and ((concat(coalesce(p.name,''),coalesce(p.code,'') ))" +
            " like (concat('%',:search\\:\\:varchar ,'%')) or coalesce(:search\\:\\:varchar, '#') = '#' )"+
            " and (p.status = 1 )", nativeQuery = true)
    Page<InterviewInvolve> search(@Param("companyId") Long cid
            , @Param("interviewId") Long interviewId
            , @Param("orgId") Long orgId
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , @Param("roomId") Long roomId
            , @Param("levelId") Long levelId
            , @Param("groupId") Long groupId
            , @Param("interviewerId") String interviewerId
            , @Param("supporterId") Long supporterId
            , @Param("search") String search
            , Pageable pageable);

    @Query(value = " select p " +
            " from InterviewInvolve p " +
            " where (p.companyId = :companyId)" +
            " and (p.interviewId = :interviewId or coalesce(p.interviewId, 0)  = 0) " +
            " and (p.orgId = :orgId or coalesce(p.orgId, 0)  = 0) " +
            " and (p.positionId = :positionId or coalesce(p.positionId, 0)  = 0)" +
            " and (p.titleId = :titleId or coalesce (p.titleId, 0) = 0)" +
            " order by  coalesce(p.interviewId, 0) desc " +
            "           , coalesce(p.positionId, 0) desc " +
            "           , coalesce(p.orgId, 0) desc " +
            "           , coalesce(p.titleId, 0) desc ")
    List<InterviewInvolve> readConfig(@Param("companyId") Long cid
            , @Param("interviewId") Long interviewId
            , @Param("orgId") Long orgId
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId);

    @Query(value = " select p.* " +
            " from recruiting_service.Interview_Involve p " +
            " where (p.company_Id = :companyId)" +
            " and (p.interview_Id = :interviewId or :interviewId = -1) " +
            " and (p.org_Id = :orgId or :orgId = -1) " +
            " and (p.position_Id = :positionId or :positionId = -1) " +
            " and (p.title_Id = :titleId or :titleId = -1)" +
            " and (:interviewerId = any(p.interviewer_Id) or :interviewerId = '#') " +
            " and (p.supporter_Id = :supporterId or :supporterId = -1) " +
            " and (p.status = 1 ) LIMIT 1 ", nativeQuery = true)
    Optional<InterviewInvolve> find(@Param("companyId") Long cid
            , @Param("interviewId") Long interviewId
            , @Param("orgId") Long orgId
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , @Param("interviewerId") String interviewerId
            , @Param("supporterId") Long supporterId);

    List<InterviewInvolve> findByCompanyIdAndStatus(Long cid, Integer status);
}

