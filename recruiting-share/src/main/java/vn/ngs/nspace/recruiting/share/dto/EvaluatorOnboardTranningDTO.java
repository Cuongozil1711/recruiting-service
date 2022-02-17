package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EvaluatorOnboardTranningDTO {
    private Long id;
    private Long onboardOrderId;
    private Long onboardTraningId;
    private Long leaderId;
    private Long evaluatorId;
    private Long hrId;
    private String leaderComment;
    private String evaluatorCommnet;
    private String hrComment;
    private String ideaLeader;
    private String idealHr;
    private Integer status;

    private EmployeeDTO objEvaluator;
    private EmployeeDTO objLeader;
    private EmployeeDTO objHR;
}
