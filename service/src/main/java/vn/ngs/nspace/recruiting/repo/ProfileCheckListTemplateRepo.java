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
import java.util.Optional;

public interface ProfileCheckListTemplateRepo extends BaseRepo<ProfileCheckListTemplate,Long> {
    Optional<ProfileCheckListTemplate> findByCompanyIdAndId(long cid, Long id);

    @Query(value = " select p " +
            " from ProfileCheckListTemplate p " +
            " where (p.companyId = :companyId)" +
            " and (p.positionId = :positionId or :positionId = -1) " +
            " and (p.titleId = :titleId or :titleId = -1) " +
            " and (p.contractTypeId = :contractTypeId or :contractTypeId = -1) ")
    Page<ProfileCheckListTemplate> search(@Param("companyId") Long cid
            , @Param("positionId") Long positionId
            , @Param("titleId") Long titleId
            , @Param("contractTypeId") Long contractTypeId
            , Pageable pageable);
}

