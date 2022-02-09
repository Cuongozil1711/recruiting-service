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
    private Long templateId;
    private String bigGoal;
    private float completion;
    private String description;
    private Integer status;

    private List<OnboardTrainingTemplateItemChildrenDTO> childrenItems;

}