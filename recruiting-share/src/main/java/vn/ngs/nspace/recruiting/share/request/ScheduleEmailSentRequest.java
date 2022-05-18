package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class ScheduleEmailSentRequest {
    private Long templateId; // mẫu mail
    private Long emailSettingId; // mẫu config setting
    private List<Long> candidateIds; // danh sách ứng viên
    private List<String> CandidateMails; // danh dách mail người nhận
    private List<String> InvolveMails; // danh dách mail người nhận
    private String title; // tiêu đề
    private String content; // nội dung
    private Date interviewDate; // ngày phỏng vấn
    private Long interviewInvolveId; // mã hội đồng đánh giá
    private Long scheduleTypeId; // loại phỏng vấn
    private Long templateCheckList; // mẫu đánh giá
    private Date scheduleDate; // ngày đặt lịch
    private boolean sentCandidate; // có gửi cho ứng viên hay không
    private boolean sentInvolve; // có gửi cho hội đồng hay không
}
