package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;

import javax.transaction.Transactional;
import java.util.*;

public interface RecruitmentPlanRequestRepo extends BaseRepo<RecruitmentPlanRequest, Long> {

    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentRequestIdAndStatus(Long companyId, Long recruitmentRequestId, Integer status);
    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentPlanIdAndStatus(Long cid, Long planId, Integer status);

    @Transactional
    @Modifying
    @Query("delete from RecruitmentPlanRequest rpr where rpr.recruitmentPlanId = :planId and rpr.companyId = :cid")
    void deleteByPlanId(Long cid, Long planId);
}
