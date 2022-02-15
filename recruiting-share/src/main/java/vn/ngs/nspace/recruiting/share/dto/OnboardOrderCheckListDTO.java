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
public class OnboardOrderCheckListDTO {
    private Long id;
    private Long employeeId; // nhan vien he thong
    private String code; // ma thu tuc
    private Long onboardOrderId; // id cua ho so xin viec

    private Date deadline;
    private Long responsibleId; // id nhan vien chiu trach nhiem
    private Long participantId; // id nhan vien lien quan
    private Date startDate; // id nhan vien lien quan
    private String state;
    private String description;
    private Integer status;

    private EmployeeDTO employeeObj;
    private EmployeeDTO responsibleObj;
    private EmployeeDTO participantObj;
}
