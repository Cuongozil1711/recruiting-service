package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListTemplateV2Service {
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewResultV2Service resultV2Service;
    private final InterviewCheckListTemplateItemV2Service itemV2Service;

    public InterviewCheckListTemplateV2Service(InterviewCheckListTemplateRepo templateRepo, InterviewResultV2Service resultV2Service, InterviewCheckListTemplateItemV2Service itemV2Service, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateItemRepo itemRepo1) {
        this.templateRepo = templateRepo;
        this.resultV2Service = resultV2Service;
        this.itemV2Service = itemV2Service;
        this.itemRepo = itemRepo1;
    }

    public InterviewCheckListTemplateDTO update(Long cid, String uid, InterviewCheckListTemplateDTO dto) {
        InterviewCheckListTemplate template = templateRepo.findById(dto.getId())
                .orElseThrow(EntityNotFoundException::new);

        MapperUtils.copyWithoutAudit(dto, template);
        template.setUpdateBy(uid);

        if (dto.getItems() != null) {
            findAndUpdateListItem(cid, uid, dto.getItems().stream().filter(e -> e.getId() != null).collect(Collectors.toList()), dto.getId());
            dto.getItems().stream().filter(e->e.getId() == null).forEach(e->createItem(cid, uid,e));
        }

        return toDTO(templateRepo.save(template));
    }

    private void findAndUpdateListItem(Long cid, String uid, List<InterviewCheckListTemplateItemDTO> itemIdDTOs, Long templateId) {
        List<InterviewCheckListTemplateItem> templateItems = itemRepo.findByCompanyIdAndTemplateId(cid, templateId);

        for (InterviewCheckListTemplateItem templateItem : templateItems) {
            for (InterviewCheckListTemplateItemDTO dto : itemIdDTOs) {
                if (Objects.equals(dto.getId(), templateItem.getId())) {
                    MapperUtils.copyWithoutAudit(dto, templateItem);
                    templateItem.setUpdateBy(uid);
                    itemRepo.save(templateItem);
                } else {
                    templateItem.setStatus(Constants.ENTITY_INACTIVE);
                    templateItem.setUpdateBy(uid);
                    itemRepo.save(templateItem);
                }
            }
        }
    }

    private void createItem(long cid, String uid, InterviewCheckListTemplateItemDTO itemDTO) {
        InterviewCheckListTemplateItem item = InterviewCheckListTemplateItem.of(cid, uid, itemDTO);
        item.setCompanyId(cid);
        item.setCreateBy(uid);
        item.setUpdateBy(uid);
        item.setStatus(Constants.ENTITY_ACTIVE);

        item = itemRepo.save(item);
    }

    public InterviewCheckListTemplateDTO getByInterviewResult(Long cid, String uid, Long interviewResult) {
        InterviewResultDTO resultDTO = resultV2Service.getByInterviewResultId(cid, uid, interviewResult);

        InterviewCheckListTemplate template = templateRepo.findById(resultDTO.getTemplateId()).orElseThrow(() -> new EntityNotFoundException(InterviewCheckListTemplate.class, resultDTO.getTemplateId()));

        return toDTO(template);
    }

    public InterviewCheckListTemplateDTO getByInterviewResultAndCandidateId(Long cid, String uid, Long interviewResult) {
        InterviewResultDTO resultDTO = resultV2Service.getByInterviewResultId(cid, uid, interviewResult);

        InterviewCheckListTemplate template = templateRepo.findById(resultDTO.getTemplateId()).orElseThrow(() -> new EntityNotFoundException(InterviewCheckListTemplate.class, resultDTO.getTemplateId()));

        return toDTO(template);
    }

    public InterviewCheckListTemplateDTO toDTO(InterviewCheckListTemplate checkListTemplate) {
        List<InterviewCheckListTemplateItemDTO> templateItemDTOS = new ArrayList<>();

        templateItemDTOS = itemV2Service.getAllByTemplateId(checkListTemplate.getId(), checkListTemplate.getId());
        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = MapperUtils.map(checkListTemplate, InterviewCheckListTemplateDTO.class);
        interviewCheckListTemplateDTO.setItems(templateItemDTOS);

        return interviewCheckListTemplateDTO;
    }

    public List<InterviewCheckListTemplateDTO> toDTOs(List<InterviewCheckListTemplate> templates) {
        return templates.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
