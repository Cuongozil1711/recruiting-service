package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DemarcationRepo extends BaseRepo<Demarcation, Long> {

    List<Demarcation> findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndStatus(Long orgId, Long levelId, Long titleId, Long positionId, Integer status);
    Demarcation findByCompanyIdAndId(Long cId, Long id);

    @Query(value = "SELECT p.org_id as orgId, p.level_id as levelId, p.position_id as postionId, p.title_id as titleID, sum(p.sum_demarcation) as sumDemarcation FROM recruiting_service.demarcation as p\n" +
            "where p.status = 1 " +
            "and (p.org_id = :orgId or coalesce(:orgId, 0) = 0)  " +
            "and (p.level_id = :levelId or coalesce(:levelId, 0) = 0) " +
            "and (p.position_id = :positionId or coalesce(:positionId, 0) = 0) " +
            "and (p.title_id = :titleId or coalesce(:titleId, 0) = 0) " +
            "and (extract(year from p.demarcation_date) = :dateDemarcation or coalesce(:dateDemarcation, 0) = 0) " +
            "group by p.org_id, p.level_id, p.position_id, p.title_id ", nativeQuery = true)
    List<Map<String, Object>> search(
              @Param("orgId") Long orgId
             ,@Param("levelId") Long levelId
             ,@Param("positionId") Long positionId
             ,@Param("titleId") Long titleId
             ,@Param("dateDemarcation") Integer dateDemarcation
             ,Pageable pageable);

}
