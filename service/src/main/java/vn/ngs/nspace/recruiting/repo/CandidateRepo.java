package vn.ngs.nspace.recruiting.repo;

import org.camunda.feel.syntaxtree.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;


import java.util.Date;
import java.util.Optional;

public interface CandidateRepo extends BaseRepo<Candidate,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (concat(coalesce(c.fullName,''), coalesce(c.wardCode,''), coalesce(c.phone,'')" +
            ", coalesce(c.email,'') ) like %:search%)" )
    Page<Candidate> search(
            @Param("companyId") Long cid
            ,@Param("search") String search
//            ,@Param("fullname") String fullname
//            ,@Param("gender") Long gender
//            ,@Param("wardCode") String wardCode
//            ,@Param("phone") String phone
//            ,@Param("email") String email
            , Pageable pageable);

    @Query(value = "select c" +
            ""+
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.status = 1)" +
            " and (c.applyPositionId = :applyPosition or :applyPosition = -1 )" +
            " and (c.gender = :gender or :gender = -1)" +
            " and (c.language = :language or :language = 'all')" +
            " and (c.educationLevel = :educationLevel or :educationLevel = -1)" +
            " and (:educateLocation = 'all' or lower(educateLocation) like '%:educateLocation%') " +
            " and (:industry = 'all' or lower(industry) like '%:industry%') " +
            " and (:lastPosition = 'all' or lower(educateLocation) = '%:lastPosition%') " +
            " and (cast(:ageLess AS java.time.LocalDateTime) is null  or :ageLess < c.birthDate)" +
            " group by c.id having " +
            " ((case when (experience_unit = 'year') then (experience * 12) else experience" +
            "      end ) >= :fromExp" +
            " and (case when (experience_unit = 'year') then (experience * 12)  else experience " +
            "      end ) <= :toExp)" )
    Page<Candidate> filter(
            @Param("companyId") Long cid
            ,@Param("applyPosition") Long applyPosition
            ,@Param("gender") Long gender
            ,@Param("language") String language
            ,@Param("educationLevel") Long educationLevel
            ,@Param("educateLocation") String educateLocation
            ,@Param("industry") String industry
            ,@Param("ageLess") Date ageLess
            ,@Param("lastPosition") String lastPosition
            ,@Param("fromExp") Double fromExp
            ,@Param("toExp") Double toExp
            , Pageable pageable);
}

