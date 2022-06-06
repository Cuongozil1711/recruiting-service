package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruitmentRequestDTO {
    private Long id;
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long pic; // employeeId
    private String type; //trong kế hoạch ngoài kế hoạch
    private Integer quantity;

    private String code;
    private Long orgDeptId;
    private Long groupId;
    private Long contractTypeId;
    private Long recruitmentPlanId;
    private Date startDate;
    private Date endDate;
    private String workType;
    private String workArea;
    private Long salaryType;
    private Long fromSalary;
    private Long toSalary;
    private Long currencyUnit;
    private Long gender;
    private Long degree;
    private Integer fromAge;
    private Integer toAge;
    private String otherRequirement;
}
