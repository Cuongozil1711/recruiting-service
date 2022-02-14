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
    private String createBy;
    private Date createDate;
    private Long orgId;
    private Long costTypeId;
    private Long quantity;
    private String unit;
    private Double price;
    private Double totalAmount;
    private Long year;
    private Date startDate;
    private Date endDate;
    private Integer status;


    private Map<String, Object> createByObj;
    private Map<String, Object> costTypeObj;
    private List<CostDetailDTO> costDetails;
    private OrgResp org;
}
