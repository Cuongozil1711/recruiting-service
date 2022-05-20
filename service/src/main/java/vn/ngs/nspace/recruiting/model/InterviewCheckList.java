package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;

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
public class InterviewCheckList extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long interviewResultId;
    private Long checkListId; // tu interview check list template
    private Long interviewerId; // người đánh giá
    private Double rating;
    private String result;
    private Date interviewDate; // ngày phỏng vấn
    private Double average;
    private String description;
    private Long itemId;

    public static InterviewCheckList of(Long cid, String uid, InterviewCheckListDTO dto){
        InterviewCheckList obj = InterviewCheckList.builder()
                .id(dto.getId())
                .interviewResultId(dto.getInterviewResultId())
                .interviewerId(dto.getInterviewerId())
                .checkListId(dto.getCheckListId())
                .rating(dto.getRating())
                .result(dto.getResult())
                .interviewDate(dto.getInterviewDate())
                .average(dto.getAverage())
                .description(dto.getDescription())
                .itemId(dto.getItemId())
                .build();
        return obj;
    }

}
