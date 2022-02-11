package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingTemplateItemGrandChildDTO {
    private Long id;
    private String key;
    private Long templateId;
    private Long itemId;
    private Long itemChildrenId;
    private String name;
    private Integer deadline;
    private String description;
    private float completion;
    private Long employeeId;
    private Integer status;

    private EmployeeDTO employeeObj;
}
