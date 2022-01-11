package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplateItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProfileCheckListTemplateItemRepo extends BaseRepo<ProfileCheckListTemplateItem,Long> {
    Optional<ProfileCheckListTemplateItem> findByCompanyIdAndId(long cid, Long id);
    List<ProfileCheckListTemplateItem> findByCompanyIdAndTemplateIdInAndStatus(long cid, Set<Long> templateIds, Integer status);
}

