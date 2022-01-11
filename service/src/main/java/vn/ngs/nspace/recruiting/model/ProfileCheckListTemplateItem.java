package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateItemDTO;

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
//Danh sách hồ sơ cần hoàn thiện trong quá trình onboard
public class ProfileCheckListTemplateItem extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long checklistId; //dm dung chung
    private Long templateId; // ngay nhan
    private String description; // ngay nhan
    private Boolean required = true;

    public static ProfileCheckListTemplateItem of(Long cid, String uid, ProfileCheckListTemplateItemDTO dto){
        ProfileCheckListTemplateItem obj = ProfileCheckListTemplateItem.builder()
                .id(dto.getId())
                .checklistId(dto.getChecklistId())
                .templateId(dto.getTemplateId())
                .description(dto.getDescription())
                .required(dto.getRequired())
                .build();
        return obj;
    }
}
