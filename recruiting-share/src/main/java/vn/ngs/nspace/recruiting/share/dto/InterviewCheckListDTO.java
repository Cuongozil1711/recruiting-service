package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
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
}
