package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeDTO {
    private Long id;
    private String empNo;
    private String type;
    private String state;
    private String fullName;
    private String avatar;
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Date endProbationaryDate;
    private Date startDate;
    private Date endDate;
    private String workEmail;
    private String taxId;
    private String taxIssueDate;
    private Long bankId;
    private String bankBranch;
    private String cardNo;
    private Long managerId;
    private Integer status;
}
