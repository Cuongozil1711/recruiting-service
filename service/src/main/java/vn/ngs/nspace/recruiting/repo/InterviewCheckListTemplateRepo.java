package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface InterviewCheckListTemplateRepo extends BaseRepo<InterviewCheckListTemplate,Long> {
    Optional<InterviewCheckListTemplate> findByCompanyIdAndId(long cid, Long id) ;


    @Query(value = " select i " +
            " from InterviewCheckListTemplate i " +
            " where (i.companyId = :companyId)" +
            " and (i.positionId = :positionId or :positionId = -1) " +
            " and (i.orgId = :orgId or :orgId = -1)" +
            " and (i.status = 1) ")
    Page<InterviewCheckListTemplate> search(@Param("companyId") long cid
            ,@Param("positionId") Long positionId
            ,@Param("orgId") Long orgId
            , Pageable pageable);


    @Query(value = " select i" +
            " from InterviewCheckListTemplate i " +
            " where (i.companyId = :companyId)" +
            " and (i.positionId = :positionId or coalesce(i.positionId, 0)  = 0) " +
            " and (i.orgId = :orgId or coalesce(i.orgId, 0)  = 0) " +
            " order by  coalesce(i.orgId, 0) desc " +
            "  , coalesce(i.positionId, 0) desc " +
            " ")
    List<InterviewCheckListTemplate> searchConfigTemplate(
            @Param("companyId") long cid
            ,@Param("positionId") Long positionId
            ,@Param("orgId") Long orgId

           );
}
