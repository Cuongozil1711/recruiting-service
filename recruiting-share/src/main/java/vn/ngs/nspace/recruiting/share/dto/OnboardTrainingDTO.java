package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingDTO {
    private Long id;
    private Long onboardOrderId;
    private Long employeeId;
    private String result;
    private float evaluate;
    private String finalResult;
    private String selfAssessment;
    private String commentCBQL;
    private String commentHr;
}
