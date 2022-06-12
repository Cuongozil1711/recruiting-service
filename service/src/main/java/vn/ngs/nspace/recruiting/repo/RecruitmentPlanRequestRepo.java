package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;

import javax.transaction.Transactional;
import java.util.*;

public interface RecruitmentPlanRequestRepo extends BaseRepo<RecruitmentPlanRequest, Long> {

    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentRequestIdAndStatus(Long companyId, Long recruitmentRequestId, Integer status);
    List<RecruitmentPlanRequest> findByCompanyIdAndRecruitmentPlanIdAndStatus(Long cid, Long planId, Integer status);

    @Query("select rpr from RecruitmentPlanRequest rpr" +
            " inner join RecruitmentRequest rr on rr.id = rpr.recruitmentRequestId" +
            " where rpr.status = 1 and rpr.companyId = :cid" +
            " and rpr.recruitmentPlanId = :planId" +
            " and (rr.positionId = :posittionId or :positionId = 1L)" +
            " and (rr.groupId = :groupId or :groupId = 1L)" +
            " and (rr.levelId = :levelId or :levelId = 1L)" +
            " and (rr.orgId = :orgId or :orgId = 1L)" +
            " and (lower(rr.code) like concat('%',:search,'%') or :search = '')" +
            " and (rpr.deadline between :deadlineFrom and :deadlineTo)" +
            " and (rr.type = :type or :type = '')" +
            " and (rr.typeRequest = :typeRequest or :typeRequest = '')" +
            " and (rpr.picId = :picId or :picId = 1L)" +
            " and (rr.state in :state or '' in :state)" +
            " and rr.status = 1")
    List<RecruitmentPlanRequest> getByPlanIdAndFilter (Long cid, Long planId, String type, String typeRequest, String search, List<String> state, Long picId, Long positionId, Long orgId, Long levelId, Date deadlineFrom, Date deadlineTo);

    @Query("select rec from RecruitmentPlanRequest rec where rec.status = 1 and rec.companyId =:cid and rec.recruitmentPlanId = :planId")
    List<RecruitmentPlanRequest> getByPlanId(Long cid, Long planId);

    @Transactional
    @Modifying
    @Query("delete from RecruitmentPlanRequest rpr where rpr.recruitmentPlanId = :planId and rpr.companyId = :cid")
    void deleteByPlanId(Long cid, Long planId);
}
