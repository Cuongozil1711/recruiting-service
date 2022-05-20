package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnboardCheckListV2Service {

    private final OnboardOrderCheckListRepo checkListRepo;

    public OnboardCheckListV2Service(OnboardOrderCheckListRepo checkListRepo) {
        this.checkListRepo = checkListRepo;
    }

    public Page<OnboardOrderCheckListDTO> getPage(Long cid, String uid, String search, Pageable pageable) {
        Page<OnboardOrderCheckList> onboardOrderCheckLists = checkListRepo.getPageOnboard(cid, search, pageable);
        List<OnboardOrderCheckListDTO> onboardOrderCheckListDTOS = toDTOs(onboardOrderCheckLists.getContent());

        return new PageImpl<>(onboardOrderCheckListDTOS, onboardOrderCheckLists.getPageable(), onboardOrderCheckLists.getTotalElements());
    }

    public OnboardOrderCheckListDTO create(Long cid, String uid, OnboardOrderCheckListDTO dto) {
        OnboardOrderCheckList checkList = OnboardOrderCheckList.of(cid, uid, dto);

        checkList.setCreateBy(uid);
        checkList.setStatus(Constants.ENTITY_ACTIVE);
        return toDTO(checkListRepo.save(checkList));
    }

    public OnboardOrderCheckListDTO update(Long cid, Long id, String uid, OnboardOrderCheckListDTO dto) {
        OnboardOrderCheckList orderCheckList = checkListRepo.findByCompanyIdAndId(cid, id)
                .orElseThrow(() -> new EntityNotFoundException(OnboardOrderCheckList.class, id));

        MapperUtils.copyWithoutAudit(dto, orderCheckList);
        orderCheckList.setUpdateBy(uid);

        return toDTO(checkListRepo.save(orderCheckList));
    }

    public OnboardOrderCheckListDTO getById(Long cid, Long id, String uid) {
        OnboardOrderCheckList orderCheckList = checkListRepo.findByCompanyIdAndId(cid, id)
                .orElseThrow(() -> new EntityNotFoundException(OnboardOrderCheckList.class, id));

        return toDTO(orderCheckList);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        List<OnboardOrderCheckList> checkLists = checkListRepo.getAllByListId(cid, ids);

        checkLists.forEach(
                e -> {
                    e.setUpdateBy(uid);
                    e.setStatus(Constants.ENTITY_INACTIVE);
                    checkListRepo.save(e);
                }
        );
    }

    private OnboardOrderCheckListDTO toDTO(OnboardOrderCheckList checkList) {
        return MapperUtils.map(checkList, OnboardOrderCheckListDTO.class);
    }

    private List<OnboardOrderCheckListDTO> toDTOs(List<OnboardOrderCheckList> checkLists) {
        return checkLists.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
