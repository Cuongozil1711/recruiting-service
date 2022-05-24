package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplateItem;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.recruiting.share.request.ReviewRequest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewResultV2Service {

    private final InterviewResultRepo resultRepo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewCheckListTemplateRepo templateRepo;

    public InterviewResultV2Service(@Lazy InterviewResultRepo resultRepo, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateRepo templateRepo) {
        this.resultRepo = resultRepo;
        this.itemRepo = itemRepo;
        this.templateRepo = templateRepo;
    }

//    public InterviewResultDTO getByInterviewResultId(Long cid, String uid, Long interviewResultId) {
//        InterviewResult interviewResult = resultRepo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));
//
//        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = templateV2Service.getById(cid, uid, interviewResult.getTemplateCheckListId());
//        InterviewResultDTO interviewResultDTO = toDTO(interviewResult);
//        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);
//
//        return interviewResultDTO;
//    }

    public InterviewResultDTO getByInterviewResultIdAndCandidateId(Long cid, String uid, Long interviewResultId, Long candidateId) {
        InterviewResult interviewResult = resultRepo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));

        InterviewCheckListTemplateItem templateItem = itemRepo.findById( interviewResult.getTemplateCheckListId())
                .orElseThrow(() -> new EntityNotFoundException(InterviewCheckListTemplateItem.class, interviewResult.getTemplateCheckListId()));

        InterviewCheckListTemplate interviewCheckListTemplate = templateRepo.getOne(templateItem.getCheckListId());

        List<InterviewCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid,interviewCheckListTemplate.getId());
        List<InterviewCheckListTemplateItemDTO> itemDTOS = items.stream().map(e -> MapperUtils.map(e,InterviewCheckListTemplateItemDTO.class)).collect(Collectors.toList());

        InterviewResultDTO interviewResultDTO = toDTO(interviewResult);
        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = MapperUtils.map(interviewCheckListTemplate,InterviewCheckListTemplateDTO.class);
        interviewCheckListTemplateDTO.setItems(itemDTOS);

        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);

        return interviewResultDTO;
    }

    // danh sách lịch sử đánh giá
    public List<InterviewResultDTO> getByCandidateId(Long cid, String uid, Long candidateId) {
        List<InterviewResult> interviewResults = resultRepo.findAllByCandidateIdAndCompanyIdAndStatus(candidateId, cid, 1);

        return toDTOs(interviewResults);
    }

    public InterviewResultDTO updateResult(Long cid, String uid, ReviewRequest request) {
        List<ReviewRequest.ResultItem> resultDTOS = request.getResultItems();

        resultDTOS.forEach(
                e -> {
                    InterviewResult result = resultRepo.getByCandidateAndTemplate(cid, request.getCandidateId(), e.getId());
                            if(result == null) throw  new EntityNotFoundException(InterviewResult.class, e.getId());

                    MapperUtils.copyWithoutAudit(request, result);

                    result.setUpdateBy(uid);
                    result.setContent(request.getContent());
                    if (request.getFinalResult() != null) {
                        result.setState(request.getFinalResult());
                    }
                    result.setFinalResult(e.getResult().toString());

                    resultRepo.save((result));
                }
        );
        return null;
    }

    // lấy danh sách người đánh giá ứng viên
    public List<Long> getAllInterviewer(Long cid, String uid, Long candidateId) {
        List<InterviewResult> interviewResults = resultRepo.findAllByCandidateIdAndCompanyIdAndStatus(candidateId, cid, 1);

        return interviewResults.isEmpty() ? new ArrayList<>() : interviewResults.stream().map(InterviewResult::getInterviewerId).collect(Collectors.toList());
    }

    public InterviewResultDTO toDTO(InterviewResult interviewResult) {
        return MapperUtils.map(interviewResult, InterviewResultDTO.class);
    }

    public List<InterviewResultDTO> toDTOs(List<InterviewResult> interviewResults) {
        return interviewResults.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
