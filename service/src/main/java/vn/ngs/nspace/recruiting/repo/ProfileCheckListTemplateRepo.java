package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ProfileCheckListTemplateRepo extends BaseRepo<ProfileCheckListTemplate,Long> {
    Optional<ProfileCheckListTemplate> findByCompanyIdAndId(long cid, Long id);
    List<ProfileCheckListTemplate> findByCompanyIdAndStatus(Long cid, Integer status);
    List<ProfileCheckListTemplate> findByCompanyIdAndPositionIdAndTitleIdAndStatus(Long cid, Long positionId, Long titleId, Integer status);
    @Query(value = " select p " +
            " from ProfileCheckListTemplate p " +
            " where (p.companyId = :companyId)" +
            " and (p.positionId = :positionId or :positionId = -1) " +
            " and (p.titleId = :titleId or :titleId = -1)" +
            " and (p.status = 1)")
    Page<ProfileCheckListTemplate> search(@Param("companyId") Long cid
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , Pageable pageable);

    @Query(value = " select p " +
            " from ProfileCheckListTemplate p " +
            " where (p.companyId = :companyId)" +
            " and (p.status = 1 ) " +
            " and (p.positionId = :positionId or coalesce(p.positionId, 0)  = 0) " +
            " and (p.titleId = :titleId or coalesce(p.titleId, 0)  = 0) " +
            " and (p.contractType = :contractType or coalesce(p.contractType, '#')  = '#')" +
            " order by  coalesce(p.titleId, 0) desc " +
            "           , coalesce(p.positionId, 0) desc " +
            "           , case when coalesce(p.contractType, '#') = '#' then 0 else 1 end desc ")
    List<ProfileCheckListTemplate> searchConfigTemplate(@Param("companyId") Long cid
            ,@Param("positionId") Long positionId
            ,@Param("titleId") Long titleId
            ,@Param("contractType") String contractType);

    @Query(value = "select c" +
            " from ProfileCheckListTemplate c" +
            " where (c.companyId = :companyId)" +
            " and (c.positionId = :positionId)"+
            " and (c.titleId = :titleId)"+
            " and (c.contractType = :contractType)" +
            " and (c.status = 1)")
    List<ProfileCheckListTemplate> findProfileCheckListTemplate(
            @Param("companyId") Long companyId
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , @Param("contractType") String contractType
    );
}

