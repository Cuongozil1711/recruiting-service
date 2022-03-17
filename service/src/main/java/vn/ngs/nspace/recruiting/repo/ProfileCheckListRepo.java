package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;

import java.util.List;
import java.util.Optional;

public interface ProfileCheckListRepo extends BaseRepo<ProfileCheckList, Long> {
    Optional<ProfileCheckList> findByCompanyIdAndChecklistIdAndEmployeeIdAndStatus(Long cid, Long checkListId, Long employeeId, Integer status);
    List<ProfileCheckList> findByCompanyIdAndOnboardOrderId(Long cid, Long onboarOrderId);
    Optional<ProfileCheckList> findByCompanyIdAndOnboardOrderIdAndItemIdAndStatus(Long cid, Long OnboardId,Long itemId, Integer status);

    Optional<ProfileCheckList> findByCompanyIdAndId(Long cid, Long id);

    @Query(value = " select p " +
            " from ProfileCheckList p " +
            " where (p.companyId = :companyId)" +
            " and (p.onboardOrderId = :onboardOrderId) " +
            " and (p.status = :status)")
    Optional<ProfileCheckList> findByCidAndOnboardId(
            @Param("companyId") Long cid,
            @Param("onboardOrderId") Long onboardOrderId
            ,@Param("status") Integer status

    );
}
