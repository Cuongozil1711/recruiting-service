package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    private String overall; // điểm tổng trung bình
    private String result; // đạt hay không đạt
    private String content; // nội dung đánh giá
    private Long candidateId;
    private Long templateId;
}
