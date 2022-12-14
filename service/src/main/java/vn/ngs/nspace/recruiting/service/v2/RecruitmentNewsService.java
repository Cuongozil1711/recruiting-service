package vn.ngs.nspace.recruiting.service.v2;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.RecruitmentNewsRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRequestRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentRequestRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentNewsDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.dto.utils.DateUtils;
import vn.ngs.nspace.recruiting.share.request.RecruitmentNewsFilterRequest;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentNewsService {

    private final RecruitmentNewsRepo recruitmentNewsRepo;
    private final RecruitmentRequestRepo recruitmentRequestRepo;
    private final RecruitmentPlanRepo recruitmentPlanRepo;
    private final RecruitmentPlanRequestRepo planRequestRepo;
    private final ExecuteConfigService configService;
    private final ExecuteHcmService hcmService;

    public Page<RecruitmentNewsDTO> searchRecruitmentNews(long cid, String uid, RecruitmentNewsFilterRequest request, Pageable page) {

        if (Constants.GET_ALL.equals(request.getGetAll())){
            page = PageRequest.of(0,Integer.MAX_VALUE,page.getSort());
        }

        Long positionId;
        Long titleId;
        Long levelId;
        String search;
        Long fromQuantity;
        Long toQuantity;
        List<String> states = new ArrayList<>();

        if (StringUtils.isEmpty(request.getSearch())) {
            search = "%%";
        } else {
            search = "%" + request.getSearch().toLowerCase() + "%";
        }

        if (request.getPositionId() == null) {
            positionId = -1L;
        } else {
            positionId = request.getPositionId();
        }

        if (request.getTitleId() == null) {
            titleId = -1L;
        } else {
            titleId = request.getTitleId();
        }

        if (request.getLevelId() == null) {
            levelId = -1L;
        } else {
            levelId = request.getLevelId();
        }

        if (request.getFromQuantity() == null) {
            fromQuantity = -1L;
        } else {
            fromQuantity = request.getFromQuantity();
        }

        if (request.getToQuantity() == null) {
            toQuantity = -1L;
        } else {
            toQuantity = request.getToQuantity();
        }

        if (CollectionUtils.isEmpty(request.getStates())) {
            states.add("");
        } else {
            states.addAll(request.getStates());
        }

        Page<Map<String, Object>> recruitmentNews = recruitmentNewsRepo.searchRecruitmentNews(cid, search, states, positionId, titleId, levelId, fromQuantity, toQuantity, request.getFromDate(), request.getToDate(), page);
        List<Map<String, Object>> recruitmentNewList = MapperUtils.underscoreToCamelcase(recruitmentNews.getContent());
        List<RecruitmentNewsDTO> result = getAllInfo(cid, uid, recruitmentNewList);
        return new PageImpl<>(result, page, recruitmentNews.getTotalElements());
    }

    public RecruitmentNewsDTO detail(Long cid, String uid, Long id) {
        RecruitmentNews recruitmentNews = recruitmentNewsRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, id));

        return toDTO(recruitmentNews);
    }

    public RecruitmentNewsDTO detailRecruitmentNews(Long cid, String uid, long id) {
        RecruitmentNews recruitmentNews = recruitmentNewsRepo.findByCompanyIdAndIdAndStatus(cid, id, Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, id));

        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, recruitmentNews.getRequestId(), Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentRequest.class, recruitmentNews.getRequestId()));

        RecruitmentPlan recruitmentPlan = recruitmentPlanRepo.findByCompanyIdAndIdAndStatus(cid, recruitmentNews.getPlanId(), Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, id));

        Set<Long> categoryIds = new HashSet<>();
        categoryIds.add(recruitmentRequest.getPositionId());
        categoryIds.add(recruitmentRequest.getLevelId());
        categoryIds.add(recruitmentRequest.getTitleId());
        categoryIds.add(recruitmentRequest.getGroupId());
        Map<Long, Map<String, Object>> mapCategory = configService.getCategoryByIds(uid, cid, categoryIds);

        Set<Long> orgIds = new HashSet<>();
        orgIds.add(recruitmentRequest.getOrgId());
        orgIds.add(recruitmentRequest.getOrgDeptId());
        var mapOrg = hcmService.getMapOrgs(uid, cid, orgIds);

        return RecruitmentNewsDTO.builder()
                .code(recruitmentNews.getCode())
                .title(recruitmentNews.getTitle())
                .requestCode(recruitmentRequest.getCode())
                .planCode(recruitmentPlan.getCode())
                .position(getValue(recruitmentRequest.getPositionId(), mapCategory))
                .level(getValue(recruitmentRequest.getLevelId(), mapCategory))
                .title(getValue(recruitmentRequest.getTitleId(), mapCategory))
                .groupName(getValue(recruitmentRequest.getGroupId(), mapCategory))
                .orgName(String.valueOf(mapOrg.get(recruitmentRequest.getOrgId())))
                .orgDeptName(String.valueOf(mapOrg.get(recruitmentRequest.getOrgDeptId())))
                .quantity(Long.valueOf(recruitmentRequest.getQuantity()))
                .workType(recruitmentRequest.getWorkType())
                .workArea(recruitmentRequest.getWorkArea())
                .salaryLevel(recruitmentRequest.getSalaryType().toString())
                .salaryFrom(Double.valueOf(recruitmentRequest.getFromSalary()))
                .salaryTo(Double.valueOf(recruitmentRequest.getToSalary()))
                .description(recruitmentNews.getDescription())
                .requirement(recruitmentNews.getRequirement())
                .profit(recruitmentNews.getProfit())
                .employeeId(recruitmentNews.getEmployeeId())
                .phone(recruitmentNews.getPhone())
                .email(recruitmentNews.getEmail())
                .deadlineSendCV(recruitmentNews.getDeadlineSendCV())
                .build();
    }

    private List<RecruitmentNewsDTO> getAllInfo(Long cid, String uid, List<Map<String, Object>> recruitmentNewList) {

        List<RecruitmentNewsDTO> rsDTOs = recruitmentNewList.stream().map(entity -> {
            RecruitmentNewsDTO dto = new RecruitmentNewsDTO();
            if (entity.get("newsId") != null) {
                dto.setId(Long.parseLong(String.valueOf(entity.get("newsId"))));
            }
            if(entity.get("newsCode") != null) {
                dto.setCode(String.valueOf(entity.get("newsCode")));
            }
            if(entity.get("newsName") != null) {
                dto.setTitle(String.valueOf(entity.get("newsName")));
            }
            if(entity.get("state") != null) {
                dto.setState(String.valueOf(entity.get("state")));
            }
            if(entity.get("positionId") != null) {
                dto.setPositionId(Long.parseLong(String.valueOf(entity.get("positionId"))));
            }
            if (entity.get("levelId") != null) {
                dto.setLevelId(Long.parseLong(String.valueOf(entity.get("levelId"))));
            }
            if(entity.get("titleId") != null) {
                dto.setTitleId(Long.parseLong(String.valueOf(entity.get("titleId"))));
            }
            if(entity.get("quantity") != null) {
                dto.setQuantity(Long.parseLong(String.valueOf(entity.get("quantity"))));
            }
            if(entity.get("deadlineSendCV") != null) {
                dto.setDeadlineSendCV(DateUtils.toDate(String.valueOf(entity.get("deadlineSendCV")), DateUtils.DB_DATE_PATTERN));
            }

            return dto;
        }).collect(Collectors.toList());

        Set<Long> categoryIds = rsDTOs.stream().map(dto -> {
            List<Long> subList = new ArrayList<>();
            if (dto.getPositionId() != null) {
                subList.add(dto.getPositionId());
            }

            if (dto.getLevelId() != null) {
                subList.add(dto.getLevelId());
            }

            if (dto.getTitleId() != null) {
                subList.add(dto.getTitleId());
            }

            return subList;
        }).flatMap(List::stream).collect(Collectors.toSet());

        Map<Long, Map<String, Object>> mapCategory = configService.getCategoryByIds(uid, cid, categoryIds);

        for (RecruitmentNewsDTO dto: rsDTOs) {
            dto.setPosition(getValue(dto.getPositionId(), mapCategory));
            dto.setTitle(getValue(dto.getTitleId(), mapCategory));
            dto.setLevel(getValue(dto.getLevelId(), mapCategory));
        }

        return rsDTOs;
    }

    public RecruitmentNewsDTO create(Long cid, String uid, RecruitmentNewsDTO dto) {
        RecruitmentNews news = RecruitmentNews.of(cid, uid, dto);

        news.setStatus(Constants.ENTITY_ACTIVE);

        news = recruitmentNewsRepo.save(news);

        RecruitmentPlanRequest planRequest = new RecruitmentPlanRequest();
        planRequest.setRecruitmentRequestId(dto.getRequestId());
        planRequest.setRecruitmentPlanId(dto.getPlanId());
        planRequest.setCreateBy(uid);
        planRequest.setCreateDate(new Date());
        planRequest.setUpdateBy(uid);
        planRequest.setDeadline(dto.getDeadlineSendCV());
        planRequest.setPicId(dto.getEmployeeId());

        planRequestRepo.save(planRequest);

        return toDTO(news);
    }

    public RecruitmentNewsDTO update(Long cid, String uid, RecruitmentNewsDTO dto) {
        RecruitmentNews news = RecruitmentNews.of(cid, uid, dto);

        RecruitmentNews curr = recruitmentNewsRepo.findById( dto.getId()).orElseThrow(() -> new EntityNotFoundException(Cost.class, dto.getId()));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);

        news = recruitmentNewsRepo.save(news);

        return toDTO(news);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        recruitmentNewsRepo.deleteRecruitmentNewsBy(cid,uid,ids);
    }

    private RecruitmentNewsDTO toDTO(RecruitmentNews news) {
        return MapperUtils.map(news, RecruitmentNewsDTO.class);
    }

    private List<RecruitmentNewsDTO> toDTOs(List<RecruitmentNews> newsList) {
        return newsList.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private String getValue(Long id, Map<Long, Map<String, Object>> mapCategory) {
        var category = mapCategory.get(id);
        return category != null ? "" + category.get("name") : "";
    }

    @Transactional
    public RecruitmentNews updateStateRecruitmentNews(long cid, String uid, Long id, String newState) {
        RecruitmentNews recruitmentNew = recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, id));

        if (Constants.RECRUITMENT_NEWS_STATE.INIT.name().equals(recruitmentNew.getState())) {
            if (!Constants.RECRUITMENT_NEWS_STATE.PROCESSING.name().equals(newState)) {
                throw new BusinessException("recruitment-news-new-state-invalid");
            }
        }
        if (Constants.RECRUITMENT_NEWS_STATE.PROCESSING.name().equals(recruitmentNew.getState())) {
            if (!(Constants.RECRUITMENT_NEWS_STATE.DONE.name().equals(newState) || Constants.RECRUITMENT_NEWS_STATE.END.name().equals(newState)
                    || Constants.RECRUITMENT_NEWS_STATE.EXTEND.name().equals(newState))) {
                throw new BusinessException("recruitment-news-new-state-invalid");
            }
        }
        recruitmentNew.setState(newState);
        recruitmentNewsRepo.save(recruitmentNew);
        return recruitmentNew;
    }

    public RecruitmentNews delete(long cid, String uid, Long id) {
        RecruitmentNews recruitmentNew = recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, id));
        if (!Constants.RECRUITMENT_NEWS_STATE.INIT.name().equals(recruitmentNew.getState())){
            throw new BusinessException("recruitment-news-new-delete-invalid-state");
        }
        recruitmentNew.setStatus(Constants.STATE_INACTIVE);
        recruitmentNewsRepo.save(recruitmentNew);
        return recruitmentNew;
    }
}
