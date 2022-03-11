package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingTemplateItemDTO {
    private Long id;
    private String key;
    private Long templateId;
    private String name;
    private Integer deadline;
    private float completion;
    private String description;
    private Integer status;
    private Long employeeId;

    private EmployeeDTO employeeObj;
    private List<OnboardTrainingTemplateItemChildrenDTO> children;

}
