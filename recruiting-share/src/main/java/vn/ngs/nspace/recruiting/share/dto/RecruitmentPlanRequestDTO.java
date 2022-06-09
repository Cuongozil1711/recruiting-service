package vn.ngs.nspace.recruiting.share.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RecruitmentPlanRequestDTO {
    private Long id;
    private RecruitmentPlanDTO planDTO;
    private Long recruitmentPlanId;
    private Long requestId;
    private RecruitmentRequestDTO requestDTO;
    private Date deadline;
    private Long picId;
}
