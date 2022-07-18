package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.InterviewTemplateFilterRequest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListTemplateV2Service {
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewResultRepo resultRepo;
    private final InterviewCheckListTemplateItemV2Service itemV2Service;

    public InterviewCheckListTemplateV2Service(InterviewCheckListTemplateRepo templateRepo, InterviewResultRepo resultRepo, InterviewCheckListTemplateItemV2Service itemV2Service, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateItemRepo itemRepo1) {
        this.templateRepo = templateRepo;
        this.resultRepo = resultRepo;
        this.itemV2Service = itemV2Service;
        this.itemRepo = itemRepo1;
    }

    public InterviewCheckListTemplateDTO create(Long cid, String uid, InterviewCheckListTemplateDTO dto) {
        InterviewCheckListTemplate template = InterviewCheckListTemplate.of(cid, uid, dto);
        template.setStartDate(new Date());
        template.setCompanyId(cid);
        template.setCreateBy(uid);
        template.setUpdateBy(uid);
        template.setStatus(Constants.ENTITY_ACTIVE);

        template = templateRepo.save(template);

        if (dto.getItems() != null) {
            Long templateId = template.getId();
            dto.getItems().forEach(e->createItem(cid, uid,e, templateId));
        }

        return toDTO(template);
    }

    public InterviewCheckListTemplateDTO getById(Long cid, String uid, Long id) {
        InterviewCheckListTemplate template = templateRepo.findByCompanyIdAndId(cid,id)
                .orElseThrow(EntityNotFoundException::new);

//        List<InterviewCheckListTemplateItemDTO> items = itemV2Service.getAllByTemplateId(cid,template.getId());

        InterviewCheckListTemplateDTO dto = toDTO(template);
        return dto;
    }

    public Page<InterviewCheckListTemplateDTO> getPage(Long cid, InterviewTemplateFilterRequest request, Pageable pageable) {
        Page<InterviewCheckListTemplate> templates = templateRepo.search(cid, request.getPositionId(), request.getOrgId() ,request.getTitleId(),pageable);

        List<InterviewCheckListTemplateDTO> dtos = toDTOs(templates.getContent());

        return new PageImpl<>(dtos,templates.getPageable(),templates.getTotalElements());
    }

    public InterviewCheckListTemplateDTO update(Long cid, String uid, InterviewCheckListTemplateDTO dto) {
        InterviewCheckListTemplate template = templateRepo.findById(dto.getId())
                .orElseThrow(EntityNotFoundException::new);

        MapperUtils.copyWithoutAudit(dto, template);
        template.setUpdateBy(uid);

        if (dto.getItems() != null) {
            findAndUpdateListItem(cid, uid, dto.getItems().stream().filter(e -> e.getId() != null).collect(Collectors.toList()), dto.getId());
            dto.getItems().stream().filter(e->e.getId() == null).forEach(e->createItem(cid, uid,e,template.getId()));
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

    private void createItem(long cid, String uid, InterviewCheckListTemplateItemDTO itemDTO, Long templateId) {
        InterviewCheckListTemplateItem item = InterviewCheckListTemplateItem.of(cid, uid, itemDTO);
        item.setCheckListId(templateId);
        item.setTemplateId(templateId);
        item.setCompanyId(cid);
        item.setCreateBy(uid);
        item.setUpdateBy(uid);
        item.setStatus(Constants.ENTITY_ACTIVE);

        item = itemRepo.save(item);
    }

    public InterviewCheckListTemplateDTO toDTO(InterviewCheckListTemplate checkListTemplate) {
        List<InterviewCheckListTemplateItemDTO> templateItemDTOS = new ArrayList<>();

        templateItemDTOS = itemV2Service.getAllByTemplateId(checkListTemplate.getCompanyId(), checkListTemplate.getId());
        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = MapperUtils.map(checkListTemplate, InterviewCheckListTemplateDTO.class);
        interviewCheckListTemplateDTO.setItems(templateItemDTOS);

        return interviewCheckListTemplateDTO;
    }

    public InterviewCheckListTemplateDTO toDTO(InterviewCheckListTemplate checkListTemplate, List<InterviewCheckListTemplateItemDTO> itemDTOList) {
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
