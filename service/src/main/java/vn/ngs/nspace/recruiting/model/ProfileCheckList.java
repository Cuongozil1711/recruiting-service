package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Danh sách hồ sơ cần hoàn thiện trong quá trình onboard
public class ProfileCheckList extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long checklistId; //dm dung chung
    private Date receiptDate; // ngay nhan
    private Long senderId; // nguoi giao
    private Long employeeId; // nguoi nhan
    private String description;

    public static ProfileCheckList of(Long cid, String uid, ProfileCheckListDTO dto){
        ProfileCheckList obj = ProfileCheckList.builder()
                .id(dto.getId())
                .checklistId(dto.getChecklistId())
                .receiptDate(dto.getReceiptDate())
                .senderId(dto.getSenderId())
                .employeeId(dto.getEmployeeId())
                .description(dto.getDescription())
                .build();
        return obj;
    }
}
