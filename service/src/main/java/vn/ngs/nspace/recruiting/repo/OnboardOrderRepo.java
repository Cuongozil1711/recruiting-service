package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.Mapping;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OnboardOrderRepo extends BaseRepo<OnboardOrder,Long> {

    Optional<OnboardOrder> findByCompanyIdAndId(long cid, Long Id);

    @Query(value = " select p " +
            " from OnboardOrder p " +
            " where (p.companyId = :companyId)" +
            " and (p.buddy = :buddy or :buddy = -1) " +
            " and (p.employeeId in (:empIds))"+
            " and (p.jobApplicationId = :jobApplicationId or :jobApplicationId = -1) ")
    Page<OnboardOrder> search(@Param("companyId") Long cid
            , @Param("buddy") Long buddy
            , @Param("jobApplicationId") Long jobApplicationId
            , @Param("empIds") List<Long> empIds
            , Pageable pageable);

    @Query(value = " select p " +
            " from OnboardOrder p " +
            " where (p.companyId = :companyId)" +
            " and (p.buddy = :buddy or :buddy = -1) " +
            " and (p.employeeId = :employeeId or :employeeId = -1) " +
            " and (p.jobApplicationId = :jobApplicationId or :jobApplicationId = -1) ")
    Page<OnboardOrder> searchAll(@Param("companyId") Long cid
            , @Param("buddy") Long buddy
            , @Param("employeeId") Long employeeId
            , @Param("jobApplicationId") Long jobApplicationId
            , Pageable pageable);


    @Query(value = " select ja " +
            " from OnboardOrder p " +
            " inner join JobApplication ja on p.jobApplicationId = ja.id" +
            " where (p.companyId = :companyId)" +
            " and (p.id = :id)" )
    Optional<JobApplication> getInfoOnboard(@Param("companyId") Long cid
            , @Param("id") Long id);



}

