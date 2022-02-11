package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplateItemChildren;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardTrainingTemplateItemChildrenRepo extends BaseRepo<OnboardTrainingTemplateItemChildren, Long> {
   List<OnboardTrainingTemplateItemChildren> findByCompanyIdAndTemplateIdInAndItemIdInAndStatus (Long cid, Set<Long> templateId, Set<Long> itemId, Integer status);
   Optional<OnboardTrainingTemplateItemChildren> findByCompanyIdAndId(Long cid, Long id);
   List<OnboardTrainingTemplateItemChildren> findByCompanyIdAndTemplateIdAndItemId (Long cid, Long templateId, Long itemId);
   List<OnboardTrainingTemplateItemChildren> findByCompanyIdAndTemplateIdAndItemIdIn (Long cid, Long templateId, Set<Long> itemId);
}
