package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.converter.ListHashMapConverter;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Long candidateId; // ứng viên
    private Date interviewDate; // ngày phỏng vấn
    private String content; // bỏ
    private Long interviewerId; // người đánh giá
    private String finalResult;

    private Long templateCheckListId; // mẫu đánh giá
    private String evaluate; // điẻm chung
    private String state ;
    @Convert(converter = ListHashMapConverter.class)
    @Column(columnDefinition = "text")
    private List<Map<String,Object>> items;// bỏ



    public static InterviewResult of(Long cid, String uid, InterviewResultDTO dto){
        InterviewResult builder = InterviewResult.builder()
                .id(dto.getId())
                .interviewerId(dto.getInterviewerId())
                .candidateId(dto.getCandidateId())
                .interviewDate(dto.getInterviewDate())
                .content(dto.getContent())
                .state(dto.getState())
                .finalResult(dto.getFinalResult())
                .evaluate(dto.getEvaluate())
                .build();

        return builder;
    }
}
