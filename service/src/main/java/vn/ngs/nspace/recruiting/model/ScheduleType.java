package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ScheduleTypeDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class ScheduleType extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id", strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private String description;

    public static ScheduleType of(Long cid, String uid, ScheduleTypeDTO dto) {
        ScheduleType scheduleType = ScheduleType.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        scheduleType.setCompanyId(cid);
        scheduleType.setCreateBy(uid);
        scheduleType.setUpdateBy(uid);

        return scheduleType;
    }
}
