package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.*;

public interface RecruitmentPlanOrderRepo extends BaseRepo<RecruitmentPlanOrder,Long> {
   Optional<RecruitmentPlanOrder>  findByCompanyIdAndId(Long cid, Long id);
   Optional<RecruitmentPlanOrder> findByCompanyIdAndFromCodeAndStatus(Long cid, String fromCode, Integer status);
   List<RecruitmentPlanOrder> findByCompanyIdAndPlanIdInAndStatus(Long cid, Set<Long> planID, Integer status);

   @Query(value = " select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.orgId = :orgId or :orgId = -1l) " +
           " and (p.startDate >= :startDate)"+
           " and (p.deadline <= :deadline)"+
           " and (p.positionId in :positionId or :positionId = -1) "
           )

   Page<RecruitmentPlanOrder> searchRecruitingPlanOrder(@Param("companyId") Long cid
           ,@Param("orgId") Long orgId
           ,@Param("positionId") Long positionId
           ,@Param("startDate") Date startDate
           ,@Param("deadline") Date deadline
           , Pageable pageable);

    @Query(value = "select plan_id, sum(quantity) from recruiting_service.recruitment_plan_order where plan_id is not null group by plan_id",nativeQuery = true)

    List<Map<String,Object>> sumQuanity();


//   @Query(value = " select a.position_id, a.org_id, count(*) as apply" +
//                  " from (select rpo.org_id as org_id, rpo.position_id as position_id, rpo.quantity as quantity, rpo.start_date as start_date, rpo.deadline as deadline " +
//                  " from recruiting_service.recruitment_plan_order rpo"+
//                  " left join recruiting_service.job_application c on rpo.position_id = c.position_id and rpo.org_id = c.org_id "+
//                  " where (rpo.company_id = :companyId) "+
//                  " and (rpo.status = 1)"+
//                  " and (rpo.org_id = :org_id or :org_id = -1)"+
//                  " and (rpo.position_id = :position_id or :position_id = -1)"+
//                  " and ((:startDate between rpo.start_date and rpo.deadline )" +
//                  ")) as a"+
//                  " group by a.position_id,a.org_id"+
//                  " order by a.org_id,a.position_id,a.start_date,a.deadline desc"
    @Query(value = "select rpo.org_id,rpo.position_id,rpo.quantity,rpo.start_date,rpo.deadline,count(*) as total"+
                  " from recruiting_service.recruitment_plan_order rpo"+
                  " left join recruiting_service.job_application c on rpo.position_id = c.position_id and rpo.org_id = c.org_id"+
                  " where (rpo.company_id = :companyId )"+
                  "and (rpo.status = 1)"+
                  "and (rpo.org_id = :org_id or :org_id = -1)"+
                  "and (rpo.position_id = :position_id or :position_id = -1)"+
                  "and ((:startDate between rpo.start_date and rpo.deadline ))"+
                  " group by rpo.start_date, rpo.position_id, rpo.quantity, rpo.org_id, rpo.deadline" +
                  " order by rpo.org_id,rpo.position_id,rpo.start_date,rpo.deadline desc"
           ,nativeQuery = true
   )
   Page<Map<String,Object>> searchByOrgAndPositionAndStartDateAndEndDate(@Param("companyId") Long cid
           ,@Param("position_id") Long positionId
           ,@Param("org_id") Long orgId
           ,@Param("startDate") Date startDate
           ,Pageable pageable);
   @Query(value = "select * from recruiting_service.recruitment_plan_order s " +
           "where s.company_id = :companyId" +
           " and s.plan_id = :planId "+
           " and (s.state in :states or '#' in :states)" +
           " and ( coalesce(s.deadline,'2000-01-02') >= :deadlineFrom\\:\\:date and coalesce(s.deadline,'2000-01-02')<=:deadlineTo\\:\\:date )"+
           " and s.pic = :pic" +
           " and s.room = :room" +
           " and s.position_id = :positionId" +
           " and s.title_id = :titleId" +
           " and s.solution_suggest_type = :solutionSuggestType" +
           " and s.type = :type desc",nativeQuery = true)
   Page<RecruitmentPlanOrder> searchByFilter (
           @Param("companyId") Long cid
           ,@Param("planId") String planId
           , @Param("states") List<String> states
           ,@Param("deadlineFrom") Date deadlineFrom
           ,@Param("deadlineTo") Date deadlineTo
           ,@Param("pic") String pic
           ,@Param("room") String room
           ,@Param("positionId") String positionId
           ,@Param("titleId") String titleId
           ,@Param("solutionSuggestType") String solutionSuggestType
           ,@Param("type") String type
           ,Pageable pageable
   );

