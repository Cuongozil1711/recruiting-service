package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InterviewTemplateFilterRequest {
    private Long positionId = -1L;
    private Long orgId = -1L;
    private Long titleId = -1L;
}
