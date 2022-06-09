package vn.ngs.nspace.recruiting.share.dto;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentNewsDTO {
    private Long id;
    private String code;
    private String name;
    private Long planId; // mã kế hoạch tuyển dụng
    private Long requestId; // mã yêu cầu
    private String salaryLevel; // mức lương
    private Double salaryFrom; // mức lương từ
    private Double salaryTo; // mức lương đến
    private Long unit; //loại tiền
    private String description; // mô tả công việc
    private String location; // địa điểm làm việc
    private String requirement; // yêu cầu công việc
    private Date deadlineSendCV; // hạn nộp hồ sơ
    private String profit;
    private String fullName; // họ tên
    private String phone;
    private String email;
    private Long positionId;
    private Long titleId;
    private Long levelId;

    private String state;
    private String position;
    private String title;
    private String level;
    private String groupName;
    private Long quantity;
    private String requestCode;
    private String planCode;
    private String orgName;
    private String orgDeptName;
    private String workType;
    private String workArea;
    private String newState;
}
