package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.EvaluatorOnboardTranning;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EvaluatorOnboardTranningRepo  extends BaseRepo<EvaluatorOnboardTranning, Long> {
    Optional<EvaluatorOnboardTranning> findByCompanyIdAndOnboardOrderIdAndOnboardTraningIdAndStatus(Long cid, Long onboardOrderId, Long onboardTraningId, Integer status);
    Optional<EvaluatorOnboardTranning> findByCompanyIdAndId(Long cid, Long id);
    List<EvaluatorOnboardTranning> findByCompanyIdAndOnboardTraningIdIn(Long cid, Set<Long> onboardTraningIds);
}
