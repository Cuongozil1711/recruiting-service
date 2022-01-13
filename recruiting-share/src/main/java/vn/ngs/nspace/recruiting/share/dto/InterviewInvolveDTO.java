package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterviewInvolveDTO {
    private Long id;
    private Long interviewId;
    private Long orgId;
    private Long positionId;
    private Long titleId;
    private List<String> interviewerId;
    private Long supporterId; //empId

    private Integer status;

    private List<EmployeeDTO> interviewerObj;
    private EmployeeDTO supporterObj;
    private OrgResp org;
    private Map<String, Object> positionObj;
    private Map<String,Object> titleObj;
}
