package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;

import java.util.List;
import java.util.Optional;

public interface OnboardOrderCheckListRepo extends BaseRepo<OnboardOrderCheckList,Long> {

    List<OnboardOrderCheckList> findByCompanyIdAndOnboardOrderIdAndCodeIn(Long cid, Long orderId, List<String> codes);
}

