package vn.ngs.nspace.recruiting.service.v2;


import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListTemplateItemV2Service {

    private final InterviewCheckListTemplateItemRepo itemRepo;

    public InterviewCheckListTemplateItemV2Service(InterviewCheckListTemplateItemRepo itemRepo) {
        this.itemRepo = itemRepo;
    }

    public InterviewCheckListTemplateItemDTO getById(Long cid, String uid, Long id) {
        InterviewCheckListTemplateItem checkListTemplateItem = itemRepo.findById(id).orElseThrow(() -> new EntityNotFoundException(InterviewCheckListTemplateItem.class, id));

        return toDTO(checkListTemplateItem);
    }

    public List<InterviewCheckListTemplateItemDTO> getAllByTemplateId(Long cid, Long templateId) {
        List<InterviewCheckListTemplateItem> templateItems = itemRepo.findByTemplateId(cid, templateId);

        return toDTOs(templateItems);
    }

    public InterviewCheckListTemplateItemDTO toDTO(InterviewCheckListTemplateItem checkListTemplateItem) {
        return MapperUtils.map(checkListTemplateItem, InterviewCheckListTemplateItemDTO.class);
    }

    public List<InterviewCheckListTemplateItemDTO> toDTOs(List<InterviewCheckListTemplateItem> templateItems) {
        return templateItems.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
