package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardOrderCheckListRepo extends BaseRepo<OnboardOrderCheckList,Long> {

//    Optional<OnboardOrderCheckList> findByCompanyIdAndOnboardOrderIdAndId(Long cid, Long onboardOrderId, Long id);
    Optional<OnboardOrderCheckList> findByCompanyIdAndId(Long cid, Long id);
//  //  List<OnboardOrderCheckList> findByCompanyIdAndOnboardOrderIdAndCodeIn(Long cid, Long orderId, List<String> codes);
//    Optional<OnboardOrderCheckList> findByCompanyIdAndIdAndEmployeeId(Long cid, Long id, Long employeeId);
//    List<OnboardOrderCheckList> findByCompanyIdAndOnboardOrderId (Long cid, Long onboardOrderId);
//    List<OnboardOrderCheckList> findByCompanyIdAndOnboardOrderIdIn (Long cid, Set<Long> orderIds);

    // new

    @Query("select o " +
            "from OnboardOrderCheckList o " +
            "where o.status = 1 " +
            "and o.companyId = :companyId")
    Page<OnboardOrderCheckList> getPageOnboard(
            @Param("companyId") Long companyId
            , Pageable pageable
    );

    @Query("select o from OnboardOrderCheckList o where o.status = 1")
    List<OnboardOrderCheckList> getAllCheckList();

    @Query("select o from OnboardOrderCheckList o where o.companyId= :cid and o.id in :ids and o.status =1")
    List<OnboardOrderCheckList> getAllByListId(Long cid, List<Long> ids);
}

