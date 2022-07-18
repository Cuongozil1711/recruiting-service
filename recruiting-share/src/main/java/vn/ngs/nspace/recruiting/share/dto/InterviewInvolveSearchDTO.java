package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterviewInvolveSearchDTO {
    private Long interviewId;
    private String interviewerId;
    private Long supporterId;
    private Long orgId;
    private Long positionId;
    private Long titleId;
    private Long roomId;
    private Long levelId;
    private Long groupId;
    private String search;
}
