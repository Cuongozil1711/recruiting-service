package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
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
    private final InterviewCheckListTemplateV2Service templateV2Service;

    public InterviewResultV2Service(@Lazy InterviewResultRepo resultRepo, @Lazy InterviewCheckListTemplateV2Service templateV2Service) {
        this.resultRepo = resultRepo;
        this.templateV2Service = templateV2Service;
    }

    public InterviewResultDTO getByInterviewResultId(Long cid, String uid, Long interviewResultId) {
        InterviewResult interviewResult = resultRepo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));

        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = templateV2Service.getById(cid, uid, interviewResult.getTemplateCheckListId());
        InterviewResultDTO interviewResultDTO = toDTO(interviewResult);
        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);

        return interviewResultDTO;
    }

    public InterviewResultDTO getByInterviewResultIdAndCandidateId(Long cid, String uid, Long interviewResultId, Long candidateId) {
        InterviewResult interviewResult = resultRepo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));

        //InterviewResult result = resultRepo.getByCandidateAndComAndCompanyId(cid, candidateId, interviewResult.getTemplateCheckListId());
        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = templateV2Service.getById(cid, uid, interviewResult.getTemplateCheckListId());
        InterviewResultDTO interviewResultDTO = toDTO(interviewResult);
        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);

        return interviewResultDTO;
    }

    // danh sách lịch sử đánh giá
    public List<InterviewResultDTO> getByCandidateId(Long cid, String uid, Long candidateId) {
        List<InterviewResult> interviewResults = resultRepo.findAllByCandidateIdAndCompanyIdAndStatus(candidateId, cid, 1);

        return toDTOs(interviewResults);
    }

    public InterviewResultDTO updateResult(Long cid, String uid, ReviewRequest request) {
        List<InterviewResultDTO> resultDTOS = request.getInterviewResultDTOS();

        resultDTOS.forEach(
                e -> {
                    InterviewResult result = resultRepo.getByCandidateAndCompanyId(cid, request.getCandidateId(), e.getTemplateId());
                            if(result == null) throw  new EntityNotFoundException(InterviewResult.class, e.getId());

                    MapperUtils.copyWithoutAudit(request, result);

                    result.setUpdateBy(uid);
                    result.setContent(request.getContent());
                    if (request.getFinalResult() != null) {
                        result.setFinalResult(request.getFinalResult());
                    }

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
