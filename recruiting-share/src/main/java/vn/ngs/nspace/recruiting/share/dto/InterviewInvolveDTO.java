package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterviewInvolveDTO {
    private Long id;
    private Long interviewerLastId;
    private Long orgId;
    private Long positionId;
    private Long titleId;
    private Long levelId;
    private Long roomId;
    private Long groupId;
    private String code;
    private String name;
    private List<String> interviewerId;
    private Long supporterId; //empId
    private List<Map<String, Object>> interviewDescription;
    private Integer status;

    private List<EmployeeDTO> interviewerObj;
    private EmployeeDTO supporterObj;
    private OrgResp org;
    private Map<String, Object> positionObj;
    private Map<String,Object> titleObj;
    private Date createDate;
}
