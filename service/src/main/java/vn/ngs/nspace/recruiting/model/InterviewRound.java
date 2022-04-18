package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.converter.HashMapConverter;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewRoundDTO;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class InterviewRound extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
   // @Convert(converter = HashMapConverter.class)
    //@Column(columnDefinition =  "text")
   // private Map<String, Object> configs;

    public static InterviewRound of(Long cid, String uid, InterviewRoundDTO dto){
        InterviewRound builder = InterviewRound.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                //.configs(dto.getConfigs())
                .build();

        return builder;
    }
}
