package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Người tham gia đánh giá, bang phuc vu cau hinh
public class InterviewInvolve extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long interviewId;
    private Long orgId;
    private Long positionId;
    private Long interviewerId; //empId
    private Long supporterId; //empId

    public static InterviewInvolve of(Long cid, String uid, InterviewInvolveDTO dto){
        InterviewInvolve involve = InterviewInvolve.builder()
                .id(dto.getId())
                .interviewId(dto.getInterviewId())
                .orgId(dto.getOrgId())
                .positionId(dto.getPositionId())
                .interviewerId(dto.getInterviewerId())
                .supporterId(dto.getSupporterId())
                .build();
        return involve;
    }
}
