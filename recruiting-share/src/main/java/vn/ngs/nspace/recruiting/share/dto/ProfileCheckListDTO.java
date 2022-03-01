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
public class ProfileCheckListDTO {
    private Long id;
    private Long onboardOrderId;
    private Long checklistId; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;

    private Long positionId;
    private Long titleId;
    private String contractType;

    private EmployeeDTO senderObj; // doi tuong nguoi giao
    private EmployeeDTO employeeObj; // doi tuong nguoi nhan
    private Map<String, Object> checkListObj; // doi tuong ho so
    private List<ProfileCheckListTemplateItemDTO> items;
}
