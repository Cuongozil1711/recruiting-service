package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentRequest;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface RecruitmentRequestRepo extends BaseRepo<RecruitmentRequest, Long> {
    Optional<RecruitmentRequest> findByCompanyIdAndIdAndStatus(Long companyId, Long id, Integer status);

    @Query(value = "select * from recruiting_service.Recruitment_Request r where r.company_Id =:cid and r.status =1 " +
            " and (r.org_Id in (:orgIds) or '-1' in (:orgIds) ) " +
            " and (r.position_Id in  (:positionIds) or '-1' in (:positionIds) ) " +
            " and ( r.create_By in (:createByUIds) or '' in (:createByUIds) ) " +
            " and ( r.state in (:statuses) or ( '' in (:statuses)) )" +
            " and ( lower( r.code ) like :search or r.quantity = :quantity )" +
            " and ( cast(:type as text ) is null or r.type = cast(:type as text )) "+
            " and r.start_Date > :fromDate order by r.id desc", nativeQuery = true)
    Page<RecruitmentRequest> filterAllByPage(@Param("cid") long cid, @Param("orgIds") List<Long> orgIds, @Param("positionIds") List<Long> positionIds,
                                             @Param("createByUIds") List<String> createByUIds, @Param("statuses")   List<String> statuses,
                                             @Param("search")  String search, @Param("quantity") Integer quantity,
                                             @Param("type")  String type, @Param("fromDate") Date fromDate,
                                            Pageable page);

    @Query("select re from RecruitmentRequest re where  re.companyId = :cid and re.status = :status and re.id in :ids")
    List<RecruitmentRequest> getIdIn(Long cid, List<Long> ids, Integer status);

    @Query("select re from RecruitmentRequest re" +
            " inner join RecruitmentPlanRequest rpl on rpl.recruitmentRequestId = re.id" +
            " inner join RecruitmentPlan rp on rp.id = rpl.recruitmentPlanId" +
            " where rpl.status = 1 and rpl.companyId = :cid and rp.id = :planId and rpl.status = 1 and re.status = 1")
    List<RecruitmentRequest> getALlByPlanId(Long cid, Long planId);

    @Query("select rr from RecruitmentRequest rr inner join RecruitmentPlanRequest  rpr on rpr.recruitmentRequestId = rr.id where rr.status =1 and rr.companyId = :cid and rr.state in :state")
    List<RecruitmentRequest> getAllByStateAndNews(Long cid, List<String> state);

    List<RecruitmentRequest> findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndStateAndStatus(Long orgId, Long levelId, Long titleId, Long positionId, String sate, Integer status);
}
