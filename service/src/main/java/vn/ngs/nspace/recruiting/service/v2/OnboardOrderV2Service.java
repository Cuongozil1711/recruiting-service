package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardOrderV2Service {

    private final OnboardOrderRepo onboardOrderRepo;

    public OnboardOrderV2Service(OnboardOrderRepo onboardOrderRepo) {
        this.onboardOrderRepo = onboardOrderRepo;
    }

    protected OnboardOrderDTO create(Long cid, String uid, OnboardOrderDTO request) {
        OnboardOrder onboardOrder = OnboardOrder.of(cid, uid, request);

        onboardOrder.setCreateBy(uid);
        onboardOrder.setStatus(Constants.ENTITY_ACTIVE);

        return toDTO(onboardOrderRepo.save(onboardOrder));
    }

    private OnboardOrderDTO toDTO(OnboardOrder order) {
        return MapperUtils.map(order, OnboardOrderDTO.class);
    }

    private List<OnboardOrderDTO> toDTOs(List<OnboardOrder> orders) {
        return orders.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
