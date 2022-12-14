package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.ScheduleType;

import java.util.Optional;

@Repository
public interface ScheduleTypeRepo extends BaseRepo<ScheduleType, Long> {

    @Query("select s from ScheduleType as s" +
            " where lower(concat(s.name,s.code)) like lower(concat('%',coalesce(:search,''),'%'))" +
            " and s.status = 1"
    )
    Page<ScheduleType> getPageScheduleType(
            @Param("search") String search
            , Pageable pageable
    );

    Optional<ScheduleType> findByCompanyIdAndId(Long cid, Long id);
}
