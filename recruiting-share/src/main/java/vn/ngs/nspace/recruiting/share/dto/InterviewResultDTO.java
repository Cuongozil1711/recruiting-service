package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InterviewResultDTO {
    private Long id;
    private Long interviewerId;
    private Long candidateId;
    private Date interviewDate;
    private String content;
    private Integer state;

    private EmployeeDTO interviewerIdObj;



}
