package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CostDTO {
    private Long id;
    private String name;
    private Double expectedCost; // chi phí dự kiến
    private Double cost;
    private Long newsId;
    private String createBy;
    private Date createDate;
    private String unit;
    private Integer status;
}
