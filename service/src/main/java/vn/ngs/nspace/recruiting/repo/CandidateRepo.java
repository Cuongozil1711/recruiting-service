package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.Candidate;


import java.util.Optional;

public interface CandidateRepo extends BaseRepo<Candidate,Long> {

    Optional<Candidate> findByCompanyIdAndId(long cid, Long id);

    @Query(value = "select c " +
            " from Candidate c" +
            " where (c.companyId = :companyId)" +
            " and (c.fullName = :fullname or :fullname = 'all')" +
            " and (c.gender = :gender or :gender = -1)" +
            " and (c.wardCode = :wardCode or :wardCode = 'all')" +
            " and (c.phone = :phone or :phone = 'all')" +
            " and (c.email = :email or :email = 'all')" +
            " and (c.status = 1)" )
    Page<Candidate> search(
            @Param("companyId") Long cid
            ,@Param("fullname") String fullname
            ,@Param("gender") Long gender
            ,@Param("wardCode") String wardCode
            ,@Param("phone") String phone
            ,@Param("email") String email
            , Pageable pageable);
}

