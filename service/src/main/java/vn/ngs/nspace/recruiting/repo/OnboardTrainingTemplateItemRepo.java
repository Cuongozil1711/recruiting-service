package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplateItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface OnboardTrainingTemplateItemRepo extends BaseRepo<OnboardTrainingTemplateItem, Long> {
    List<OnboardTrainingTemplateItem>  findByCompanyIdAndTemplateIdInAndStatus(Long cid, Set<Long> templateId, Integer status);
    Optional<OnboardTrainingTemplateItem> findByCompanyIdAndId(Long cid, Long id);
}
