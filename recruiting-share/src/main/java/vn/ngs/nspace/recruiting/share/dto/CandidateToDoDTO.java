package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateToDoDTO {
    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date deadline;
    private Long candidateId;
    private Long responsibleId;
    private String state;

    private EmployeeDTO responsibleIdObj;
    private CandidateDTO fullname;
}
