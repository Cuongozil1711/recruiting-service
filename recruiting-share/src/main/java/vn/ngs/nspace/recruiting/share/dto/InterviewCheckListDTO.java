package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCheckListDTO {
    private Long id;
    private Long interviewResultId;
    private Long checkListId; // tu interview check list
    private Long interviewerId;
    private Double rating;
    private String result;
    private Date interviewDate;
    private Long positionId;
    private Long orgId;
    private Long itemId;
    private Integer status;

    private EmployeeDTO interviewerObj;
    private Map<String, Object> checkListObj;
    private List<InterviewCheckListTemplateItemDTO> items;
}
