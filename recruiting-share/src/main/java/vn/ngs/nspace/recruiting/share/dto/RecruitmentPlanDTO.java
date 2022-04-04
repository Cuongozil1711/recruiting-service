package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentPlanDTO {
    private Long id;
    private String code;
    private String name; //in-plan , out-plan
    private Date startDate;
    private Date endDate;
    private String state;
    private Integer status;

    private List<RecruitmentPlanOrderDTO> recruitmentPlanDetails;

}
