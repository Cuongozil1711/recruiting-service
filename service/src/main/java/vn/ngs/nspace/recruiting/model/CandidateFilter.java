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
//Lưu tìm kiếm ứng viên
public class CandidateFilter extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Object> configs;

    public static CandidateFilter of(Long cid, String uid, CandidateFilter obj){
        CandidateFilter candidateFilter = CandidateFilter.builder()
                .id(obj.getId())
                .code(obj.getCode())
                .name(obj.getName())
                .configs(obj.getConfigs())
                .build();
        return candidateFilter;
    }
}
