package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingTemplateItemChildrenDTO {
    private Long id;
    private Long templateId;
    private Long itemId;
    private String smallGoal;
    private float completion;
    private String description;
    private Integer status;

    private List<OnboardTrainingTemplateItemGrandChildDTO> grandChildItems;
}
