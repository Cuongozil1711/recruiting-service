package vn.ngs.nspace.recruiting.repo;


import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTraining;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;

import java.util.List;
import java.util.Optional;


public interface OnboardTrainingRepo extends BaseRepo<OnboardTraining, Long> {
    Optional<OnboardTraining> findByCompanyIdAndOnboardOrderId(Long cid, Long onboarOrderId);
    Optional<OnboardTraining> findByCompanyIdAndId (Long cid, Long id);
    Optional<OnboardTraining> findById(Long aLong);
}
