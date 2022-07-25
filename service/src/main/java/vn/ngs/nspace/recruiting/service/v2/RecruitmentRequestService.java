package vn.ngs.nspace.recruiting.service.v2;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanRequest;
import vn.ngs.nspace.recruiting.model.RecruitmentRequest;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.DemarcationRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanRequestRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentRequestRepo;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.service.DemarcationService;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDemarcationDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentResponseDemarcationDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.RecruitmentRequestFilterRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentRequestService {

    private final RecruitmentRequestRepo recruitmentRequestRepo;
    private final RecruitmentPlanRequestRepo recruitmentPlanRequestRepo;
    private final ExecuteConfigService configService;
    private final ExecuteHcmService hcmService;
    private final DemarcationRepo demarcationRepo;
    private final DemarcationService demarcationService;
    private final ExecuteHcmService executeHcmService;
    private final CandidateRepo candidateRepo;

    @Transactional
    public RecruitmentRequestDTO createRecruitmentRequest(Long cid, String uid, RecruitmentRequestDTO dtoList) {
        validateInput(dtoList);
        for(int i = 0;  i < dtoList.getRecruitmentResponseDemarcationDTOS().size(); i++){
            RecruitmentRequest recruitmentRequest = RecruitmentRequest.of(cid, uid, dtoList);
            recruitmentRequest.setDemarcationId(dtoList.getRecruitmentResponseDemarcationDTOS().get(i).getId());
            if (dtoList.getRecruitmentResponseDemarcationDTOS().get(i).getQuantity() == null) {
                throw new BusinessException("quantity-recruit-null");
            }
            recruitmentRequest.setQuantity(dtoList.getRecruitmentResponseDemarcationDTOS().get(i).getQuantity());
            recruitmentRequest.setState(Constants.HCM_RECRUITMENT.INIT.toString());
            recruitmentRequestRepo.save(recruitmentRequest);

            RecruitmentPlanRequest planRequest = RecruitmentPlanRequest.builder()
                    .recruitmentPlanId(dtoList.getRecruitmentPlanId())
                    .recruitmentRequestId(recruitmentRequest.getId())
                    .build();
            planRequest.setCompanyId(cid);
            planRequest.setUpdateBy(uid);
            planRequest.setCreateBy(uid);
            recruitmentPlanRequestRepo.save(planRequest);
        }

        return dtoList;
    }

    @Transactional
    public RecruitmentRequestDTO updateRecruitmentRequest(Long cid, String uid, RecruitmentRequestDTO dto) {
        validateInput(dto);
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, dto.getId(), Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new BusinessException("recruitment-request-does-not-exists"));

        if (dto.getRecruitmentResponseDemarcationDTOS().get(0).getId() == null) {
            throw new BusinessException("demarcation-recruit-null");
        }

        if (dto.getRecruitmentResponseDemarcationDTOS().get(0).getQuantity() == null) {
            throw new BusinessException("quantity-recruit-null");
        }
        recruitmentRequest.setUpdateBy(uid);
        recruitmentRequest.setCode(dto.getCode());
        recruitmentRequest.setOrgId(dto.getOrgId());
        recruitmentRequest.setOrgDeptId(dto.getOrgDeptId());
        recruitmentRequest.setGroupId(dto.getGroupId());
        recruitmentRequest.setPositionId(dto.getPositionId());
        recruitmentRequest.setTitleId(dto.getTitleId());
        recruitmentRequest.setLevelId(dto.getLevelId());
        recruitmentRequest.setState(dto.getState());
        recruitmentRequest.setContractTypeId(dto.getContractTypeId());
        recruitmentRequest.setQuantity(dto.getRecruitmentResponseDemarcationDTOS().get(0).getQuantity());
        recruitmentRequest.setType(dto.getType());
        recruitmentRequest.setStartDate(dto.getStartDate());
        recruitmentRequest.setEndDate(dto.getEndDate());
        recruitmentRequest.setWorkType(dto.getWorkType());
        recruitmentRequest.setWorkArea(dto.getWorkArea());
        recruitmentRequest.setSalaryType(dto.getSalaryType());
        recruitmentRequest.setFromSalary(dto.getFromSalary());
        recruitmentRequest.setToSalary(dto.getToSalary());
        recruitmentRequest.setCurrencyUnit(dto.getCurrencyUnit());
        recruitmentRequest.setGender(dto.getGender());
        recruitmentRequest.setDegree(dto.getDegree());
        recruitmentRequest.setFromAge(dto.getFromAge());
        recruitmentRequest.setToAge(dto.getToAge());
        recruitmentRequest.setJobDescription(dto.getJobDescription());
        recruitmentRequest.setOtherRequirement(dto.getOtherRequirement());
        recruitmentRequest.setBenefit(dto.getBenefit());
        recruitmentRequest.setDemarcationId(dto.getRecruitmentResponseDemarcationDTOS().get(0).getId());
        recruitmentRequest.setCreateBy(uid);
        recruitmentRequestRepo.save(recruitmentRequest);

        List<RecruitmentPlanRequest> recruitmentPlanRequests = recruitmentPlanRequestRepo.findByCompanyIdAndRecruitmentRequestIdAndStatus(cid, recruitmentRequest.getId(), Constants.ENTITY_ACTIVE);
        recruitmentPlanRequests.forEach(recruitmentPlanRequest -> recruitmentPlanRequest.setRecruitmentPlanId(dto.getRecruitmentPlanId()));
        recruitmentPlanRequestRepo.saveAll(recruitmentPlanRequests);

        return dto;
    }

    @Transactional
    public RecruitmentRequest deleteRecruitmentRequest(Long cid, String uid, Long id) {
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, id, Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new BusinessException("recruitment-request-does-not-exists"));
        recruitmentRequest.setStatus(Constants.ENTITY_INACTIVE);
        recruitmentRequest.setUpdateBy(uid);
        recruitmentRequestRepo.save(recruitmentRequest);

        List<RecruitmentPlanRequest> recruitmentPlanRequests = recruitmentPlanRequestRepo.findByCompanyIdAndRecruitmentRequestIdAndStatus(cid, recruitmentRequest.getId(), Constants.ENTITY_ACTIVE);
        recruitmentPlanRequests.forEach(recruitmentPlanRequest -> {
            recruitmentPlanRequest.setStatus(Constants.ENTITY_INACTIVE);
            recruitmentPlanRequest.setUpdateBy(uid);
        });
        recruitmentPlanRequestRepo.saveAll(recruitmentPlanRequests);

        return recruitmentRequest;
    }

    public RecruitmentRequestDTO detailRecruitmentRequest(Long cid, String uid, Long id) {
        RecruitmentRequest recruitmentRequest = recruitmentRequestRepo.findByCompanyIdAndIdAndStatus(cid, id, Constants.ENTITY_ACTIVE)
                .orElseThrow(() -> new BusinessException("recruitment-request-does-not-exists"));

        List<RecruitmentRequestDTO> result = getAllInfo(cid, uid, List.of(recruitmentRequest));

        RecruitmentRequestDemarcationDTO demarcationDTO = new RecruitmentRequestDemarcationDTO();
        demarcationDTO.setDateDemarcationYear(result.get(0).getDateRequestYear());
        demarcationDTO.setDateDemarcationMonth(result.get(0).getDateRequestMonth());
        demarcationDTO.setLevelId(result.get(0).getLevelId());
        demarcationDTO.setPositionId(result.get(0).getPositionId());
        demarcationDTO.setOrgId(result.get(0).getOrgId());
        demarcationDTO.setTitleId(result.get(0).getTitleId());

        // tim dinh bien trong phiếu yêu cầu detail
        Demarcation demarcation =  demarcationRepo.findById(recruitmentRequest.getDemarcationId()).orElseThrow(() -> new BusinessException("Can not find demarcation in recruitment"));
        Integer sumDemarcationMissing = findRecruimentDemarcation(demarcation, uid, cid, recruitmentRequest.getQuantity());
        RecruitmentResponseDemarcationDTO responseDemarcationDTO = new RecruitmentResponseDemarcationDTO(); // 1 tìm kiếm theo phiếu đang sửa
        responseDemarcationDTO.setOrgId(result.get(0).getOrgId());
        responseDemarcationDTO.setTitleId(result.get(0).getTitleId());
        responseDemarcationDTO.setLevelId(result.get(0).getLevelId());
        responseDemarcationDTO.setPositionId(result.get(0).getPositionId());
        responseDemarcationDTO.setQuantity(recruitmentRequest.getQuantity());
        responseDemarcationDTO.setSumDemarcationMissing(sumDemarcationMissing);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM");
        responseDemarcationDTO.setDateDemarcationYear(Integer.valueOf(simpleDateFormat.format(demarcation.getDemarcationDate()).split("-")[1]));
        responseDemarcationDTO.setDateDemarcationMonth(Integer.valueOf(simpleDateFormat.format(demarcation.getDemarcationDate()).split("-")[0]));
        responseDemarcationDTO.setId(recruitmentRequest.getDemarcationId());


        RecruitmentRequestDTO recruitmentRequestDTO = result.get(0);
        // Tra ve dinh bien
        List<RecruitmentResponseDemarcationDTO> responseDemarcationDTOList = new ArrayList<>();
        responseDemarcationDTOList.add(responseDemarcationDTO);
        recruitmentRequestDTO.setRecruitmentResponseDemarcationDTOS(responseDemarcationDTOList);
        return recruitmentRequestDTO;
    }

    public void validateInput(RecruitmentRequestDTO dto) {
        if (StringUtils.isEmpty(dto.getCode())) {
            throw new BusinessException("request-code-null");
        }

        if (!dto.getCode().matches(Constants.RECRUITMENT_REQUEST_CODE_REGEX)) {
            throw new BusinessException("allow-only-letter-number");
        }

        if (dto.getOrgId() == null) {
            throw new BusinessException("unit-null");
        }

        if (dto.getStartDate() == null) {
            throw new BusinessException("required-field-null");
        }
    }

    public Page<RecruitmentRequestDTO> getPage(long cid, String uid, RecruitmentRequestFilterRequest request, Pageable page) {
        validateDataSearch(request);
        if (Constants.GET_ALL.equals(request.getGetAll())){
            page = PageRequest.of(0,Integer.MAX_VALUE,page.getSort());
        }

        List<Long> orgIds = new ArrayList<>();
        List<Long> positionIds = new ArrayList<>();
        List<String> createdByUids = new ArrayList<>();
        List<String> statuses = new ArrayList<>();
        String search;
        Integer quantity = 0;

        if (request.getOrgId() == null) {
            orgIds.add(-1L);
        } else {
            orgIds.add(request.getOrgId());
        }

        if (CollectionUtils.isEmpty(request.getPositionIds())) {
            positionIds.add(-1L);
        } else {
            positionIds.addAll(request.getPositionIds());
        }

        if (CollectionUtils.isEmpty(request.getCreateByUIds())) {
            createdByUids.add(StringUtils.EMPTY);
        } else {
            createdByUids.addAll(request.getCreateByUIds());
        }

        if (CollectionUtils.isEmpty(request.getStatuses())) {
            statuses.add(StringUtils.EMPTY);
        } else {
            statuses.addAll(request.getStatuses());
        }

        if (StringUtils.isEmpty(request.getSearch())) {
            search = "%%";
        } else {
            try {
                quantity = Integer.parseInt(request.getSearch());
            } catch (Exception e) {
                quantity = 0;
            }
            search = "%" + request.getSearch() + "%";
        }

            Page<RecruitmentRequest> recruitmentRequests = recruitmentRequestRepo.filterAllByPage(cid, orgIds, positionIds, createdByUids, statuses, search, quantity, request.getType(), request.getFromDate(), request.getToDate(), page);
        List<RecruitmentRequest> rsList = recruitmentRequests.getContent();
        List<RecruitmentRequestDTO> rsDTOList = getAllInfo(cid, uid, rsList);

        return new PageImpl<>(rsDTOList, page, recruitmentRequests.getTotalElements());
    }

    public List<RecruitmentRequestDTO> getAllByState(Long cid, String uid) {
        List<String> states = List.of(Constants.HCM_RECRUITMENT_REQUEST.EXTEND.toString()
                ,Constants.HCM_RECRUITMENT_REQUEST.INIT.toString());
        List<RecruitmentRequest> recruitmentRequests = recruitmentRequestRepo.getAllByStateAndNews(cid,states);

        return toDTOs(recruitmentRequests);
    }

    public List<RecruitmentRequestDTO> getAllByPlan(Long cid, String uid, Long planId) {
        List<RecruitmentRequest> recruitmentRequests = recruitmentRequestRepo.getALlByPlanId(cid,planId);

        return toDTOs(recruitmentRequests);
    }

    private List<RecruitmentRequestDTO> getAllInfo(Long cid, String uid, List<RecruitmentRequest> rsList) {
        List<RecruitmentRequestDTO> rsDTOList = rsList.stream().map(entity -> {
            RecruitmentRequestDTO dto = new RecruitmentRequestDTO();
            BeanUtils.copyProperties(entity, dto);
            return dto;
        }).collect(Collectors.toList());

        Set<Long> categoryIds = rsDTOList.stream().map(dto -> {
            List<Long> subList = new ArrayList<>();
            if (dto.getTitleId() != null) {
                subList.add(dto.getTitleId());
            }
            if (dto.getPositionId() != null) {
                subList.add(dto.getPositionId());
            }
            if (dto.getLevelId() != null) {
                subList.add(dto.getLevelId());
            }
            if (dto.getGroupId() != null) {
                subList.add(dto.getGroupId());
            }
            if (dto.getContractTypeId() != null) {
                subList.add(dto.getContractTypeId());
            }
            if (dto.getSalaryType() != null) {
                subList.add(dto.getSalaryType());
            }
            if (dto.getGender() != null) {
                subList.add(dto.getGender());
            }
            if (dto.getDegree() != null) {
                subList.add(dto.getDegree());
            }
            if (dto.getCurrencyUnit() != null) {
                subList.add(dto.getCurrencyUnit());
            }
            return subList;
        }).flatMap(List::stream).collect(Collectors.toSet());

        Set<Long> orgIds = rsDTOList.stream().map(dto -> {
            List<Long> subList = new ArrayList<>();
            subList.add(dto.getOrgId());
            if (dto.getOrgDeptId() != null) {
                subList.add(dto.getOrgDeptId());
            }

            return subList;
        }).flatMap(List::stream).collect(Collectors.toSet());

        Map<Long, Map<String, Object>> mapCategory = configService.getCategoryByIds(uid, cid, categoryIds);
        var mapOrg = hcmService.getMapOrgs(uid, cid, orgIds);
        for (RecruitmentRequestDTO dto : rsDTOList) {
            if (dto.getTitleId() != null) {
                var category = mapCategory.get(dto.getTitleId());
                dto.setTittleName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getPositionId() != null) {
                var category = mapCategory.get(dto.getPositionId());
                dto.setPositionName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getLevelId() != null) {
                var category = mapCategory.get(dto.getLevelId());
                dto.setLevelName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getGroupId() != null) {
                var category = mapCategory.get(dto.getGroupId());
                dto.setGroupName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getContractTypeId() != null) {
                var category = mapCategory.get(dto.getContractTypeId());
                dto.setContractTypeName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getSalaryType() != null) {
                var category = mapCategory.get(dto.getSalaryType());
                dto.setSalaryTypeName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getGender() != null) {
                var category = mapCategory.get(dto.getGender());
                dto.setGenderName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getDegree() != null) {
                var category = mapCategory.get(dto.getDegree());
                dto.setDegreeName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getCurrencyUnit() != null) {
                var category = mapCategory.get(dto.getCurrencyUnit());
                dto.setCurrencyUnitName(category != null ? "" + category.get("name") : "");
            }
            if (dto.getOrgId() != null) {
                var org = mapOrg.get(dto.getOrgId());
                dto.setOrgName(org != null ? org.getName() : "");
            }
            if (dto.getOrgDeptId() != null) {
                var org = mapOrg.get(dto.getOrgDeptId());
                dto.setOrgDeptName(org != null ? org.getName() : "");
            }
        }
        return rsDTOList;
    }

    private void validateDataSearch(RecruitmentRequestFilterRequest request) {

    }

    private RecruitmentRequestDTO toDTO(RecruitmentRequest recruitmentRequest) {
        return MapperUtils.map(recruitmentRequest, RecruitmentRequestDTO.class);
    }

    private List<RecruitmentRequestDTO> toDTOs(List<RecruitmentRequest> recruitmentRequests) {
        return recruitmentRequests.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<RecruitmentResponseDemarcationDTO> search(String uId, Long cId, RecruitmentRequestDemarcationDTO requestDemarcationDTO){
        try {
            List<Map<String, Object>> dataSearch = demarcationRepo.search(
                    requestDemarcationDTO.getOrgId(),
                    requestDemarcationDTO.getLevelId(),
                    requestDemarcationDTO.getPositionId(),
                    requestDemarcationDTO.getTitleId(),
                    requestDemarcationDTO.getDateDemarcationYear(),
                    requestDemarcationDTO.getDateDemarcationMonth()
            );
            List<RecruitmentResponseDemarcationDTO> dtoList = new ArrayList<>();
            for(int i = 0; i < dataSearch.size(); i++){
                Map<String, Object> itemData = dataSearch.get(i);
                RecruitmentResponseDemarcationDTO responseDto = new RecruitmentResponseDemarcationDTO();
                responseDto.setOrgId(Long.valueOf(itemData.get("orgid").toString()));
                responseDto.setLevelId(Long.valueOf(itemData.get("levelid").toString()));
                responseDto.setTitleId(Long.valueOf(itemData.get("titleids").toString()));
                responseDto.setPositionId(Long.valueOf(itemData.get("positionid").toString()));
                responseDto.setDateDemarcationYear(requestDemarcationDTO.getDateDemarcationYear());
                responseDto.setDateDemarcationMonth(requestDemarcationDTO.getDateDemarcationMonth());
                responseDto.setSumDemarcationMissing(demarcationMissing(Long.valueOf(itemData.get("id").toString()), responseDto.getOrgId(), responseDto.getLevelId(), responseDto.getTitleId(), responseDto.getPositionId(), uId, cId, requestDemarcationDTO.getDateDemarcationYear(), requestDemarcationDTO.getDateDemarcationMonth()));
                responseDto.setId(Long.valueOf(itemData.get("id").toString()));
                dtoList.add(responseDto);
            }
            return dtoList;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Integer findRecruimentDemarcation(Demarcation demarcation, String uId, Long cId, int sumRequestDemarcation){
        try {
            Integer sumDemarcation = demarcation.getSumDemarcation();
            OnboardEmployeeFilterRequest onboardEm = new OnboardEmployeeFilterRequest();
            onboardEm.setOrgId(demarcation.getOrgId());
            onboardEm.setTitleId(demarcation.getTitleId());
            onboardEm.setPositionId(demarcation.getPositionId());
            List<String> states = new ArrayList<>();
            states.add("official");
            states.add("probation");
            onboardEm.setStates(states);
            Long orgId = demarcation.getOrgId();
            Long levelId = demarcation.getLevelId();
            Long titleId = demarcation.getTitleId();
            Long positionId = demarcation.getPositionId();
            // So luong nhan vien chinh thuc hoac thu viec theo orgId, levelId, titleId, positionId
            Integer employeeSize = 0;
            Map<String, Object> listEmployee = executeHcmService.filter(uId, cId, onboardEm).getData();
            List<Map<String, Object>> content = (List<Map<String, Object>>) listEmployee.get("content");
            if(levelId != null){
                for(Map<String, Object> keySet : content){
                    if(keySet.get("level_Id") != null){
                        if(Long.valueOf(keySet.get("level_Id").toString()) == levelId){
                            employeeSize++;
                        }
                    }
                }
            }
            else{
                employeeSize = listEmployee.size();
            }
            // So luong can tuyen YCTD trang thai moi, da duyet
            Integer sumRecruitment = 0;
            List<RecruitmentRequest> recruitmentRequestListInit = recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.INIT.name());
            for(RecruitmentRequest recruitmentRequest : recruitmentRequestListInit) sumRecruitment += recruitmentRequest.getQuantity();
            List<RecruitmentRequest> recruitmentRequestListApproved = recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.DONE.name());
            for(RecruitmentRequest recruitmentRequest : recruitmentRequestListApproved) sumRecruitment += recruitmentRequest.getQuantity();
            List<RecruitmentRequest> recruitmentRequestListProcess= recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.PROCESSING.name());


            // So luong con thieu trong ke hoach tuyen dung
            Integer totalMissing = 0;
            for(RecruitmentRequest request : recruitmentRequestListProcess){
                for(Candidate candidate : candidateRepo.findAllByRecruitmentRequestId(request.getId())){
                    if(candidate.getEmployeeId() != null){
                        totalMissing++;
                    }
                }
            }

            int result = sumDemarcation - employeeSize - sumRecruitment - totalMissing;
            // trừ số lượng của phiếu đang sửa
            if(result == 0 && sumDemarcation > 0){
                return sumRequestDemarcation;
            }

            return result;

        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Integer demarcationMissing(Long id, Long orgId, Long levelId, Long titleId, Long positionId,String uId, Long cId, Integer year, Integer month){
        try {
            Integer sumDemarcation = demarcationRepo.findById(id).get().getSumDemarcation();
            OnboardEmployeeFilterRequest onboardEm = new OnboardEmployeeFilterRequest();
            onboardEm.setOrgId(orgId);
            onboardEm.setTitleId(titleId);
            onboardEm.setPositionId(positionId);
            List<String> states = new ArrayList<>();
            states.add("official");
            states.add("probation");
            onboardEm.setStates(states);
            // So luong nhan vien chinh thuc hoac thu viec theo orgId, levelId, titleId, positionId
            Integer employeeSize = 0;
            Map<String, Object> listEmployee = executeHcmService.filter(uId, cId, onboardEm).getData();
            List<Map<String, Object>> content = (List<Map<String, Object>>) listEmployee.get("content");
            if(levelId != null){
                for(Map<String, Object> keySet : content){
                    if(keySet.get("level_Id") != null){
                        if(Long.valueOf(keySet.get("level_Id").toString()) == levelId){
                            employeeSize++;
                        }
                    }
                }
            }
            else{
                employeeSize = listEmployee.size();
            }
            // So luong can tuyen YCTD trang thai moi, da duyet
            Integer sumRecruitment = 0;
            List<RecruitmentRequest> recruitmentRequestListInit = recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.INIT.name());
            for(RecruitmentRequest recruitmentRequest : recruitmentRequestListInit) sumRecruitment += recruitmentRequest.getQuantity();
            List<RecruitmentRequest> recruitmentRequestListApproved = recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.DONE.name());
            for(RecruitmentRequest recruitmentRequest : recruitmentRequestListApproved) sumRecruitment += recruitmentRequest.getQuantity();
            List<RecruitmentRequest> recruitmentRequestListProcess= recruitmentRequestRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndState(orgId, levelId, titleId, positionId, Constants.RECRUITMENT_NEWS_STATE.PROCESSING.name());


            // So luong con thieu trong ke hoach tuyen dung
            Integer totalMissing = 0;
            for(RecruitmentRequest request : recruitmentRequestListProcess){
                for(Candidate candidate : candidateRepo.findAllByRecruitmentRequestId(request.getId())){
                    if(candidate.getEmployeeId() != null){
                        totalMissing++;
                    }
                }
            }

            return sumDemarcation - employeeSize - sumRecruitment - totalMissing;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
