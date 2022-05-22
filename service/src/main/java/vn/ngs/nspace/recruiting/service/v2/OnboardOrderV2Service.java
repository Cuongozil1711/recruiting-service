package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.JobApplicationRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

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

    public OnboardOrderV2Service(OnboardOrderRepo onboardOrderRepo, JobApplicationRepo jobApplicationRepo, OnboardOrderCheckListRepo checkListRepo) {
        this.onboardOrderRepo = onboardOrderRepo;
        this.jobApplicationRepo = jobApplicationRepo;
        this.checkListRepo = checkListRepo;
    }

    protected OnboardOrderDTO create(Long cid, String uid, OnboardOrderDTO request) {
        OnboardOrder onboardOrder = OnboardOrder.of(cid, uid, request);

        onboardOrder.setCreateBy(uid);
        onboardOrder.setStatus(Constants.ENTITY_ACTIVE);

        return toDTO(onboardOrderRepo.save(onboardOrder));
    }

    public List<OnboardOrderDTO> creates(Long cid, String uid, Long candidateId) {
        JobApplication jobApplication = jobApplicationRepo.findByStatusCompanyIdCandidateId(cid, cid);

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

    private OnboardOrderDTO toDTO(OnboardOrder order) {
        return MapperUtils.map(order, OnboardOrderDTO.class);
    }

    private List<OnboardOrderDTO> toDTOs(List<OnboardOrder> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
