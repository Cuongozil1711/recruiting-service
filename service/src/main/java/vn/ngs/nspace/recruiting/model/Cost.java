package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * chi phí tuyển dụng theo tin
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Cost extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private Double expectedCost; // chi phí dự kiến
    private Double cost; // chi phí thực tế
    private String unit;
    private Long newsId; // tin tuyển dụng

    public static Cost of(Long cid, String uid, CostDTO dto){
        Cost cost = Cost.builder()
                .id(dto.getId())
                .unit(dto.getUnit())
                .name(dto.getName())
                .expectedCost(dto.getExpectedCost())
                .cost(dto.getCost())
                .newsId(dto.getNewsId())
                .build();
        return cost;
    }
}


