package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OnboardTrainingTemplateRepo  extends BaseRepo<OnboardTrainingTemplate, Long> {
    Optional<OnboardTrainingTemplate> findByCompanyIdAndId (Long cid, Long id);

    void deleteAllByIdIn(Set<Long> id);

    @Query(value = " select p " +
            " from OnboardTrainingTemplate p " +
            " where (p.companyId = :companyId)" +
            " and (p.positionId = :positionId or :positionId = -1) " +
            " and (p.titleId = :titleId or :titleId = -1) ")
    Page<OnboardTrainingTemplate> search(@Param("companyId") Long cid
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , Pageable pageable);

    @Query(value = " select p " +
            " from OnboardTrainingTemplate p " +
            " where (p.companyId = :companyId)" +
            " and (p.positionId = :positionId or coalesce(p.positionId, 0)  = 0) " +
            " and (p.titleId = :titleId or coalesce(p.titleId, 0)  = 0) " +
            " order by  coalesce(p.titleId, 0) desc " +
            "           , coalesce(p.positionId, 0) desc " )
    List<OnboardTrainingTemplate> searchConfigTemplate(@Param("companyId") Long cid
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId );
}
