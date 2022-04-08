package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.task.core.repo.TaskRepo;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobApplicationRepo extends TaskRepo<JobApplication>, BaseRepo<JobApplication,Long> {
    Optional<JobApplication> findByCompanyIdAndId(Long cid, Long id);

    Optional<JobApplication> findByCompanyIdAndCandidateIdAndStatus(long cid, long candidateId, int status);

    @Query(value = "select plan_oder_id, count(*) from recruiting_service.job_application where plan_oder_id is not null group by plan_oder_id",nativeQuery = true)
    List<Map<String,Object>> countStaff();
//    @Query(value = "select j " +
//            " from jobApplication j " +
//            " where (j.companyId = :companyId)" +
//            " and (j.requestId = :requestId) LIMIT 1"
//    )
//    Optional<JobApplication> findOne(@Param("companyId") Long cid
//            , @Param("requestId") Long requestId);
}

