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

        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = templateV2Service.getByInterviewResult(cid, uid, interviewResultId);
        InterviewResultDTO interviewResultDTO = toDTO(interviewResult);
        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);

        return interviewResultDTO;
    }

    public InterviewResultDTO getByInterviewResultIdAndCandidateId(Long cid, String uid, Long interviewResultId, Long candidateId) {
        InterviewResult interviewResult = resultRepo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));

        InterviewResult result = resultRepo.findByCandidateIdAndCompanyIdAndTemplateCheckListIdAndStatus(candidateId, cid, interviewResult.getTemplateCheckListId(), 1)
                .orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, candidateId));
        InterviewCheckListTemplateDTO interviewCheckListTemplateDTO = templateV2Service.getByInterviewResult(cid, uid, interviewResultId);
        InterviewResultDTO interviewResultDTO = toDTO(result);
        interviewResultDTO.setTemplateDTO(interviewCheckListTemplateDTO);

        return interviewResultDTO;
    }

    public InterviewResultDTO updateResult(Long cid, String uid, ReviewRequest request) {
        InterviewResult interviewResult = resultRepo.findByCandidateIdAndCompanyIdAndTemplateCheckListIdAndStatus(request.getCandidateId(), cid, request.getTemplateId(), 1)
                .orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, request.getCandidateId()));

        interviewResult.setUpdateBy(uid);
        interviewResult.setFinalResult(request.getOverall());
        interviewResult.setState(request.getResult());
        interviewResult.setContent(request.getContent());

        resultRepo.save(interviewResult);

        return toDTO(interviewResult);
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
