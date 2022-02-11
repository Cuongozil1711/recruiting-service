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
    private String state ;

    private EmployeeDTO interviewerIdObj;


}
