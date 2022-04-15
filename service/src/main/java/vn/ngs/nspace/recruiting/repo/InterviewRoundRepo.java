package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.EmailSetting;

import java.util.List;
import java.util.Optional;

public interface InterviewRoundRepo extends BaseRepo<EmailSetting,Long> {
    Optional<EmailSetting> findByCompanyIdAndId(long cid, Long id);
    Optional<EmailSetting> findByCompanyIdAndCode(long cid, String code);
    List<EmailSetting> findByCompanyId(long cid);
}

