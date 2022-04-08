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
    Optional<RecruitmentPlan> findByCompanyIdAndCodeAndStatus(Long cid, String code, Integer status);

    @Query(value = " select p " +
            " from RecruitmentPlan p " +
            " where (p.companyId = :companyId)" +
            " and (p.status = 1)")
    Page<RecruitmentPlan> search(@Param("companyId") Long cid
            , Pageable pageable);


    @Query(value = " select s.* from recruiting_service.recruitment_plan s " +
            "where (s.company_id = :companyId) " +
            " and (s.status = 1)"+
            " and (s.state in :states or '#' in :states)" +
            " and ( coalesce(s.start_date,'2000-01-02') >= :startDateFrom\\:\\:date and coalesce(s.start_date,'2000-01-02')<=:startDateTo\\:\\:date )"+
            " and ( coalesce(s.end_date,'2000-01-02') >= :endDateFrom\\:\\:date and coalesce(s.end_date,'2000-01-02')<=:endDateTo\\:\\:date )"+
            "desc"
//            " and ((concat(coalesce(s.name,'#')" +
//            ", ' ', coalesce(s.code,'#'))) like :search) "
            ,nativeQuery = true)
    Page<RecruitmentPlan> filter(@Param("companyId") Long cid
            , @Param("states") List<String> states
            ,@Param("startDateFrom") Date startDateFrom
            ,@Param("startDateTo") Date startDateTo
            ,@Param("endDateFrom") Date endDateFrom
            ,@Param("endDateTo") Date endDateTo
//            , @Param("search") String search
            , Pageable pageable);
}
