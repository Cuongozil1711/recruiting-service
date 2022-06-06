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
            " and ( r.create_By in (:createByUIds) or '-1' in (:createByUIds) ) " +
            " and ( r.status in (:statuses) or ( '-1' in (:statuses) and r.status!=0) )" +
            " and ( lower( r.code ) like :search or r.quantity = :quantity )" +
            " and ( cast(:type as text ) is null or r.type = cast(:type as text )) "+
            " and r.start_Date > :fromDate and r.end_Date <:toDate", nativeQuery = true)
    Page<RecruitmentRequest> filterAllByPage(@Param("cid") long cid, @Param("orgIds") List<Long> orgIds, @Param("positionIds") List<Long> positionIds,
                                             @Param("createByUIds") List<String> createByUIds, @Param("statuses")   List<Integer> statuses,
                                             @Param("search")  String search, @Param("quantity") Integer quantity,
                                             @Param("type")  String type, @Param("fromDate") Date fromDate,
                                             @Param("toDate") Date toDate, Pageable page);
}
