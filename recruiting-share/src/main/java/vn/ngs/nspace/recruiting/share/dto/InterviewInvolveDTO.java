package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

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
    private Long interviewerId; //empId
    private Long supporterId; //empId

    private Integer status;

    private EmployeeDTO interviewerObj;
    private EmployeeDTO supporterObj;
    private OrgResp org;
    private Map<String, Object> positionObj;
}
