package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InterviewCheckListRepo extends BaseRepo<InterviewCheckList, Long> {


    Optional<InterviewCheckList> findByCompanyIdAndCheckListIdAndInterviewerIdAndStatus(Long cid, Long checkListId, Long interviewerId, int status);

    Optional<InterviewCheckList> findByCompanyIdAndCheckListIdAndInterviewerId(long cid, Long checkListId, Long interviewerId);

    Optional<InterviewCheckList> findByCompanyIdAndId(Long cid, Long id);
    List<InterviewCheckList> findByCompanyIdAndInterviewResultId(Long cid, Long resultId);
    List<InterviewCheckList> findByCompanyIdAndInterviewResultIdInAndStatus(Long cid, Set<Long> resultId, Integer status);
}
