package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTrainingItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardTrainingItemRepo extends BaseRepo<OnboardTrainingItem, Long> {
    Optional<OnboardTrainingItem> findByCompanyIdAndOnboardTrainingIdAndStatusAndItemIdAndItemChildIdAndItemGrandChildId (Long cid, Long onboardTrainingId, Integer status, Long itemId, Long childId, Long grandChildId);
    Optional<OnboardTrainingItem> findByCompanyIdAndId (Long cid, Long id);
    List<OnboardTrainingItem> findByCompanyIdAndOnboardTrainingIdIn (Long cid, Set<Long> onboardTrainingIds);
}
