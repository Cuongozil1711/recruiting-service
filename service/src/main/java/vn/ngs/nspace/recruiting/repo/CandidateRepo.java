package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CandidateRepo extends BaseRepo<Candidate, Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);
    List<Candidate> findByCompanyIdAndIdAndStatus(long cid, Long id,int status);
    Optional<Candidate> findByCompanyIdAndPhoneAndStatus(long cid, String phone, int status);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.id = :id)"
    )
    Optional<Candidate> findById(
            @Param("companyId") Long cid
            , @Param("id") Long id);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.id in :id)"
    )
    List<Candidate> findByArrayId(
            @Param("companyId") Long cid
            , @Param("id") List<Long> id);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (lower(concat(coalesce(c.fullName,''),coalesce(c.code,''), coalesce(c.wardCode,''), coalesce(c.phone,''), coalesce(c.email,'') ))" +
            " like (concat('%',:search,'%')) or coalesce(:search, '#') = '#' )" +
            " order by c.id DESC "
    )
        // or coalesce(:search, '#') = '#'
    Page<Candidate> search(
            @Param("companyId") Long cid
            , @Param("search") String search
//            ,@Param("fullname") String fullname
//            ,@Param("gender") Long gender
//            ,@Param("wardCode") String wardCode
//            ,@Param("phone") String phone
//            ,@Param("email") String email
            , Pageable pageable);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.applyPositionId = :applyPosition or :applyPosition = -1)" +
            " and (c.cvSourceId = :resource or :resource = -1)" +
            " and (c.applyDate between :applyDateFrom and :applyDateTo)" +
            " and (c.graduationYear >= :graduationFrom and c.graduationYear <= :graduationTo or c.graduationYear is null )" +
            " and (c.gender = :gender or :gender = -1)" +
            " and (cast(:ageLess AS java.time.LocalDateTime) is null  or (:ageLess < c.birthDate))" +
            " and (c.experience = :experience or coalesce(:experience,'#') ='#')" +
            " and (c.educationLevel in :educationLevel or -1L in (:educationLevel))" +
            " and (c.language in :language or -1L in (:language))" +
            " and (c.state in :states or '#' in (:states))" +
            " and ((concat(lower(coalesce(c.fullName,'')),lower(coalesce(c.code,'')),lower(coalesce(c.wardCode,'')), lower(coalesce(c.phone,'')), lower(coalesce(c.email,'') )))" +
            " like lower(concat('%',:search,'%')) or coalesce(:search, '#') = '#' )" +
            " order by c.id DESC "

    )
        // or coalesce(:search, '#') = '#'
    Page<Candidate> fillterStates(
            @Param("companyId") Long cid
            , @Param("search") String search
            , @Param("states") List<String> states
            , @Param("educationLevel") List<Long> educationLevel
            , @Param("language") List<Long> language
            , @Param("applyDateFrom") Date applyDateFrom
            , @Param("applyDateTo") Date applyDateTo
            , @Param("graduationFrom") Integer graduationFrom
            , @Param("graduationTo") Integer graduationTo
            , @Param("gender") Long gender
            , @Param("applyPosition") Long applyPosition
            , @Param("resource") Long resource
            , @Param("experience") String experience
            , @Param("ageLess") Date ageLess
            , Pageable pageable);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.state in :states or '#' in (:states))" +
            " order by c.id DESC "
    )
    Page<Candidate> filterCandidate(
            @Param("companyId") Long cid
            , @Param("states") List<String> states
            , Pageable pageable);


    @Query(value = "select count( case when state = 'INIT' then 0 end) as init ,count( case when state = 'STAFF' then 0 end) as staff ,count( case when state = 'DENIED' then 0 end) as denied ,count( case when state = 'RECRUITED' then 0 end) as RECRUITED,count( case when state = 'ARCHIVE' then 0 end) as ARCHIVE,count( case when state = 'INTERVIEWED' then 0 end) as INTERVIEWED,count( case when state = 'APPROVED' then 0 end) as APPROVED,count( case when state = 'APPOINTMENT' then 0 end) as APPOINTMENT,count( case when state = 'ONBOARD' then 0 end) as ONBOARD from recruiting_service.candidate where company_id = :companyId and status = 1", nativeQuery = true)
    Map<String, Object> countAllStates(@Param("companyId") Long companyId);

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
            "      end ) <= :toExp)" +
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

    @Query(value = "select cd" +
            " from Candidate as cd" +
            " inner join JobApplication as ja on ja.candidateId = cd.id" +
            " inner join OnboardOrder as o on o.jobApplicationId = ja.id" +
            " where cd.state = :stateCandidate" +
            " and (concat(coalesce(cd.fullName,''), coalesce(cd.fullName,'')) like lower(concat('%',:name,'%')) or coalesce(:name, '#') = '#')" +
            " and cd.gender = :gender or cd.gender = -1" +
            " and cd.orgRecrutingId = :orgRecruitingId or cd.orgRecrutingId = -1" +
            " and cd.code = :code or coalesce(:code, '#') = '#'"+
            " and o.state = :state or coalesce(:state, '#') = '#'"
    )
    Page<Candidate> filterCandidateOnboard(@Param("code") String code
            , @Param("name") String name
            , @Param("gender") Long gender
            , @Param("orgRecruitingId") Long orgRecruitingId
            , @Param("state") String state
            , @Param("stateCandidate") String stateCandidate
            , Pageable pageable
    );
    
    @Query(value = "select c " +
            " from JobApplication c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.state = :state)" +
            " and (c.id = :id)"
    )
    Optional<JobApplication> findState(
            @Param("companyId") Long cid
            ,@Param("id") Long id
            ,@Param("state") String state);
}

