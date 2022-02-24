package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.model.Reason;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ReasonRepo extends BaseRepo<Reason,Long> {
    Optional<Reason> findByCompanyIdAndId(long cid, Long id);
    Optional<Reason> findByCompanyIdAndCode(long cid, String code);
    List<Reason> findByCompanyIdAndType(long cid, String type);
    Optional<Reason> findByCompanyIdAndTypeAndCodeAndStatus(long cid, String type, String code, Integer status);
    @Query(value = "select c.id as id, c.code as code, c.title as title, c.status as status, c.type as type" +
            " from Reason c" +
            " where (c.companyId = :companyId)" +
            " and (c.type = :type)" +
            " and (lower(concat(coalesce(c.code,''), coalesce(c.title,''))) like (concat('%',:search,'%')) or coalesce(:search, '#') = '#') " )
    Page<Map<String, Object>> search(
            @Param("companyId") Long cid
            ,@Param("type") String type
            ,@Param("search") String search
            , Pageable pageable);
}

