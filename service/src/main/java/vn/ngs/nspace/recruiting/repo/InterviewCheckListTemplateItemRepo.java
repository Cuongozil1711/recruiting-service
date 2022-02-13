package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplateItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InterviewCheckListTemplateItemRepo extends BaseRepo<InterviewCheckListTemplateItem,Long> {

    Optional<InterviewCheckListTemplateItem> findByCompanyIdAndId(long cid, Long id);
    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateIdInAndStatus(long cid, Set<Long> templateIds, Integer status);
    List<InterviewCheckListTemplateItem> findByCompanyIdAndId(Long cid, Long ids);
    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateId(long cid, Long templateId);
}