   @Query(value = "select count(*) as recruited, c.position_id"+
         "  from recruiting_service.job_application c"+
         "  inner join recruiting_service.recruitment_plan_order rpo on rpo.position_id = c.position_id and rpo.org_id = c.org_id"+
         "  where (rpo.company_id = :companyId )"+
         "  and (rpo.status = 1)"+
         "  and (rpo.org_id = :org_id or :org_id = -1)"+
         "  and (rpo.position_id = :position_id or :position_id = -1)" +
         "  and ((:startDate between rpo.start_date and rpo.deadline ))"+
         "  and (c.state = 'STAFF')" +
         "  group by c.position_id, c.org_id",
           nativeQuery = true
    )
   Map<String,Object> searchByState(@Param("companyId") Long cid
            ,@Param("position_id") Long positionId
            ,@Param("org_id") Long orgId
            ,@Param("startDate")Date startDate
            );

    @Query(value = "select  count(*) as totalRecruit"+
            "  from recruiting_service.job_application c"+
            "  inner join recruiting_service.recruitment_plan_order rpo on rpo.position_id = c.position_id and rpo.org_id = c.org_id"+
            "  where (rpo.company_id = :companyId )"+
            "  and (rpo.status = 1)"+
            "  and (rpo.org_id = :org_id or :org_id = -1)"+
            "  and (rpo.position_id = :position_id )" +
            "  and (rpo.start_date >= :startDate )"+
            "  and (rpo.deadline <= :deadline )" +
            " and (c.status = 1)"+
            "  group by c.position_id",
            nativeQuery = true
    )
    Long getCountJobApplications(@Param("companyId") Long cid
            ,@Param("org_id") Long orgId
            ,@Param("position_id") Long positionId
            ,@Param("startDate")Date startDate
            ,@Param("deadline") Date deadline
    );

    @Query(value = "select  count(*) as recruited"+
            "  from recruiting_service.job_application c"+
            "  inner join recruiting_service.recruitment_plan_order rpo on rpo.position_id = c.position_id and rpo.org_id = c.org_id"+
            "  where (rpo.company_id = :companyId )"+
            "  and (rpo.status = 1)"+
            "  and (rpo.org_id = :org_id or :org_id = -1)"+
            "  and (rpo.position_id = :position_id )" +
            "  and (rpo.start_date >= :startDate)"+
            "  and (rpo.deadline <= :deadline)"+
            "  and (c.state = 'STAFF')" +
            "  and (c.status = 1)" +
            "  group by c.position_id",
            nativeQuery = true
    )
    Long getCountJobApplication(@Param("companyId") Long cid
            ,@Param("org_id") Long orgId
            ,@Param("position_id") Long positionId
            ,@Param("startDate")Date startDate
            ,@Param("deadline") Date deadline
    );

   @Query(value = "select p " +
           " from RecruitmentPlanOrder p " +
           " where (p.companyId = :companyId)" +
           " and (p.positionId = :positionId or :positionId = -1)" +
           " and (p.orgId = :orgId or :orgId = -1)" +
           " and (p.type = :type or :type = 'all')" +
           " and (p.solutionSuggestType = :solutionSuggestType or  :solutionSuggestType = '#'  )" +
           "and (lower(p.fromCode) like (concat('%',:code,'%')) or :code = 'all') ")
   Page<RecruitmentPlanOrder> filter(@Param("companyId") Long cid
            ,@Param("positionId") Long positionId
            ,@Param("orgId") Long orgId
            ,@Param("code") String code
            ,@Param("solutionSuggestType") String solutionSuggestType
            ,@Param("type") String type
            , Pageable pageable);

}
