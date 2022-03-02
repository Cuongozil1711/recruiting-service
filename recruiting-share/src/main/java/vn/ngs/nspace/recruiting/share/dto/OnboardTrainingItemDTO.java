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
public class OnboardTrainingItemDTO {
    private Long id;
    private Long onboardTrainingId;
    private String result;
    private float evaluate;
    private String finalResult;
    private String selfAssessment;
    private String commentCBQL;
    private String commentHr;
    private Long itemId;
    private Long itemChildId;
    private Long itemGrandChildId;
    private String sourceTL;
    private Integer status;

    private OnboardTrainingTemplateItemDTO item;
    private OnboardTrainingTemplateItemChildrenDTO itemChildren;
    private OnboardTrainingTemplateItemGrandChildDTO itemGrandChild;
}
