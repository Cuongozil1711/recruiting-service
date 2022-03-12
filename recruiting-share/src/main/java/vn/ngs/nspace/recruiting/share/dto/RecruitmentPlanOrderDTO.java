package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentPlanOrderDTO {
    private Long id;
    private String code;
    private String type; //in-plan , out-plan
    private String solutionSuggestType; //in-company, out
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long pic; // employeeId
    private Long supporterId; // employeeId
    private Long quantity;
    private String businessAddition;
    private Date startDate;
    private Date deadline;
    private Date escalateDate;
    private Long reasonId;
    private String description;
    private String state;
    private OrgResp orgResp;
    private EmployeeDTO picObj;
    private EmployeeDTO supporterObj;
    private Map<String, Object> positionObj;
    private Map<String, Object> titleObj;
    private Map<String, Object> levelObj;
    private Integer totalRecruit;
    private Integer recruited;
    private Integer totalMissing;
    private Integer status;
}
