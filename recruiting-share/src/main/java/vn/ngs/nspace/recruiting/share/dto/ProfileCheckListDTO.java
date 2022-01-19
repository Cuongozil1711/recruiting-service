package vn.ngs.nspace.recruiting.share.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfileCheckListDTO {
    private Long id;
    private Long checklistId; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;
    private Long positionId;
    private Long titleId;
    private String contractType;
}
