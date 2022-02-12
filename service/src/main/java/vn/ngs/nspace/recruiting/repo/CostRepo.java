package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.EmailSent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface CostRepo extends BaseRepo<Cost,Long> {

    Optional<Cost> findByCompanyIdAndId(long cid, Long id);
    List<Cost> findByCompanyIdAndOrgIdAndYearAndStatus(long cid, long orgId, long year, int status);

    @Query(value = "select org_id, year, sum(totalAmount) as request_amount, sum(usageAmount) as usage_amount" +
            " from (select cost.org_id, cost.year, cost.totalAmount, coalesce(costDetail.totalAmount, 0) as usageAmount " +
                " from recruiting_service.cost cost " +
                " left join recruiting_service.cost_detail costDetail on cost.id = costDetail.cost_id and costDetail.status = 1" +
                " where (cost.company_id = :companyId) " +
                " and (cost.status = 1) " +
                " and (cost.org_id = :orgId or :orgId = -1) " +
                " and (cost.year = :year or :year = -1)  )"
            , nativeQuery = true
    )
    Page<Map<String, Object>> getSummaryByOrgAndYear(@Param("companyId") long companyId,
                                                     @Param("orgId") Long orgId,
                                                     @Param("year") Long year
                                                    , Pageable pageable);
}

