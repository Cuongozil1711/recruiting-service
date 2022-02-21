package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.share.dto.CandidateFilterDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CandidateFilterRepo extends BaseRepo<CandidateFilter,Long> {

    Optional<CandidateFilter> findByCompanyIdAndId(long cid, Long id);

    List<CandidateFilter> findByCompanyIdAndAndCreateByInAndStatus(Long cid, Set<String> lstCreateBy, Integer status);

    @Query(value = "select c "+
            " from CandidateFilter c "+
            " where (c.companyId = :companyId) " +
            " and (c.status = 1) " +
            " and (c.createBy in (:lstCreateBy)) "+
            " and (coalesce(c.name,'') like %:search% )" )
    Page<CandidateFilter> search(@Param("companyId") Long cid
            , @Param("lstCreateBy") List<String> lstCreateBy
            , @Param("search") String search
            , Pageable pageable);

    @Query(value = "select c "+
            " from CandidateFilter c "+
            " where (c.companyId = :companyId) " +
            " and (c.status = 1) " +
            " and (coalesce(c.name,'') like %:search% )" )
    Page<CandidateFilter> searchName(@Param("companyId") Long cid
            , @Param("search") String search
            , Pageable pageable);

    @Query(value = "select c "+
            " from CandidateFilter c "+
            " where (c.companyId = :companyId) " +
            " and (c.status = 1) " )
    Page<CandidateFilter> searchAll(@Param("companyId") Long cid
            , Pageable pageable);
}

