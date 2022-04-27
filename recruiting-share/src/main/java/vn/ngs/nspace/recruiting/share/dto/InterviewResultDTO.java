package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterviewResultDTO {
    private Long id;
    private Long candidateId;
    private Date interviewDate;
    private String content;
    private Long interviewerId;
    private Long templateId;
    private Long templateItemId;
    private String state ;
    private String createBy;
    private Integer status;
    private String finalResult;
    private Integer evaluate;
    private List<Map<String,Object>> items; // luu diem danh gia

    private Map<String, Object> createByObj;
    private EmployeeDTO interviewerIdObj;
    private List<InterviewCheckListDTO> checkLists;

}
