package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AssetCheckListDTO {
    private Long id;
    private Long onboardOrderId;
    private Long assetId; //dm dung chung
    private String assetType; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // id nguoi giao
    private Long employeeId; // id  nguoi nhan
    private String description;

    private EmployeeDTO senderObj; // doi tuong nguoi giao
    private EmployeeDTO employeeObj; // doi tuong nguoi nhan
    private Map<String, Object> assetObj; // doi tuong tai san
    private String state;
    private Integer status;

    private String cmd; // constants DELETE, UPDATE ...
}
