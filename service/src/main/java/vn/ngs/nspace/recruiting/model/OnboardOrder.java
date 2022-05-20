package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

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
//Quy trình yêu cầu thủ tục onboard cho nhân viên
public class OnboardOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long jobApplicationId; // id cua ho so xin viec
    private Long onboardOrderId; // id cua thu tuc onboard
    private Long responsibleId; // id nhan vien chiu trach nhiem
    private Date deadline; // hạn hoàn thành
    private Date endDate; // ngày đóng việc
    private String state; // trạng thái

    public static OnboardOrder of (Long cid, String uid, OnboardOrderDTO dto){
        OnboardOrder order = OnboardOrder.builder()
                .id(dto.getId())
                .jobApplicationId(dto.getJobApplicationId())
                .state(dto.getState())
                .build();
        return order;
    }
}
