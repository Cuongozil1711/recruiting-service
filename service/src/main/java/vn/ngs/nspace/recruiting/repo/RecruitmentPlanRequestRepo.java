package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;

import java.util.*;

public interface RecruitmentPlanRequestRepo extends BaseRepo<RecruitmentPlanRequest, Long> {

    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentRequestIdAndStatus(Long companyId, Long recruitmentRequestId, Integer status);
    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentPlanIdAndStatus(Long cid, Long planId, Integer status);
}
