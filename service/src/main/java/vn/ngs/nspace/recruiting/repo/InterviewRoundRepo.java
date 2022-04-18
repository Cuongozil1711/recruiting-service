package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.InterviewRound;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InterviewRoundRepo extends BaseRepo<InterviewRound,Long> {
    Optional<InterviewRound> findByCompanyIdAndId(long cid, Long id);
    Optional<InterviewRound> findByCompanyIdAndCode(long cid, String code);
    List<InterviewRound> findByCompanyId(long cid);
    @Query(value = "select c.id as id , c.code as code, c.name as name" +
            " from InterviewRound c" +
            " where (c.companyId = :companyId)" +
            " and (concat(coalesce(c.code,''), coalesce(c.name,'')) like lower(concat('%',:search,'%')) or coalesce(:search, '#') = '#') " )
    Page<Map<String, Object>> search(
            @Param("companyId") Long cid
            ,@Param("search") String search
            , Pageable pageable);
}

