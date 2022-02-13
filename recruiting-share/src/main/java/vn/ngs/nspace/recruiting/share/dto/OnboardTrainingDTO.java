package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingDTO {
    private Long id;
    private Long onboardOrderId;
    private Long employeeId;
    private Integer status;

    private EmployeeDTO employeeObj;
    private List<OnboardTrainingItemDTO> items;
}
