package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.Date;

public interface DemarcationRepo extends BaseRepo<Demarcation, Long> {

    Demarcation findByCompanyIdAndId(Long cId, Long id);

    @Query(value = " select p " +
            " from Demarcation p " +
            " where " +
            "(p.orgId = :orgId or coalesce(:orgId, -1) = -1)" +
            " and (p.levelId = :levelId or coalesce(:levelId, -1) = -1)" +
            " and (p.positionId = :positionId or coalesce(:positionId, -1) = -1) " +
            " and (p.titleId = :titleId or coalesce(:titleId, -1) = -1)" +
//            " and (p.demarcationDate between :dateFrom and :dateTo )" +
            " and  p.status = 1")
    Page<Demarcation> search(
            @Param("orgId") Long orgId
            , @Param("levelId") Long levelId
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId,
//            , @Param("dateFrom") Date dateFrom
//            , @Param("dateTo") Date dateTo
             Pageable pageable);
}
