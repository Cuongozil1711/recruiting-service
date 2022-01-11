package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.Optional;

public interface ProfileCheckListTemplateRepo extends BaseRepo<ProfileCheckListTemplate,Long> {
    Optional<ProfileCheckListTemplate> findByCompanyIdAndId(long cid, Long id);
}

