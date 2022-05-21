package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InterviewCheckListTemplateItemRepo extends BaseRepo<InterviewCheckListTemplateItem, Long> {

    Optional<InterviewCheckListTemplateItem> findByCompanyIdAndId(long cid, Long id);

    List<InterviewCheckListTemplateItem> findByCompanyIdAndId(Long cid, Long id);

    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateIdInAndStatus(long cid, Set<Long> tempIds, Integer status);

    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateId(long cid, Long templateId);

    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateIdAndStatus(Long cid, Long templateId, Integer status);

    // new
    @Query("select i from InterviewCheckListTemplateItem i where i.companyId = :companyId and i.templateId = :templateId and i.status =1")
    List<InterviewCheckListTemplateItem> findByTemplateId(@Param("companyId") Long cid, @Param("templateId") Long templateId);

    @Query(value = "select i from InterviewCheckListTemplateItem i where i.companyId = :companyId and i.status = 1 and i.checkListId = :templateId")
    List<InterviewCheckListTemplateItem> findByCompanyIdAndTemplateId(Long companyId, Long templateId);
}
