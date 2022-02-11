package vn.ngs.nspace.recruiting.repo;


import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTraining;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;

import java.util.List;
import java.util.Optional;


public interface OnboardTrainingRepo extends BaseRepo<OnboardTraining, Long> {
    List<ProfileCheckList> findByCompanyIdAndOnboardOrderId(Long cid, Long onboarOrderId);
    Optional<OnboardTraining> findByCompanyIdAndEmployeeIdAndStatusAndItemIdAndItemChildIdAndItemGrandChildId (Long cid, Long employeeId, Integer status, Long itemId, Long childId, Long grandChildId);
}
