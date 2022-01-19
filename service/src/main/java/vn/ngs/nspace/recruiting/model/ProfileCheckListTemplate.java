package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
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
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class ProfileCheckListTemplate extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private Long positionId; // vi tri
    private Long titleId; // chuc danh
    private Date startDate;
    private Date endDate;
    private String contractType;
    public static ProfileCheckListTemplate of(Long cid, String uid, ProfileCheckListTemplateDTO dto){
        ProfileCheckListTemplate obj = ProfileCheckListTemplate.builder()
                .id(dto.getId())
                .name(dto.getName())
                .positionId(dto.getPositionId())
                .titleId(dto.getTitleId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .contractType(dto.getContractType())
                .build();
        return obj;
    }
}
