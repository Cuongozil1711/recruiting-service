package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class Reason extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long parentId;
    private String code;
    private String type;
    private String title;
    private String description;


    public static Reason of(Long cid, String uid, ReasonDTO dto){
        Reason reason = Reason.builder()
                .id(dto.getId())
                .parentId(dto.getParentId())
                .code(dto.getCode())
                .type(dto.getType())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
        return reason;
    }
}
