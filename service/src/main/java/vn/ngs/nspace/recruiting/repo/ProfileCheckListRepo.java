package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;

import java.util.Optional;

public interface ProfileCheckListRepo extends BaseRepo<ProfileCheckList, Long> {
    Optional<ProfileCheckList> findByCompanyIdAndChecklistIdAndEmployeeIdAndStatus(Long cid, Long checkListId, Long employeeId, Integer status);
}