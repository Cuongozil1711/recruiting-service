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





}