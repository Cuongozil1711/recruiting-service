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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Quy trình yêu cầu thủ tục onboard cho nhân viên
public class OnboardOrderCheckList extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long employeeId; // nhan vien he thong
    private String code; // ma thu tuc
    private Long onboardOrderId; // id cua thu tuc onboard
    private Long responsibleId; // id nhan vien chiu trach nhiem
    private Long participantId; // id nhan vien lien quan
    private Date deadline;
    private Date startDate;
    private String state;

    public static OnboardOrderCheckList of (Long cid, String uid, OnboardOrderCheckListDTO dto){
        OnboardOrderCheckList order = OnboardOrderCheckList.builder()
                .id(dto.getId())
                .employeeId(dto.getEmployeeId())
                .onboardOrderId(dto.getOnboardOrderId())
                .responsibleId(dto.getResponsibleId())
                .participantId(dto.getParticipantId())
                .startDate(dto.getStartDate())
                .state(dto.getState())
                .build();
        return order;
    }
}
