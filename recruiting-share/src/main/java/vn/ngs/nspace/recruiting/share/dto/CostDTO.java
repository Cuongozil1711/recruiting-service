package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CostDTO {
    private Long id;
    private String createBy;
    private Date createDate;
    private Long orgId;
    private Long costTypeId;
    private Long quantity;
    private String unit;
    private Double price;
    private Double totalAmount;
    private Date startDate;
    private Date endDate;

    private Map<String, Object> createByObj;
    private List<CostDetailDTO> costDetails;
}
