package vn.ngs.nspace.recruiting.share.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentResponseDemarcationDTO {
    private Long id;
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Integer dateDemarcationMonth;
    private Integer dateDemarcationYear;
    private Integer sumDemarcationMissing;
    private Integer quantity;
}
