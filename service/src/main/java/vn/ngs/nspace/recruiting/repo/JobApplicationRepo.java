package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.task.core.repo.TaskRepo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobApplicationRepo extends TaskRepo<JobApplication>, BaseRepo<JobApplication, Long> {
    Optional<JobApplication> findByCompanyIdAndId(Long cid, Long id);

    @Modifying
    @Transactional
    @Query(value = "update JobApplication set state = 'ONBOARD' where id = :id")
    void updateStateById(@Param("id") Long id);

    Optional<JobApplication> findByCompanyIdAndCandidateIdAndStatus(long cid, long candidateId, int status);

    @Query(value = "select plan_oder_id, count(*) from recruiting_service.job_application job " +
            "where (job.company_id = :companyId)" +
            "and (job.position_id = :positionId )" +
            "and (job.org_id = :orgId)" +
            " and (job.plan_oder_id = :planOderId)" +
            "and (job.state = 'STAFF')" +
            "group by plan_oder_id", nativeQuery = true)
    List<Map<String, Object>> countStaff(@Param("companyId") Long companyId
            , @Param("positionId") Long positionId
            , @Param("orgId") Long orgId
            , @Param("planOderId") Long planOderId);

    @Query(value = "select plan_oder_id, count(*) from recruiting_service.job_application job " +
            "where (job.company_id = :companyId)" +
            "and (job.position_id = :positionId )" +
            "and (job.org_id = :orgId)" +
            " and (job.plan_oder_id = :planOderId)" +
            "group by plan_oder_id", nativeQuery = true)
    List<Map<String, Object>> countAll(@Param("companyId") Long companyId
            , @Param("positionId") Long positionId
            , @Param("orgId") Long orgId
            , @Param("planOderId") Long planOderId);

    @Query(value = "select c " +
            " from JobApplication c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " order by c.id DESC "
    )
        // or coalesce(:search, '#') = '#'
    Page<JobApplication> search(
            @Param("companyId") Long cid
//            ,@Param("search") String search
//            ,@Param("fullname") String fullname
//            ,@Param("gender") Long gender
//            ,@Param("wardCode") String wardCode
//            ,@Param("phone") String phone
//            ,@Param("email") String email
            , Pageable pageable);

    @Query(value = "select * from recruiting_service.job_application job " +
            "where (job.company_id = :companyId)" +
            "and (job.position_id = :positionId )" +
            "and (job.planning_id = :planningId)" +
            "and (job.candidate_id = :candidateId)" +
            " and (job.plan_oder_id = :planOderId)", nativeQuery = true)
    Optional<JobApplication> checkJobApplicationDuplicate(@Param("companyId") Long companyId
            , @Param("positionId") Long positionId
            , @Param("planningId") Long planningId
            , @Param("planOderId") Long planOderId
            , @Param("candidateId") Long candidateId);

    // new
    Optional<JobApplication> findByCandidateIdAndPlanningIdAndCompanyIdAndStatus(Long candidateId, Long planId, Long cid, Integer status);

    @Query("select ja " +
            " from JobApplication ja inner join Candidate c on c.id = ja.candidateId" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and ja.status = 1" +
            " and (c.applyPositionId = :applyPosition or :applyPosition = -1)" +
            " and (c.cvSourceId = :resource or :resource = -1)" +
            " and (c.applyDate between :applyDateFrom and :applyDateTo)" +
            " and (c.graduationYear >= :graduationFrom and c.graduationYear <= :graduationTo or c.graduationYear is null )" +
            " and (c.gender = :gender or :gender = -1)" +
            " and (cast(:ageLess AS java.time.LocalDateTime) is null  or (:ageLess < c.birthDate))" +
            " and (c.experience = :experience or coalesce(:experience,'#') ='#')" +
            " and (c.educationLevel in :educationLevel or -1L in (:educationLevel))" +
            " and (c.language in :language or -1L in (:language))" +
            " and (c.state in :states or '#' in (:states))" +
            " and ((concat(lower(coalesce(c.fullName,'')),lower(coalesce(c.code,'')),lower(coalesce(c.wardCode,'')), lower(coalesce(c.phone,'')), lower(coalesce(c.email,'') )))" +
            " like lower(concat('%',:search,'%')) or coalesce(:search, '#') = '#' )" +
            " order by c.id DESC")
    Page<JobApplication> getPage(
            @Param("companyId") Long cid
            , @Param("search") String search
            , @Param("states") List<String> states
            , @Param("educationLevel") List<Long> educationLevel
            , @Param("language") List<Long> language
            , @Param("applyDateFrom") Date applyDateFrom
            , @Param("applyDateTo") Date applyDateTo
            , @Param("graduationFrom") Integer graduationFrom
            , @Param("graduationTo") Integer graduationTo
            , @Param("gender") Long gender
            , @Param("applyPosition") Long applyPosition
            , @Param("resource") Long resource
            , @Param("experience") String experience
            , @Param("ageLess") Date ageLess
            , Pageable pageable
    );

    // new
    @Query("select jb from JobApplication jb where jb.status =1 and jb.candidateId= :candidateId and jb.companyId = :companyId")
    JobApplication findByStatusCompanyIdCandidateId(Long candidateId, Long companyId);

    @Modifying
    @Query(value = "update JobApplication j set j.status = 0, j.updateBy = :uid where j.companyId = :cid and j.id in :ids")
    void removeAllBy(Long cid, String uid, List<Long> ids);
}

