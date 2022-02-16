package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardContract;
import vn.ngs.nspace.recruiting.model.OnboardOrder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardContractRepo extends BaseRepo<OnboardContract,Long> {

    Optional<OnboardContract> findByCompanyIdAndId(long cid, Long Id);
    Optional<OnboardContract> findByCompanyIdAndOnboardOrderId(long cid, Long orderId);
    List<OnboardContract> findByCompanyIdAndOnboardOrderIdIn(Long cid, Set<Long> orderIds);
}

