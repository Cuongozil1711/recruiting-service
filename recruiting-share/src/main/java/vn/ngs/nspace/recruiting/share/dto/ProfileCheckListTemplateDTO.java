package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfileCheckListTemplateDTO {
    private Long id;
    private String name;
    private Long positionId;
    private Long titleId;
    private String contractType;
    private Date startDate;
    private Date endDate;
    private Integer status;
    private Integer numbers;
    private Long recieverId;
    private String note;


    private Map<String, Object> contractTypeObj;
    private Map<String, Object> positionObj;
    private Map<String, Object> titleObj;
    private List<ProfileCheckListTemplateItemDTO> items;

}
