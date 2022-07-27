package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.CustomPage;
import vn.ngs.nspace.recruiting.share.request.PlantRequestFilter;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RecruitmentPlanV2Service {

    private final RecruitmentPlanRepo planRepo;
    private final RecruitmentPlanRequestRepo planRequestRepo;
    private final RecruitmentNewsRepo newsRepo;
    private final RecruitmentRequestRepo requestRepo;
    private final CandidateRepo candidateRepo;
    private final JobApplicationRepo jobApplicationRepo;

    public RecruitmentPlanV2Service(RecruitmentPlanRepo planRepo, RecruitmentPlanRequestRepo planRequestRepo, RecruitmentNewsRepo newsRepo, RecruitmentRequestRepo requestRepo, CandidateRepo candidateRepo, JobApplicationRepo jobApplicationRepo) {
        this.planRepo = planRepo;
        this.planRequestRepo = planRequestRepo;
        this.newsRepo = newsRepo;
        this.requestRepo = requestRepo;
        this.candidateRepo = candidateRepo;
        this.jobApplicationRepo = jobApplicationRepo;
    }

    public RecruitmentPlanDTO create(Long cid, String uid, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findByCode(dto.getCode(),cid);
        if (recruitmentPlan != null) {
            throw new BusinessException("duplicate-data-with-this-Code");
        }

        recruitmentPlan = RecruitmentPlan.of(cid,uid,dto);
        recruitmentPlan.setStatus(Constants.ENTITY_ACTIVE);
        recruitmentPlan = planRepo.save(recruitmentPlan);

        List<RecruitmentPlanRequestDTO> recruitmentPlanRequests = dto.getRequestDTOList();

        RecruitmentPlan finalRecruitmentPlan = recruitmentPlan;
        recruitmentPlanRequests.forEach(
                e -> {
                    RecruitmentPlanRequest planRequest = new RecruitmentPlanRequest();
                    planRequest = RecruitmentPlanRequest.of(uid, cid, e);
                    planRequest.setRecruitmentPlanId(finalRecruitmentPlan.getId());
                    planRequestRepo.save(planRequest);

                    RecruitmentNews news = new RecruitmentNews();
                    news.setCreateBy(uid);
                    news.setUpdateBy(uid);
                    news.setCompanyId(cid);
                    news.setRequestId(e.getId());
                    news.setPlanId(finalRecruitmentPlan.getId());
                    news.setDeadlineSendCV(e.getDeadline());
                    news.setEmployeeId(e.getPicId());

                    newsRepo.save(news);
                }
        );
        return toDTO(cid,recruitmentPlan);
    }

    public Page<RecruitmentPlanDTO> getPage(Long cid, String uid, RecruitmentFilterRequest request, Pageable pageable) {
        Page<RecruitmentPlan> recruitmentPlans = planRepo.getPage(cid,request.getState(),request.getStartFrom(), request.getStartTo(),request.getEndFrom(), request.getEndTo(),request.getCreatedBy(),request.getSearch(), pageable);
        List<RecruitmentPlanDTO> dtos = toDTOs(cid,recruitmentPlans.getContent());

        return new PageImpl<RecruitmentPlanDTO>(dtos,recruitmentPlans.getPageable(), recruitmentPlans.getTotalElements());
    }

    public RecruitmentPlanDTO getById(Long cid, String uid, Long id, PlantRequestFilter filter) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));

        return toDTO(cid,recruitmentPlan,filter);
    }

    public RecruitmentPlanDTO update(Long cid, String uid, Long id, RecruitmentPlanDTO dto) {
        RecruitmentPlan recruitmentPlan = planRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlan.class,id));

        MapperUtils.copyWithoutAudit(dto,recruitmentPlan);
        recruitmentPlan.setUpdateBy(uid);
        recruitmentPlan = planRepo.save(recruitmentPlan);

        planRequestRepo.deleteByPlanId(cid,recruitmentPlan.getId());
        newsRepo.deleteRecruitmentByPlanId(cid,uid,id);

        List<RecruitmentPlanRequestDTO> recruitmentPlanRequests = dto.getRequestDTOList();

        if (recruitmentPlanRequests != null) {
            for (RecruitmentPlanRequestDTO planRequestDTO : recruitmentPlanRequests) {
                RecruitmentPlanRequest planRequest = new RecruitmentPlanRequest();
                planRequest = RecruitmentPlanRequest.of(uid, cid, planRequestDTO);
                planRequest.setRecruitmentPlanId(recruitmentPlan.getId());
                planRequest.setRecruitmentRequestId(planRequestDTO.getRequestDTO().getId());
                planRequestRepo.save(planRequest);

                RecruitmentNews news = new RecruitmentNews();
                news.setCreateBy(uid);
                news.setUpdateBy(uid);
                news.setCompanyId(cid);
                news.setRequestId(planRequestDTO.getId());
                news.setPlanId(recruitmentPlan.getId());
                news.setDeadlineSendCV(planRequestDTO.getDeadline());
                news.setEmployeeId(planRequestDTO.getPicId());
                newsRepo.save(news);
            }
            ;
        }


        return toDTO(cid,recruitmentPlan);
    }

    public void delete(Long cid,String uid,  List<Long> ids) {
        planRepo.delete(cid, uid, ids);
    }

    private List<RecruitmentPlanDTO> toDTOs(Long cid, List<RecruitmentPlan> plans) {
        return plans.stream().map(e->toDTO(cid,e)).collect(Collectors.toList());
    }

    private RecruitmentPlanDTO toDTO(Long cid, RecruitmentPlan plan) {
        List<RecruitmentPlanRequest> recruitmentPlanRequests =
                planRequestRepo.getByPlanId(cid,plan.getId());
        List<RecruitmentPlanRequestDTO> planRequestDTOS = recruitmentPlanRequests.stream()
                .map(e -> {
                    RecruitmentPlanRequestDTO planRequestDTO = new RecruitmentPlanRequestDTO();
                    RecruitmentRequest recruitmentRequest = requestRepo.findById(e.getRecruitmentRequestId())
                                    .orElseThrow(() ->  new EntityNotFoundException(RecruitmentRequest.class,e.getRecruitmentRequestId()));
                    RecruitmentRequestDTO recruitmentRequestDTO = toRequestDto(recruitmentRequest);
                    planRequestDTO.setRequestDTO(recruitmentRequestDTO);
                    planRequestDTO.setDeadline(e.getDeadline());
                    planRequestDTO.setPicId(e.getPicId());

                    return planRequestDTO;
                }).collect(Collectors.toList());
        Integer sumQuanity = 0; // tổng số lượng cần tuyển
        Integer sumCandidateRecruited = 0; // số lượng ứng tuyển
        Integer recruited = 0; // số lượng đã tuyển
        for(RecruitmentPlanRequestDTO planRequestDTO : planRequestDTOS){
            sumQuanity += planRequestDTO.getRequestDTO().getQuantity();
            // số lượng ứng tuyển
            sumCandidateRecruited += candidateRepo.sumCandidateRecruitmentRequestIdAndRecruitmentPlanId(planRequestDTO.getRequestDTO().getId(), planRequestDTO.getRequestDTO().getRecruitmentPlanId());
            // số lượng đã tuyển
            recruited += jobApplicationRepo.sumEmployeeByPlant(planRequestDTO.getRequestDTO().getRecruitmentPlanId(), planRequestDTO.getRequestDTO().getId());
        }

        RecruitmentPlanDTO dto = MapperUtils.map(plan,RecruitmentPlanDTO.class);
        dto.setSumQuanity(sumQuanity);
        dto.setSumCandidateRecruited(sumCandidateRecruited);
        dto.setRecruited(recruited);



        return  dto;
    }

    public Map<String, Integer> getSumAll(Long cid){
        List<RecruitmentPlanRequest> recruitmentPlanRequests =  planRequestRepo.getAllByCompanyIdAndStatus(cid, Constants.ENTITY_ACTIVE);
        Integer sumQuanity = 0;
        Integer sumCandidateRecruited = 0;
        Integer recruited = 0;
        for(RecruitmentPlanRequest recruitmentPlanRequest : recruitmentPlanRequests){
            if(recruitmentPlanRequest.getRecruitmentPlanId() != null){
                RecruitmentPlan recruitmentPlan = planRepo.findById(recruitmentPlanRequest.getRecruitmentPlanId()).orElse(new RecruitmentPlan());
                Calendar calendarRecruitment = new GregorianCalendar();
                calendarRecruitment.setTime(recruitmentPlan.getStartDate());
                Calendar calendarNow = new GregorianCalendar();
                calendarNow.setTime(new Date());
                if(calendarRecruitment.get(Calendar.YEAR) == calendarNow.get(Calendar.YEAR)){
                    RecruitmentRequest recruitmentRequest = requestRepo.findById(recruitmentPlanRequest.getRecruitmentRequestId()).get();
                    sumQuanity += recruitmentRequest.getQuantity();
                    // số lượng ứng tuyển
                    sumCandidateRecruited += candidateRepo.sumCandidateRecruitmentRequestIdAndRecruitmentPlanId(recruitmentRequest.getId(), recruitmentPlanRequest.getRecruitmentPlanId());
                    // số lượng đã tuyển
                    recruited += jobApplicationRepo.sumEmployeeByPlant(recruitmentPlanRequest.getRecruitmentPlanId(), recruitmentRequest.getId());
                }
            }
        }

        Map<String, Integer> result = new HashMap<>();
        result.put("totalSumQuanity", sumQuanity);
        result.put("totalSumRecrutingAll", sumCandidateRecruited);
        result.put("totalRecruted", recruited);
        return result;
    }

    private RecruitmentPlanDTO toDTO(Long cid, RecruitmentPlan plan, PlantRequestFilter filter) {
        List<RecruitmentPlanRequest> recruitmentPlanRequests =
                planRequestRepo.getByPlanIdAndFilter(cid,plan.getId(), filter.getType(), filter.getTypeRequest(),filter.getSearch(),filter.getState(),filter.getPicId(),filter.getPositionId(),filter.getOrgId(),filter.getLevelId());
        List<RecruitmentPlanRequestDTO> planRequestDTOS = recruitmentPlanRequests.stream()
            .map(e -> {
                RecruitmentPlanRequestDTO planRequestDTO = new RecruitmentPlanRequestDTO();
                RecruitmentRequest recruitmentRequest = requestRepo.findById(e.getRecruitmentRequestId())
                        .orElseThrow(() ->  new EntityNotFoundException(RecruitmentRequest.class,e.getRecruitmentRequestId()));
                RecruitmentRequestDTO recruitmentRequestDTO = toRequestDto(recruitmentRequest);
                recruitmentRequestDTO.setCandidateRecruited(candidateRepo.sumCandidateRecruitmentRequestIdAndRecruitmentPlanId(recruitmentRequest.getId(), recruitmentRequestDTO.getRecruitmentPlanId()));
                recruitmentRequestDTO.setRecruited(jobApplicationRepo.sumEmployeeByPlant(recruitmentRequestDTO.getRecruitmentPlanId(), recruitmentRequest.getId()));
                planRequestDTO.setRequestDTO(recruitmentRequestDTO);
                planRequestDTO.setDeadline(e.getDeadline());
                planRequestDTO.setPicId(e.getPicId());

                return planRequestDTO;
            }).skip(filter.getSize()*filter.getPage()).limit(filter.getSize()).collect(Collectors.toList());
        RecruitmentPlanDTO dto = MapperUtils.map(plan,RecruitmentPlanDTO.class);

        Integer sumQuanity = 0; // tổng số lượng cần tuyển
        Integer sumCandidateRecruited = 0; // số lượng ứng tuyển
        Integer recruited = 0; // số lượng đã tuyển
        for(RecruitmentPlanRequestDTO planRequestDTO : planRequestDTOS){
            sumQuanity += planRequestDTO.getRequestDTO().getQuantity();
            // số lượng ứng tuyển
            sumCandidateRecruited += candidateRepo.sumCandidateRecruitmentRequestIdAndRecruitmentPlanId(planRequestDTO.getRequestDTO().getId(), planRequestDTO.getRequestDTO().getRecruitmentPlanId());
            // số lượng đã tuyển
            recruited += jobApplicationRepo.sumEmployeeByPlant(planRequestDTO.getRequestDTO().getRecruitmentPlanId(),planRequestDTO.getRequestDTO().getId());
        }

        CustomPage<RecruitmentPlanRequestDTO> customPage = new CustomPage<RecruitmentPlanRequestDTO>(filter.getSize(), filter.getPage(), recruitmentPlanRequests.size(),planRequestDTOS);
        dto.setRequestDTOCustomPage(customPage);
        dto.setSumQuanity(sumQuanity);
        dto.setSumCandidateRecruited(sumCandidateRecruited);
        dto.setRecruited(recruited);
        return  dto;
    }

    private RecruitmentRequestDTO toRequestDto(RecruitmentRequest recruitmentRequest) {
        return MapperUtils.map(recruitmentRequest,RecruitmentRequestDTO.class);
    }

    private List<RecruitmentRequestDTO> toListRequestDto(List<RecruitmentRequest> recruitmentRequests) {
        return recruitmentRequests.stream().map(this::toRequestDto).collect(Collectors.toList());
    }

}
