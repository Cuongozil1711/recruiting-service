package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

/**
 * lọc màn danh sách ứng viên theo thủ tục
 */

@Getter
@Setter
public class OnboardCandidateFilter {
    private Long orgId; // tổ chức
    private Long positionId; // vị trí
    private Long titleId; // chức vụ
    private String stateCandidate; // trạng thái ứng viên
    private String stateOnboard; // trạng thái công việc

}
