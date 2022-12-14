package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.EmailSent;

import javax.transaction.Transactional;
import java.util.*;

public interface CostRepo extends BaseRepo<Cost,Long> {
//    List<Cost> findByCompanyIdAndOrgIdAndYearAndStatus(long cid, long orgId, long year, int status);
//    Optional<Cost> findByCompanyIdAndCostTypeIdAndOrgIdAndYearAndStartDateAndEndDateAndStatus(Long cid,Long costTypeId, Long orgId, Long year, Date startDate, Date endDate, Integer status);

    @Query(value = "select t.org_id, t.year, sum(t.total_amount) as request_amount, sum(t.usage_amount) as usage_amount" +
            " from (select cost.org_id as org_id, cost.year as year, cost.total_amount as total_amount, coalesce(costDetail.total_amount, 0) as usage_amount " +
                " from recruiting_service.cost cost " +
                " left join recruiting_service.cost_detail costDetail on cost.id = costDetail.cost_id and costDetail.status = 1" +
                " where (cost.company_id = :companyId) " +
                " and (cost.status = 1) " +
                " and (cost.org_id = :orgId or :orgId = -1) " +
                " and (cost.year = :year or :year = -1)  ) as t " +
            " group by t.org_id, t.year " +
            " order by t.org_id, t.year desc "
            , countQuery = "select count(*) " +
                " from (select cost.org_id as org_id, cost.year as year, cost.total_amount as total_amount, coalesce(costDetail.total_amount, 0) as usage_amount " +
                " from recruiting_service.cost cost " +
                " left join recruiting_service.cost_detail costDetail on cost.id = costDetail.cost_id and costDetail.status = 1" +
                " where (cost.company_id = :companyId) " +
                " and (cost.status = 1) " +
                " and (cost.org_id = :orgId or :orgId = -1) " +
                " and (cost.year = :year or :year = -1)  ) as t " +
                " group by t.org_id, t.year "
            , nativeQuery = true
    )

    Page<Map<String, Object>> getSummaryByOrgAndYear(@Param("companyId") long companyId,
                                                     @Param("orgId") Long orgId,
                                                     @Param("year") Long year
                                                    , Pageable pageable);

    // new

    @Transactional
    @Query("delete from Cost c where c.status = 1 and c.id in :ids and c.companyId = :cid")
    void deleteAllByCIdAndIdIn(Long cid, List<Long> ids);
    Optional<Cost> findByCompanyIdAndStatusAndId(Long companyId, Integer status, Long id);
    List<Cost> findAllByCompanyIdAndStatusAndNewsId(Long companyId, Integer status, Long newsId);
    List<Cost> findAllByCompanyIdAndStatusAndIdIn(Long companyId, Integer status, List<Long> ids);

    @Query(value = "select c from Cost c where c.status=1 and c.companyId=:cid and c.name like :search and (:newId is null or c.newsId =:newId)")
    List<Cost> filterByNewId(@Param("cid") long cid,  @Param("newId") Long newId, @Param("search") String search, Pageable pageable);

}

