package vn.ngs.nspace.recruiting.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.ngs.nspace.lib.repo.BaseRepo;
import vn.ngs.nspace.recruiting.model.RecruitmentNews;

import java.util.*;
@Repository
public interface RecruitmentNewsRepo extends BaseRepo<RecruitmentNews, Long> {
    Optional<RecruitmentNews> findAllByCompanyIdAndStatusAndId(Long companyId, Integer status, Long id);

    Optional<RecruitmentNews> findByCompanyIdAndIdAndStatus(Long companyId, Long id, Integer status);

    @Query(value = " select rn.id as news_id, " +
            "               rn.code as news_Code," +
            "               rn.name as news_Name," +
            "               rr.position_id as position_Id," +
            "               rr.level_id as level_Id," +
            "               rr.title_id as title_Id," +
            "               rn.state as state," +
            "               rr.quantity as quantity," +
            "               rn.deadline_sendcv as deadlineSend_CV " +
            " from recruiting_service.recruitment_news rn " +
            "     join recruiting_service.recruitment_request rr " +
            "         on rr.id = rn.request_id " +
            "                and rr.company_id = rn.company_id " +
            "                and rr.status = 1 " +
            "     join recruiting_service.recruitment_plan rp " +
            "         on rp.id = rn.plan_id " +
            "                and rp.company_id = rn.company_id " +
            "                and rp.status = 1 " +
            " where rn.company_id = :cid and rn.status = 1 " +
            "   and (lower(concat(coalesce(rn.code, ''), '', coalesce(rr.code, ''), '', coalesce(rp.code, ''))) like :search) " +
            "   and (rn.state in :states or '' in :states) " +
            "   and (rr.position_id = :positionId or '-1' = :positionId) " +
            "   and (rr.title_id = :titleId or '-1' = :titleId) " +
            "   and (rr.level_id = :levelId or '-1' = :levelId) " +
            "   and (rr.quantity >= :fromQuantity or '-1' = :fromQuantity) " +
            "   and (rr.quantity <= :toQuantity or '-1' = :toQuantity) " +
            "   and (rr.start_date > :startDate) " +
            "   and (rr.end_date < :endDate) ", nativeQuery = true)
    Page<Map<String, Object>> searchRecruitmentNews(@Param(value = "cid") long cid,
                                                    @Param(value = "search") String search,
                                                    @Param(value = "states") List<String> states,
                                                    @Param(value = "positionId") Long positionId,
                                                    @Param(value = "titleId") Long titleId,
                                                    @Param(value = "levelId") Long levelId,
                                                    @Param(value = "fromQuantity") Long fromQuantity,
                                                    @Param(value = "toQuantity") Long toQuantity,
                                                    @Param(value = "startDate") Date startDate,
                                                    @Param(value = "endDate") Date endDate,
                                                    Pageable pageable);

}
