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

   @Query(value = " select a.position_id, a.org_id, count(*) as apply" +
                  " from (select rpo.org_id as org_id, rpo.position_id as position_id, rpo.quantity as quantity, rpo.start_date as start_date, rpo.deadline as deadline " +
                  " from recruiting_service.recruitment_plan_order rpo"+
                  " left join recruiting_service.job_application c on rpo.position_id = c.position_id and rpo.org_id = c.org_id "+
                  " where (rpo.company_id = :companyId) "+
                  " and (rpo.status = 1)"+
                  " and (rpo.org_id = :org_id or :org_id = -1)"+
                  " and (rpo.position_id = :position_id or :position_id = -1)"+
                  " and ((:startDate between rpo.start_date and rpo.deadline )" +
                  " or (:endDate between rpo.start_date and rpo.deadline))) as a"+
                  " group by a.org_id,a.position_id,a.start_date,a.deadline"+
                  " order by a.org_id,a.position_id,a.start_date,a.deadline desc"
   , countQuery = "select count(*)"+
                  "from (select rpo.org_id as org_id, rpo.position_id as position_id, rpo.quantity as quantity, rpo.start_date as start_date, rpo.deadline as deadline"+
                  " from recruiting_service.recruitment_plan_order rpo"+
                  " left join recruiting_service.job_application c on rpo.position_id = c.position_id and rpo.org_id = c.org_id"+
                  " where (rpo.company_id = :companyId )"+
                  "and (rpo.status = 1)"+
                  "and (rpo.org_id = org_id or org_id = -1)"+
                  "and (rpo.position_id = :position_id or :position_id = -1)"+
                  "and ((:startDate between rpo.start_date and rpo.deadline )"+
                  "or (endDate between rpo.start_date and rpo.deadline))) as a"+
                  " group by a.org_id,a.position_id)"
           ,nativeQuery = true
   )
   Page<Map<String,Object>> searchByOrgAndPositionAndStartDateAndEndDate(@Param("companyId") Long cid
           ,@Param("position_id") Long positionId
           ,@Param("org_id") Long orgId
           ,@Param("startDate") Date startDate
           ,@Param("endDate") Date endDate
           ,Pageable pageable);

   @Query(value = "select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.positionId = :positionId or :positionId = -1)" +
           " and (p.orgId = :orgId or :orgId = -1)" +
           " and (p.type = :type or :type = 'all')" +
           " and (p.solutionSuggestType = :solutionSuggestType or :solutionSuggestType = 'all')" +
           "and (lower(p.code) like (concat('%',:code,'%')) or :code = 'all') ")
   Page<RecruitmentPlanOrder> filter(@Param("companyId") Long cid
            ,@Param("positionId") Long positionId
            ,@Param("orgId") Long orgId
            ,@Param("code") String code
            ,@Param("solutionSuggestType") String solutionSuggestType
            ,@Param("type") String type
            , Pageable pageable);

}
