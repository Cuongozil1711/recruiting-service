package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

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

    private EmployeeDTO employeeObj;
    private EmployeeDTO buddyObj;
}
