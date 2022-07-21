package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DemarcationSearchResponseDto {
    private Long orgId;
    private Long levelId;
    private Long titleId;
    private Long positionId;
    private Integer[] sumDemarcationForMonth;
    private Long[] demarcationId;
    private Integer sumDemarcation;
    private Integer dateDemarcationYear;
}
