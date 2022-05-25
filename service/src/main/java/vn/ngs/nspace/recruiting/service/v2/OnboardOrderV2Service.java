package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.*;
import vn.ngs.nspace.recruiting.share.request.OnboardCandidateFilter;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardOrderV2Service {

    private final OnboardOrderRepo onboardOrderRepo;
    private final JobApplicationRepo jobApplicationRepo;
    private final OnboardOrderCheckListRepo checkListRepo;
    private final CandidateRepo candidateRepo;

    public OnboardOrderV2Service(OnboardOrderRepo onboardOrderRepo, JobApplicationRepo jobApplicationRepo, OnboardOrderCheckListRepo checkListRepo, CandidateRepo candidateRepo) {
        this.onboardOrderRepo = onboardOrderRepo;
        this.jobApplicationRepo = jobApplicationRepo;
        this.checkListRepo = checkListRepo;
        this.candidateRepo = candidateRepo;
    }

    protected OnboardOrderDTO create(Long cid, String uid, OnboardOrderDTO request) {
        OnboardOrder onboardOrder = OnboardOrder.of(cid, uid, request);

        onboardOrder.setCreateBy(uid);
        onboardOrder.setStatus(Constants.ENTITY_ACTIVE);

        return toDTO(onboardOrderRepo.save(onboardOrder));
    }

    private OnboardOrderDTO update(Long cid, String uid, OnboardOrderDTO dto) {
        OnboardOrder onboardOrder = onboardOrderRepo.findByCompanyIdAndId(cid, dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, dto.getId()));

        MapperUtils.copyWithoutAudit(dto, onboardOrder);
        onboardOrder.setUpdateBy(uid);
        onboardOrder = onboardOrderRepo.save(onboardOrder);

        return toDTO(onboardOrder);
    }

    public List<OnboardOrderDTO> creates(Long cid, String uid, Long candidateId) {
        JobApplication jobApplication = jobApplicationRepo.findByStatusCompanyIdCandidateId(candidateId, cid);

        if (jobApplication == null) return new ArrayList<>();

        List<OnboardOrderCheckList> checkLists = checkListRepo.getAllCheckList();
        List<OnboardOrderDTO> onboardOrderDTOS = new ArrayList<>();

        checkLists.forEach(
                e -> {
                    onboardOrderDTOS.add(init(cid, uid, jobApplication.getId(), e.getId()));
                }
        );

        return onboardOrderDTOS;
    }

    public OnboardOrderDTO init(Long cid, String uid, Long jobApplicationId, Long checkListId) {
        OnboardOrder onboardOrder = new OnboardOrder();

        onboardOrder.setJobApplicationId(jobApplicationId);
        onboardOrder.setCompanyId(cid);
        onboardOrder.setOnboardOrderId(checkListId);
        onboardOrder.setState(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.PENDING.name());

        onboardOrderRepo.save(onboardOrder);

        return toDTO(onboardOrder);
    }


    public Page<OnboardWithStateDTO> getPageOnboard(Long cid, String uid, String search, Pageable pageable) {
        Page<OnboardOrderCheckList> checkLists = checkListRepo.getPageOnboard(cid, pageable);

        List<OnboardWithStateDTO> onboardWithStateDTOS = new ArrayList<>();
        checkLists.getContent().forEach(
                e -> {
                    Integer pending = onboardOrderRepo.countByCompany(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.PENDING.name(), cid, e.getId());
                    Integer process = onboardOrderRepo.countByCompany(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.PROCESSING.name(), cid, e.getId());
                    Integer complete = onboardOrderRepo.countByCompany(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.COMPLETE.name(), cid, e.getId());
                    Integer cancel = onboardOrderRepo.countByCompany(vn.ngs.nspace.recruiting.share.dto.utils.Constants.HCM_RECRUITMENT_ONBOARD.CANCEL.name(), cid, e.getId());

                    OnboardWithStateDTO onboardWithStateDTO = new OnboardWithStateDTO();
                    onboardWithStateDTO.setCheckListDTO(toDTO(e));
                    onboardWithStateDTO.setPending(pending);
                    onboardWithStateDTO.setCancel(cancel);
                    onboardWithStateDTO.setComplete(complete);
                    onboardWithStateDTO.setProcessing(process);

                    onboardWithStateDTOS.add(onboardWithStateDTO);
                }
        );

        return new PageImpl<>(onboardWithStateDTOS, checkLists.getPageable(), checkLists.getTotalElements());
    }

    public Page<OnboardOrderDTO> getJobApplicationOnboardPage(Long cid, String uid, OnboardCandidateFilter filter, Long checkListId, Pageable pageable) {
        Page<OnboardOrder> onboardOrders = onboardOrderRepo.getPage(cid, checkListId, pageable);

        List<OnboardOrderDTO> onboardOrderDTOS = toDTOs(onboardOrders.getContent());

        return new PageImpl<>(onboardOrderDTOS, onboardOrders.getPageable(), onboardOrders.getTotalElements());
    }

    // cập nhật trạng thái công việc theo đầu mục
    public void updateStates(Long cid, String uid, List<OnboardOrderDTO> onboardOrderDTOs) {
        onboardOrderDTOs.forEach(
                e -> {
                    update(cid, uid, e);
                }
        );
    }

    private OnboardOrderDTO toDTO(OnboardOrder order) {
        JobApplication jobApplication = jobApplicationRepo.getOne(order.getJobApplicationId());
        JobApplicationDTO jobApplicationDTO = MapperUtils.map(jobApplication, JobApplicationDTO.class);

        Candidate candidate = candidateRepo.getOne(jobApplication.getCandidateId());
        CandidateDTO candidateDTO = MapperUtils.map(candidate, CandidateDTO.class);

        jobApplicationDTO.setCandidateObj(candidateDTO);

        OnboardOrderCheckList checkList = checkListRepo.getOne(order.getOnboardOrderId());
        OnboardOrderCheckListDTO checkListDTO = MapperUtils.map(checkList, OnboardOrderCheckListDTO.class);

        OnboardOrderDTO dto = MapperUtils.map(order, OnboardOrderDTO.class);
        dto.setOnboardOrderCheckListDTO(checkListDTO);
        dto.setJobApplicationDTO(jobApplicationDTO);

        return dto;
    }

    private List<OnboardOrderDTO> toDTOs(List<OnboardOrder> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private OnboardOrderCheckListDTO toDTO(OnboardOrderCheckList checkList) {
        return MapperUtils.map(checkList, OnboardOrderCheckListDTO.class);
    }

    private List<OnboardOrderCheckListDTO> toCheckListDTOs(List<OnboardOrderCheckList> checkLists) {
        return checkLists.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
