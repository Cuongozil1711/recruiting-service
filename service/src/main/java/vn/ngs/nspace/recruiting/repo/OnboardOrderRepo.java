package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.Optional;

public interface OnboardOrderRepo extends BaseRepo<OnboardOrder,Long> {

    Optional<OnboardOrder> findByCompanyIdAndId(long cid, Long id);

    @Query(value = " select p " +
            " from OnboardOrder p " +
            " where (p.companyId = :companyId)" +
            " and (p.buddy = :buddy or :buddy = -1) " +
            " and (p.employeeId = :employeeId or :employeeId = -1) " +
            " and (p.jobApplicationId = :jobApplicationId or :jobApplicationId = -1) ")
    Page<OnboardOrder> search(@Param("companyId") Long cid
            , @Param("buddy") Long positionId
            , @Param("employeeId") Long titleId
            , @Param("jobApplicationId") Long contractTypeId
            , Pageable pageable);
}
