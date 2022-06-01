package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.RecruitmentPlanOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;
import vn.ngs.nspace.recruiting.share.request.RequestFilter;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class RecruitmentOrderV2Service {

    private final RecruitmentPlanOrderRepo planOrderRepo;

    public RecruitmentOrderV2Service(RecruitmentPlanOrderRepo planOrderRepo) {
        this.planOrderRepo = planOrderRepo;
    }

    private RecruitmentPlanOrderDTO create(Long cid, String uid, RecruitmentPlanOrderDTO dto) {
        RecruitmentPlanOrder planOrder = RecruitmentPlanOrder.of(cid, uid, dto);
        planOrder = planOrderRepo.save(planOrder);

        return toDTO(planOrder);
    }

    private RecruitmentPlanOrderDTO update(Long cid, String uid, RecruitmentPlanOrderDTO dto) {
        RecruitmentPlanOrder planOrder = planOrderRepo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(RecruitmentPlanOrder.class, dto.getId()));

        MapperUtils.copyWithoutAudit(dto, planOrder);
        planOrderRepo.save(planOrder);

        return toDTO(planOrder);
    }

    private List<RecruitmentPlanOrderDTO> getPage(Long cid, String uid, RequestFilter filter, Pageable pageable) {
//        List<RecruitmentPlanOrder> planOrders = planOrderRepo.getPage();
        return null;
    }

    private RecruitmentPlanOrderDTO toDTO(RecruitmentPlanOrder recruitmentPlanOrder) {
        return MapperUtils.map(recruitmentPlanOrder, RecruitmentPlanOrderDTO.class);
    }

    private List<RecruitmentPlanOrderDTO> toDTOs(List<RecruitmentPlanOrder> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
