package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.request.EmployeeReq;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRecruitingReq extends EmployeeReq {
    private CandidateDTO candicate;
}
