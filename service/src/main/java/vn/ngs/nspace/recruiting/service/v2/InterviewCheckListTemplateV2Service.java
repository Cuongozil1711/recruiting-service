package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListTemplateV2Service {
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewResultV2Service resultV2Service;
    private final InterviewCheckListTemplateItemV2Service itemV2Service;

    public InterviewCheckListTemplateV2Service(InterviewCheckListTemplateRepo templateRepo, InterviewResultV2Service resultV2Service, InterviewCheckListTemplateItemV2Service itemV2Service, InterviewCheckListTemplateItemRepo itemRepo) {
        this.templateRepo = templateRepo;
        this.resultV2Service = resultV2Service;
        this.itemV2Service = itemV2Service;
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
