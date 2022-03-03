package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.UserSetting;

import java.util.List;
import java.util.Optional;

public interface UserSettingRepo extends BaseRepo<UserSetting,Long> {
    Optional<UserSetting> findByCompanyIdAndUserId(long companyId, String userId);
}

