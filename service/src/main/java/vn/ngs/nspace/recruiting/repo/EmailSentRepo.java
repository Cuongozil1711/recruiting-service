package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.EmailSent;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmailSentRepo extends BaseRepo<EmailSent,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
    List<EmailSent> findByCompanyIdAndRefTypeAndRefId(long cid, String refType, String refId);
    List<EmailSent> findByCompanyIdAndToEmail(Long cid, String toEmail);
    Optional<EmailSent> findByCompanyIdAndId(Long cid, Long id);
}

