package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
//Người tham gia đánh giá, bang phuc vu cau hinh
public class InterviewInvolve extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long interviewId;
    private Long orgId;
    private Long positionId;
    private Long titleId;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", length = 4000)
    List<String> interviewerId; //empId
    private Long supporterId; //empId

    public static InterviewInvolve of(Long cid, String uid, InterviewInvolveDTO dto){
        InterviewInvolve involve = InterviewInvolve.builder()
                .id(dto.getId())
                .interviewId(dto.getInterviewId())
                .orgId(dto.getOrgId())
                .positionId(dto.getPositionId())
                .titleId(dto.getTitleId())
                .interviewerId(dto.getInterviewerId())
                .supporterId(dto.getSupporterId())
                .build();
        return involve;
    }
}
