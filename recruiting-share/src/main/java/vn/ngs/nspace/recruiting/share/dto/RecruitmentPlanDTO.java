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
    private String name;
    private Date startDate;
    private Date endDate;
    private String state;
    private Integer status;
    private String createBy;
    private Date create_date;
    private Object creatByObj;
    private List<RecruitmentPlanRequestDTO> requestDTOS;

}
