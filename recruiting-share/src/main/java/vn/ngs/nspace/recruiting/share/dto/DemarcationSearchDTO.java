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
public class DemarcationSearchDTO {
    private Long orgId;
    private Long levelId;
    private Long titleId;
    private Long positionId;
    private Integer dateDemarcation; // dinh dang la tim theo nam
}
