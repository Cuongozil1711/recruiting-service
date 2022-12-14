package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class AssetCheckList extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long onboardOrderId; // yeu cau onboarding
    private Long assetId; //dm dung chung
    private String assetType; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;
    private Integer quantity;
    private String state;
    

    public static AssetCheckList of(Long cid, String uid, AssetCheckListDTO dto) {
        AssetCheckList assetCheckList = AssetCheckList.builder()
                .id(dto.getId())
                .onboardOrderId(dto.getOnboardOrderId())
                .assetId(dto.getAssetId())
                .assetType(dto.getAssetType())
                .receiptDate(dto.getReceiptDate())
                .senderId(dto.getSenderId())
                .employeeId(dto.getEmployeeId())
                .quantity(dto.getQuantity())
                .description(dto.getDescription())
                .state(dto.getState())
                .build();
        return assetCheckList;
    }
}
