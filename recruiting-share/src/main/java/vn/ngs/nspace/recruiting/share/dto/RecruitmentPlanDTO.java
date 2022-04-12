package vn.ngs.nspace.recruiting.share.dto;

import lombok.*;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
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
    private String createBy;
    private Date create_date;
    private String sumQuanity;
    private Long sumRecruting;
    private String recruited;
    private Object creatByObj;

    private List<RecruitmentPlanOrderDTO> recruitmentPlanDetails;

}
