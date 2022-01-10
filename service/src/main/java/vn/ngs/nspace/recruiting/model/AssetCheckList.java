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
    private Long assetId; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;

    public static AssetCheckList of(Long cid, String uid, AssetCheckListDTO dto) {
        AssetCheckList assetCheckList = AssetCheckList.builder().build();
        MapperUtils.map(dto, assetCheckList);
        assetCheckList.setCompanyId(cid);
        assetCheckList.setUpdateBy(uid);
        assetCheckList.setCreateBy(uid);
        return assetCheckList;
    }
}
