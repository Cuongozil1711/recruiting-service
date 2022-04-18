package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.RecruitmentPlan;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.*;

public interface RecruitmentPlanRepo extends BaseRepo<RecruitmentPlan,Long> {
    Optional<RecruitmentPlan>  findByCompanyIdAndId(Long cid, Long id);
    List<RecruitmentPlan>   findByCompanyIdAndStatus(Long cid, Integer status);
    Optional<RecruitmentPlan> findByCompanyIdAndCodeAndStatus(Long cid ,String code, Integer status);

    @Query(value = " select p " +
            " from RecruitmentPlan p " +
            " where (p.companyId = :companyId)" +
            " and (p.status = 1)"+
            "order by p.createDate desc ")
    Page<RecruitmentPlan> search(@Param("companyId") Long cid
            , Pageable pageable);
    @Query(value = "select sum(job.sum_quanity\\:\\:numeric) as sum_quanity,sum(job.sum_recruting) as sum_recruting,sum(job.sum_recruting_all) as sum_recruting_all from recruiting_service.recruitment_plan job where (job.company_id = :companyId) and (job.status = 1) " +
            "and job.state in ('INIT','PROCESSING')"+
            "and  ((job.create_by = :createBy) or coalesce(:createBy,'#') = '#')" +
            " and (job.start_date between :startDateFrom\\:\\:date and :startDateTo\\:\\:date)" +
            " and (job.end_date between :endDateFrom\\:\\:date and :endDateTo\\:\\:date)" +
            " and ((concat(coalesce(job.name, ''), coalesce(job.code, ''))) like (concat('%', :search\\:\\:varchar , '%')) or coalesce(:search\\:\\:varchar, '#') = '#')",nativeQuery = true)
    Map<String,Object> sumAll(@Param("companyId") Long cid

            ,@Param("startDateFrom") Date startDateFrom
            ,@Param("startDateTo") Date startDateTo
            ,@Param("endDateFrom") Date endDateFrom
            ,@Param("endDateTo") Date endDateTo
            ,@Param("createBy") String createBy
            , @Param("search") String search);

    @Query(value = " select s.* from recruiting_service.recruitment_plan s " +
            "where (s.company_id = :companyId) " +
            " and (s.status = 1)"+
            "and  ((s.create_by = :createBy) or coalesce(:createBy,'#') = '#')"+
            " and (s.start_date between :startDateFrom\\:\\:date and :startDateTo\\:\\:date)"+
            " and (s.end_date between :endDateFrom\\:\\:date and :endDateTo\\:\\:date)"+
            " and (s.state in :states or '#' in (:states))" +
            " and ((concat(coalesce(s.name, ''), coalesce(s.code, ''))) like (concat('%', :search\\:\\:varchar , '%')) or coalesce(:search\\:\\:varchar, '#') = '#')"+
            "order by s.create_date desc "
            ,nativeQuery = true)
    Page<RecruitmentPlan> filter(@Param("companyId") Long cid
            , @Param("states") List<String> states
            ,@Param("startDateFrom") Date startDateFrom
            ,@Param("startDateTo") Date startDateTo
            ,@Param("endDateFrom") Date endDateFrom
            ,@Param("endDateTo") Date endDateTo
            ,@Param("createBy") String createBy
            , @Param("search") String search
            , Pageable pageable);
}
