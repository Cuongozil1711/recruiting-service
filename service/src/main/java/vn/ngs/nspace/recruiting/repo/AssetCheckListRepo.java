package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AssetCheckListRepo extends BaseRepo<AssetCheckList,Long> {
    Optional<AssetCheckList> findByCompanyIdAndId(long cid, Long id);
    List<AssetCheckList> findByCompanyIdAndOnboardOrderId(long cid, long onboardOrderId);
    List<AssetCheckList> findByCompanyIdAndOnboardOrderIdAndAssetType(long cid, long onboardOrderId, String type);
    List<AssetCheckList> findByCompanyIdAndEmployeeId(long cid, long employeeId);
    List<AssetCheckList> findByCompanyIdAndOnboardOrderIdIn(Long cid, Set<Long> onboardOrderIds);
    @Query(value = "select c " +
            " from AssetCheckList c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.employeeId = :employeeId)" +
            " and (c.id = :id)"
    )

    Optional<AssetCheckList> findState(
            @Param("companyId") Long cid
            ,@Param("employeeId") Long employeeId
            ,@Param("id") Long id);
}

