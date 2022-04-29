package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.task.core.repo.TaskRepo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobApplicationRepo extends TaskRepo<JobApplication>, BaseRepo<JobApplication,Long> {
    Optional<JobApplication> findByCompanyIdAndId(Long cid, Long id);

    @Modifying
    @Transactional
    @Query(value = "update JobApplication set state = 'ONBOARD' where id = :id")
    void updateStateById(@Param("id")Long id);

    Optional<JobApplication> findByCompanyIdAndCandidateIdAndStatus(long cid, long candidateId, int status);

    @Query(value = "select plan_oder_id, count(*) from recruiting_service.job_application job " +
            "where (job.company_id = :companyId)" +
            "and (job.position_id = :positionId )"+
            "and (job.org_id = :orgId)"+
            " and (job.plan_oder_id = :planOderId)"+
            "and (job.state = 'STAFF')"+
            "group by plan_oder_id",nativeQuery = true)
    List<Map<String,Object>> countStaff(@Param("companyId") Long companyId
                                        ,@Param("positionId") Long positionId
                                        ,@Param("orgId") Long orgId
                                        ,@Param("planOderId") Long planOderId);

    @Query(value = "select plan_oder_id, count(*) from recruiting_service.job_application job " +
            "where (job.company_id = :companyId)" +
            "and (job.position_id = :positionId )"+
            "and (job.org_id = :orgId)"+
            " and (job.plan_oder_id = :planOderId)"+
            "group by plan_oder_id",nativeQuery = true)
    List<Map<String,Object>> countAll(@Param("companyId") Long companyId
            ,@Param("positionId") Long positionId
            ,@Param("orgId") Long orgId
            ,@Param("planOderId") Long planOderId);

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
            "and (job.position_id = :positionId )"+
            "and (job.planning_id = :planningId)"+
            "and (job.candidate_id = :candidateId)"+
            " and (job.plan_oder_id = :planOderId)",nativeQuery = true)
    Optional<JobApplication> checkJobApplicationDuplicate(@Param("companyId") Long companyId
            ,@Param("positionId") Long positionId
            ,@Param("planningId") Long planningId
            ,@Param("planOderId") Long planOderId
            ,@Param("candidateId") Long candidateId);
//    @Query(value = "select j " +
//            " from jobApplication j " +
//            " where (j.companyId = :companyId)" +
//            " and (j.requestId = :requestId) LIMIT 1"
//    )
//    Optional<JobApplication> findOne(@Param("companyId") Long cid
//            , @Param("requestId") Long requestId);
}

