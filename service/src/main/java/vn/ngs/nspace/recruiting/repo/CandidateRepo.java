package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CandidateRepo extends BaseRepo<Candidate,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
    Optional<Candidate> findByCompanyIdAndPhoneAndStatus(long cid, String phone, int status);
    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.id = :id)"
    )
    Optional<Candidate> findById(
            @Param("companyId") Long cid
            ,@Param("id") Long id);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (lower(concat(coalesce(c.fullName,''),coalesce(c.code,''), coalesce(c.wardCode,''), coalesce(c.phone,''), coalesce(c.email,'') ))" +
            " like (concat('%',:search,'%')) or coalesce(:search, '#') = '#' )"+
            " order by c.id DESC "
    )
    // or coalesce(:search, '#') = '#'
    Page<Candidate> search(
            @Param("companyId") Long cid
            ,@Param("search") String search
//            ,@Param("fullname") String fullname
//            ,@Param("gender") Long gender
//            ,@Param("wardCode") String wardCode
//            ,@Param("phone") String phone
//            ,@Param("email") String email
            , Pageable pageable);
    @Query(value = "select count( case when state = 'INIT' then 0 end) as init ,count( case when state = 'STAFF' then 0 end) as staff ,count( case when state = 'DENIED' then 0 end) as denied ,count( case when state = 'RECRUITED' then 0 end) as RECRUITED,count( case when state = 'ARCHIVE' then 0 end) as ARCHIVE,count( case when state = 'INTERVIEWED' then 0 end) as INTERVIEWED,count( case when state = 'APPROVED' then 0 end) as APPROVED,count( case when state = 'APPOINTMENT' then 0 end) as APPOINTMENT,count( case when state = 'ONBOARD' then 0 end) as ONBOARD from recruiting_service.candidate where company_id = :companyId and status = 1",nativeQuery = true)
    Map<String,Object> countAllStates(@Param("companyId") Long companyId);
    @Query(value = "select c" +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.applyPositionId = :applyPosition or :applyPosition = -1 )" +
            " and (c.gender = :gender or :gender = -1)" +
            " and (c.language in :language or -1 in :language)" +
            " and (c.educationLevel in :educationLevel or -1 in :educationLevel)" +
            " and (lower(c.educateLocation) like (concat('%',:educateLocation,'%')) or :educateLocation = 'all')  " +
            " and (lower(c.industry) like (concat('%',:industry,'%')) or :industry = 'all') " +
            " and (lower(c.lastPosition) like (concat('%',:lastPosition,'%')) or :lastPosition = 'all') " +
            " and (cast(:ageLess AS java.time.LocalDateTime) is null  or (:ageLess < c.birthDate))" +
            " group by c.id having " +
            " ((case when (experience_unit = 'year') then (experience * 12) else experience" +
            "      end ) >= :fromExp" +
            " and (case when (experience_unit = 'year') then (experience * 12)  else experience " +
            "      end ) <= :toExp)"+
            " order by c.id DESC "
    )
    Page<Candidate> filter(
            @Param("companyId") Long cid
            , @Param("applyPosition") Long applyPosition
            , @Param("gender") Long gender
            , @Param("language") List<Long> language
            , @Param("educationLevel") List<Long> educationLevel
            , @Param("educateLocation") String educateLocation
            , @Param("industry") String industry
            , @Param("ageLess") Date ageLess
            , @Param("lastPosition") String lastPosition
            , @Param("fromExp") Double fromExp
            , @Param("toExp") Double toExp
            , Pageable pageable);

    @Query(value = " select apply_position_id, count(*) from recruiting_service.candidate where apply_position_id is not null group by apply_position_id", nativeQuery = true)
    List<Map<String, Object>> countPositionApply();
}

