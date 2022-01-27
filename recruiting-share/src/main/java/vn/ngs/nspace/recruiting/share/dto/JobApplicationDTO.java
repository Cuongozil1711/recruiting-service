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
public class JobApplicationDTO {
    private Long id;
    private String createBy;
    private Long candidateId;
    private Long positionId;
    private Long titleId;
    private Long employeeId;
    private Long orgId;
    private String contractType;
    private Date onboardDate;
    private String state; // interview, offer, requested, cancelled, done

    private EmployeeDTO employeeObj;
    private CandidateDTO candidateObj;
    private Map<String, Object> positionObj;
    private Map<String, Object> titleObj;
    private Map<String, Object> contractTypeObj;
    private Map<String, Object> createByObj;
    private OrgResp org;

    private Integer status;
}
