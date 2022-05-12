package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCheckListTemplateDTO {
    private Long id;
    private Long orgId;
    private String name; // tên mẫu
    private String code; // mã mẫu
    private Long positionId;
    private Long titleId;
    private Date startDate;
    private Date endDate;
    private Integer status;
    private String createBy;

    private Map<String,Object> createByObj;
    private Map<String, Object> positionObj;
    private OrgResp org;
    private List<InterviewCheckListTemplateItemDTO> items;

}
