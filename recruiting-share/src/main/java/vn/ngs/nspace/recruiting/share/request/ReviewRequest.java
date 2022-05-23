package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;

import java.util.List;

@Getter
@Setter
public class ReviewRequest {
    private List<InterviewResultDTO> interviewResultDTOS;
    private Long candidateId;
    private String content;
    private String finalResult;
}
