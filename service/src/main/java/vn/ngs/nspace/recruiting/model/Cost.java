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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thông tin đánh giá ứng viên
public class Cost extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long orgId;
    private Long costTypeId;
    private Long quantity;
    private String unit;
    private Double price;
    private Double totalAmount;
    private Date startDate;
    private Date endDate;
    private Long year;

    public static Cost of(Long cid, String uid, CostDTO dto){
        Cost obj = Cost.builder()
                .id(dto.getId())
                .orgId(dto.getOrgId())
                .costTypeId(dto.getCostTypeId())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .price(dto.getPrice())
                .year(dto.getYear())
                .totalAmount(dto.getTotalAmount())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        return obj;
    }
}


