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
            "and  (lower(j.title) like (concat('%',:title,'%')) or :title = 'all') " +
            " and (lower(j.code) like (concat('%',:code,'%')) or :code = 'all') " 
    )
    Page<JobRequirement> search(@Param("companyId") Long cid
            , @Param("title") String title
            , @Param("code") String code
            , Pageable pageable);

    Optional<JobRequirement> findByCompanyIdAndCodeAndStatus(Long cid, String code, Integer status);
}

