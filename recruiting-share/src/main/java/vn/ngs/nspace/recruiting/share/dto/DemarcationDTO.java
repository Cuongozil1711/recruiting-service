package vn.ngs.nspace.recruiting.share.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DemarcationDTO {
    private Long id;
    private String name;
    private Long orgId;
    private Long levelId;
    private Long titleId;
    private Long positionId;
    private Date demarcationDate;
    private Integer sumDemarcation;
}
