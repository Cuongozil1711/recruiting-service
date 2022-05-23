package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

/**
 * lọc màn danh sách ứng viên theo thủ tục
 */

@Getter
@Setter
public class OnboardCandidateFilter {
    private Long orgId;
    private Long positionId;
    private Long titleId;
}
