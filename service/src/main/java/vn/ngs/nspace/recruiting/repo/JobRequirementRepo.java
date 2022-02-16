package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.JobRequirement;

import java.util.List;
import java.util.Optional;

public interface JobRequirementRepo extends BaseRepo<JobRequirement,Long> {
    Optional<JobRequirement> findByCompanyIdAndId(Long cid, Long id);

    @Query(value = "select j " +
            " from JobRequirement j " +
            " where (j.companyId = :companyId)" +
            " and (j.code = :code or :code = 'all')" +
            " and (j.title = :title or :title = 'all')" +
            " and (j.positionId = :positionId or :positionId = -1)" +
            " and (j.status = 1)" +
            "order by j.code")
    Page<JobRequirement> search(@Param("companyId") Long cid
            , @Param("title") String title
            , @Param("code") String code
            , @Param("positionId") Long positionId
            , Pageable pageable);

}

