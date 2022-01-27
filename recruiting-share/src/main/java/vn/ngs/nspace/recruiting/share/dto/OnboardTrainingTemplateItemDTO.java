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
public class OnboardTrainingTemplateItemDTO {
    private Long id;
    private Long templateId;
    private String bigGoal;
    private String smallGoal;
    private String conditions;
    private Date deadline;
    private float completion;
    private String description;
    private Long employeeId;

    private EmployeeDTO employeeObj;
}
