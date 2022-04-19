package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;

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
//Thông tin đánh giá ứng viên
public class InterviewCheckListTemplateItem extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long templateId;
    private Long checkListId;
    private String name;
    private String optionType; // number , select
    private Double minRating; //enable when optionType = number
    private Double maxRating; //enable when optionType = number
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", length = 4000)
    List<String> optionValues;
    private String description;
    private String priority;

    public static InterviewCheckListTemplateItem of(Long cid, String uid, InterviewCheckListTemplateItemDTO dto){
        InterviewCheckListTemplateItem obj = InterviewCheckListTemplateItem.builder()
                .id(dto.getId())
                .templateId(dto.getTemplateId())
                .checkListId(dto.getCheckListId())
                .optionType(dto.getOptionType())
                .minRating(dto.getMinRating())
                .maxRating(dto.getMaxRating())
                .optionValues(dto.getOptionValues())
                .priority(dto.getPriority())
                .description(dto.getDescription())
                .name(dto.getName())
                .build();
        return obj;
    }
}
