package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;

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
public class InterviewResult extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long candidateId;
    private Date interviewDate;
    private Long interviewTemplateId;
    private Long interviewTemplateItemId;
    private String content;
    private Long interviewerId;
    private String state ;


    public static InterviewResult of(Long cid, String uid, InterviewResultDTO dto){
        InterviewResult builder = InterviewResult.builder()
                .id(dto.getId())
                .interviewerId(dto.getInterviewerId())
                .candidateId(dto.getCandidateId())
                .interviewDate(dto.getInterviewDate())
                .content(dto.getContent())
                .state(dto.getState())
                .build();

        return builder;
    }
}
