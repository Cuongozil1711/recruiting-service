package vn.ngs.nspace.recruiting.share.dto;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentRequestDTO {
    private Long id;
    private Long orgId;
    private String orgName;
    private Long titleId;
    private String tittleName;
    private Long positionId;
    private String positionName;
    private Long levelId;
    private String levelName;
    private Long pic; // employeeId
    private String type; //trong kế hoạch ngoài kế hoạch
    private String typeRequest; //loại yêu cầu
    private Integer quantity;
    private String name;
    //// TODO: new
    private String code;
    private Long orgDeptId;
    private String orgDeptName;
    private Long groupId;
    private String groupName;
    private Long contractTypeId;
    private String contractTypeName;
    private Long recruitmentPlanId;
    private Date startDate;
    private Date endDate;
    private String workType;
    private String workArea;
    private Long salaryType;
    private String salaryTypeName;
    private Long fromSalary;
    private Long toSalary;
    private Long currencyUnit;
    private String currencyUnitName;
    private Long gender;
    private String genderName;
    private Long degree;
    private String degreeName;
    private Integer fromAge;
    private Integer toAge;
    private String jobDescription; // mô tả công việc
    private String otherRequirement; // yêu cầu
    private String benefit; // quyền lợi
    private String createBy;
    private String state;
    private Integer dateRequestMonth;
    private Integer dateRequestYear;
    private Long demarcationId;
    private List<RecruitmentResponseDemarcationDTO> recruitmentResponseDemarcationDTOS; // record định biên tương ứng với phiếu
}
