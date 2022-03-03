package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.converter.HashMapConverter;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;

import javax.persistence.*;
import java.util.Map;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class UserSetting extends PersistableEntity<Long> {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String userId;
    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Object> config;
}
