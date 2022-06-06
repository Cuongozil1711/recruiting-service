package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentRequest;

import java.util.Optional;

public interface RecruitmentRequestRepo extends BaseRepo<RecruitmentRequest, Long> {
    Optional<RecruitmentRequest> findByCompanyIdAndIdAndStatus(Long companyId, Long id, Integer status);
}
