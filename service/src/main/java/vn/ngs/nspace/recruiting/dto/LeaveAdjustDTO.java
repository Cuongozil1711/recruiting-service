package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.recruiting.dto.response.OrgResp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LeaveAdjustDTO {
    private Long id;
    private Long formId;
    private Long empId;
    private Date cycleDate;
    private String type;
    private Double amount;
    private Integer status;
    private Long orgId;
    private String description;

    private OrgResp org;
    private EmployeeDTO employeeDTO;
}

