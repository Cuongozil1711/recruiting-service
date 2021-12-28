package vn.ngs.nspace.recruiting.dto;

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
public class ContractDTO {
    private Long id;
    private Double expire;
    private String contractNo;
    private Long empId;
    private Long orgId;
    private Date startDate;
    private Date endDate;
    private String type;
    private Long titleId;
    private Long positionId;
    private List<Map<String, Object>> attachments;

}
