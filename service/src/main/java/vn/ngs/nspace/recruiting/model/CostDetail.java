package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CostDetailDTO;

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
public class CostDetail extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long costId;
    private Double totalAmount;
    private Date paymentDate;
    private String description;

    public static CostDetail of(Long cid, String uid, CostDetailDTO dto){
        CostDetail obj = CostDetail.builder()
                .id(dto.getId())
                .costId(dto.getCostId())
                .totalAmount(dto.getTotalAmount())
                .paymentDate(dto.getPaymentDate())
                .description(dto.getDescription())
                .build();
        return obj;
    }
}


