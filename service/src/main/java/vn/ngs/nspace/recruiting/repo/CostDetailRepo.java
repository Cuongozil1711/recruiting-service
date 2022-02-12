package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.CostDetail;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CostDetailRepo extends BaseRepo<CostDetail,Long> {

    Optional<CostDetail> findByCompanyIdAndId(long cid, Long id);
    List<CostDetail> findByCompanyIdAndCostIdAndStatus(long cid, Long costId, int status);
    List<CostDetail> findByCompanyIdAndCostIdInAndStatus(long cid, Set<Long> costId, int status);
}

