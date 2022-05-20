package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * lưu danh sách thủ tục onboard
 * vid dụ :
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardOrderCheckList extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code; // ma thu tuc
    private String name; // tên thủ tục
    private String description; // mô tả

    public static OnboardOrderCheckList of (Long cid, String uid, OnboardOrderCheckListDTO dto){
        OnboardOrderCheckList order = OnboardOrderCheckList.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .description(dto.getDescription())
                .build();

        order.setCompanyId(cid);
        order.setUpdateBy(uid);
        return order;
    }
}
