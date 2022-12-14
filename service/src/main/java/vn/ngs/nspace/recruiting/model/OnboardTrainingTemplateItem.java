package vn.ngs.nspace.recruiting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//Danh sách các hạng mục đánh giá training
public class OnboardTrainingTemplateItem extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String key;
    private Long templateId;
    private String name;
    private Integer deadline;
    private float completion;
    private String description;
    private Boolean required = true;
    private Long employeeId;

    public static OnboardTrainingTemplateItem of(Long cid, String uid, OnboardTrainingTemplateItemDTO dto){
        OnboardTrainingTemplateItem obj = OnboardTrainingTemplateItem.builder()
                .id(dto.getId())
                .key(dto.getKey())
                .templateId(dto.getTemplateId())
                .name(dto.getName())
                .deadline(dto.getDeadline())
                .completion(dto.getCompletion())
                .description(dto.getDescription())
                .employeeId(dto.getEmployeeId())
                .build();
        return obj;
    }

}
