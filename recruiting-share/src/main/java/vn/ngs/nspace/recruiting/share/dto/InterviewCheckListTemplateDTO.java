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
    private Long positionId;
    private Date startDate;
    private Date endDate;
    private Integer status;

    private Map<String, Object> positionObj;
    private OrgResp org;
    private List<InterviewCheckListTemplateItemDTO> items;

}
