package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RecruitmentPlanOrderRepo extends BaseRepo<RecruitmentPlanOrder,Long> {
   Optional<RecruitmentPlanOrder>  findByCompanyIdAndId(Long cid, Long id);


   @Query(" select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.orgId = :org_id or 0 in :orgIds) " +
           " and (p.positionId = :position_id or 0 in :positionIds) " +
           " and (p.startDate = :fromDate) " +
           " and (p.deadline = :toDate)" +
           " order by p.orgId, p.positionId, p.quantity")
   Page<Map<String, Object>> searchRecruitingPlanOrder(@Param("companyId") Long cid
           ,@Param("orgIds") Set<Long> orgIds
           ,@Param("positionIds") Set<Long> positionIds
           ,@Param("fromDate") Date fromDate
           ,@Param("toDate") Date toDate
           , Pageable pageable);
}
