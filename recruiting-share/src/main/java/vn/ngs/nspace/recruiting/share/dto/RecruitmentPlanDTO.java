package vn.ngs.nspace.recruiting.share.dto;

import lombok.*;
import vn.ngs.nspace.recruiting.share.dto.utils.CustomPage;

import java.util.Date;
import java.util.List;

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
    private Integer sumQuanity; // tổng số lượng cần tuyển
    private Integer sumCandidateRecruited; // số lượng ứng tuyển
    private Integer recruited; // số lượng đã tuyển
    private CustomPage<RecruitmentPlanRequestDTO> requestDTOCustomPage;
    private List<RecruitmentPlanRequestDTO> requestDTOList;
}
