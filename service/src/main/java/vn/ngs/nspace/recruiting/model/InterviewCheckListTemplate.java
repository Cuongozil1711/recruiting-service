package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;

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
public class InterviewCheckListTemplate extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long orgId;
    private Long positionId;
    private Date startDate;
    private Date endDate;

    public static InterviewCheckListTemplate of(Long cid, String uid, InterviewCheckListTemplateDTO dto){
        InterviewCheckListTemplate obj = InterviewCheckListTemplate.builder()
                .id(dto.getId())
                .orgId(dto.getOrgId())
                .positionId(dto.getPositionId())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        return obj;

    }
}
