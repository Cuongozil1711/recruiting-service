package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.converter.HashMapConverter;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentChannelDTO;

import javax.persistence.*;
import java.util.Map;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Quản lý thông tin về kênh tuyển dụng, các thiết lập, kết nối cấu hình
public class RecruitmentChannel extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String type;
    private String avatar;
    private String code;
    private String name;
    private String description;
    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "text")
    private Map<String, Object> configs;

    public static RecruitmentChannel of(Long cid, String uid, RecruitmentChannelDTO request){
        RecruitmentChannel builder = RecruitmentChannel.builder()
                .id(request.getId())
                .type(request.getType())
                .avatar(request.getAvatar())
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .configs(request.getConfigs())
                .build();
        return builder;
    }
}
