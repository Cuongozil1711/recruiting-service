package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.EmailSent;

import java.util.Date;
import java.util.Optional;

public interface EmailSentRepo extends BaseRepo<EmailSent,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
}

