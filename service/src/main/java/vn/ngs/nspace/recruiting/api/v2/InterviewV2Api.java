package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.InterviewResultV2Service;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;
import vn.ngs.nspace.recruiting.share.request.ReviewRequest;

import java.util.List;

/**
 * api liên quan đến đánh giá
 * @author toile
 */

@RestController
@RequestMapping("interview-check-list")
@RequiredArgsConstructor
@Tag(name = "Interview Check List", description = "Interview Check List API")
public class InterviewV2Api {

    private final InterviewResultV2Service resultV2Service;

    /**
     * hiển thị đánh giá ứng viên
     * @param cid
     * @param uid
     * @param interviewResultId
     * @param candidateId
     * @return
     */
    @GetMapping("{interviewResultId}/{candidateId}")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getInterviewByResultId(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("interviewResultId") Long interviewResultId
            , @PathVariable("candidateId") Long candidateId
    ) {
        try {
            InterviewResultDTO resultDTO = resultV2Service.getByInterviewResultIdAndCandidateId(cid, uid, interviewResultId, candidateId);

            return ResponseUtils.handlerSuccess(resultDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * đánh giá ứng viên
     * @param cid
     * @param uid
     * @param request
     * @return
     */

    @PostMapping("")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity review(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody ReviewRequest request
            ) {
        try {
            InterviewResultDTO resultDTO = resultV2Service.updateResult(cid, uid, request);

            return ResponseUtils.handlerSuccess(resultDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * lịch sử đánh giá ứng viên
     * @param cid
     * @param uid
     * @param candidateId
     * @return
     */

    @GetMapping("/{candidateId}")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity historyInterview(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable("candidateId") Long candidateId
    ) {
        try {
            List<InterviewResultDTO> resultDTOs = resultV2Service.getByCandidateId(cid, uid,  candidateId);

            return ResponseUtils.handlerSuccess(resultDTOs);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
