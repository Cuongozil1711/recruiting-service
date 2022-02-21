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
            " and (j.status = 1)" +
            "and (lower(concat(coalesce(j.title,''), coalesce(j.code,'')))" +
            " like %:search%)"
    )
    Page<JobRequirement> search(@Param("companyId") Long cid
            , @Param("search") String search
            , Pageable pageable);

    Optional<JobRequirement> findByCompanyIdAndCodeAndStatus(Long cid, String code, Integer status);
}

