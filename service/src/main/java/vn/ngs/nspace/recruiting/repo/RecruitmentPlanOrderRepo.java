package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.*;

public interface RecruitmentPlanOrderRepo extends BaseRepo<RecruitmentPlanOrder,Long> {
   Optional<RecruitmentPlanOrder>  findByCompanyIdAndId(Long cid, Long id);


   @Query(" select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.orgId in :orgId or :orgId = -1) " +
           " and (p.positionId in :positionId or :positionId = -1) " +
           " and (p.startDate = COALESCE(:startDate, now()) or COALESCE(:startDate, now()) = now()) " +
           " and (p.deadline = COALESCE(:deadline, now()) or COALESCE(:deadline, now()) = now()) " +
//           " and (p.startDate >= :startDate and p.deadline <= :deadline )" +
           " order by p.orgId, p.positionId, p.quantity")
   Page<RecruitmentPlanOrder> searchRecruitingPlanOrder(@Param("companyId") Long cid
           ,@Param("orgId") Long orgId
           ,@Param("positionId") Long positionId
           ,@Param("startDate") Date startDate
           ,@Param("deadline") Date deadline
           , Pageable pageable);


}
