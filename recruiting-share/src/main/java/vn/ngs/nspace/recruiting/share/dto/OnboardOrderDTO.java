package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.ContractDTO;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardOrderDTO {
    private Long id;
    private Long employeeId; // nhan vien he thong
    private Long buddy; // nguoi tiep nhan ho tro chuyen mon
    private Long jobApplicationId; // id cua ho so xin viec
    private Integer status;
    private Long mentorId;
    private String state;

    private Date startDate;

    private String contractType;
    private EmployeeDTO employeeObj;
    private EmployeeDTO buddyObj;
    private EmployeeDTO mentorObj;
    private OrgResp orgResp;
    private Map<String, Object> positionObj;
    private Map<String, Object> titleObj;
}
