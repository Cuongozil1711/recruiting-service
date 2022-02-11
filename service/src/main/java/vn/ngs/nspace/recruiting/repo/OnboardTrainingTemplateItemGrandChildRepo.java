package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplateItemGrandChild;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardTrainingTemplateItemGrandChildRepo extends BaseRepo<OnboardTrainingTemplateItemGrandChild, Long> {
    List<OnboardTrainingTemplateItemGrandChild> findByCompanyIdAndTemplateIdInAndItemIdInAndItemChildrenIdInAndStatus(Long cid, Set<Long> templateId, Set<Long> itemId, Set<Long> itemChildren, Integer status);
    Optional<OnboardTrainingTemplateItemGrandChild> findByCompanyIdAndId(Long cid, Long id);
    List<OnboardTrainingTemplateItemGrandChild> findByCompanyIdAndTemplateIdAndItemIdAndItemChildrenId(Long cid, Long templateId, Long itemId, Long itemChildId);
    List<OnboardTrainingTemplateItemGrandChild> findByCompanyIdAndTemplateIdAndItemIdInAndItemChildrenIdIn(Long cid, Long templateId, Set<Long> itemId, Set<Long> itemChildren);
}
