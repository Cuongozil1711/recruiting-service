package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.model.Reason;

import java.util.List;
import java.util.Optional;

public interface ReasonRepo extends BaseRepo<Reason,Long> {
    Optional<Reason> findByCompanyIdAndId(long cid, Long id);
    Optional<Reason> findByCompanyIdAndCode(long cid, String code);
    List<Reason> findByCompanyIdAndType(long cid, String type);
    Optional<Reason> findByCompanyIdAndTypeAndCodeAndStatus(long cid, String type, String code, Integer status);
}

