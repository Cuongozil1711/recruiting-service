package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AssetCheckListDTO {
    private Long id;
    private Long assetId; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;
}
