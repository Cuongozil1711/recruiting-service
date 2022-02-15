package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.*;

public interface RecruitmentPlanOrderRepo extends BaseRepo<RecruitmentPlanOrder,Long> {
   Optional<RecruitmentPlanOrder>  findByCompanyIdAndId(Long cid, Long id);
   Optional<RecruitmentPlanOrder> findByCompanyIdAndCodeAndStatus(Long cid, String code, Integer status);

   @Query(value = " select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.orgId in :orgId or :orgId = -1) " +
           " and (p.positionId in :positionId or :positionId = -1) "
           )
   Page<RecruitmentPlanOrder> searchRecruitingPlanOrder(@Param("companyId") Long cid
           ,@Param("orgId") Long orgId
           ,@Param("positionId") Long positionId
           , Pageable pageable);


   @Query(value = "select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.orgId = :orgId or :orgId = -1)" +
           " and (concat(coalesce(p.code,'')" +
           ", coalesce(p.type,'')" +
           ", coalesce(p.solutionSuggestType,'')" +
           ",coalesce(p.positionId,-1))" +
           " like %:filter%)")
   Page<RecruitmentPlanOrder> filter(@Param("companyId") Long cid
            ,@Param("orgId") Long orgId
            ,@Param("filter") String filter
            , Pageable pageable);

}
