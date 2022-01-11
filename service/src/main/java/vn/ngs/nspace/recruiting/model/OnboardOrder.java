package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Quy trình yêu cầu thủ tục onboard cho nhân viên
public class OnboardOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long employeeId; // nhan vien he thong
    private Long buddy; // nguoi tiep nhan ho tro chuyen mon
    private Long jobApplicationId; // id cua ho so xin viec

    public static OnboardOrder of (Long cid, String uid, OnboardOrderDTO dto){
        OnboardOrder order = OnboardOrder.builder()
                .id(dto.getId())
                .employeeId(dto.getEmployeeId())
                .buddy(dto.getBuddy())
                .jobApplicationId(dto.getJobApplicationId())
                .build();
        return order;
    }
}
