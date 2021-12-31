package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.converter.HashMapConverter;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class EmailSetting extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Object> configs;
}
