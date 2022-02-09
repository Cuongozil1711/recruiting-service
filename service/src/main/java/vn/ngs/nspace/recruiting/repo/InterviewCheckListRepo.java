package vn.ngs.nspace.recruiting.repo;

import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;

import java.util.List;
import java.util.Optional;

public interface InterviewCheckListRepo extends BaseRepo<InterviewCheckList, Long> {

    Optional<InterviewCheckList> findByCompanyIdAndCheckListId(Long cid, Long checkListId);
    Optional<InterviewCheckList> findByCompanyIdAndCheckListIdAndInterviewerIdAndStatus(Long cid, Long checkListId, Long interviewerId, int status);

//    Optional<InterviewCheckList> findByCompanyIdAndCheckListIdAndInterviewerId(long cid, Long checkListId, Long interviewerId);

    List<InterviewCheckList> findByCompanyIdAndInterviewResultId(long cid, Long interviewResultId);

}