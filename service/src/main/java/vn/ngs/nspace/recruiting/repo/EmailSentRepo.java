package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.EmailSent;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmailSentRepo extends BaseRepo<EmailSent,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
    List<EmailSent> findByCompanyIdAndRefTypeAndRefId(long cid, String refType, String refId);
    List<EmailSent> findByCompanyIdAndToEmailAndRefTypeAndTypeOnboard(Long cid, String toEmail, String refType, String type);
    Optional<EmailSent> findByCompanyIdAndId(Long cid, Long id);

    @Query("select es from EmailSent es where lower(es.refId) like lower(concat('%',:refId,'%')) and es.status = 1 and es.companyId = :cid")
    List<EmailSent> getListEmailSent(@RequestParam("cid") Long cid, @RequestParam("refId") String refId);
}

