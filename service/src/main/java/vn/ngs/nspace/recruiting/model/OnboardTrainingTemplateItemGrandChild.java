package vn.ngs.nspace.recruiting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemGrandChildDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardTrainingTemplateItemGrandChild extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String key;
    private Long templateId;
    private Long itemId;
    private Long itemChildrenId;
    private String name;
    private Integer deadline;
    private float completion;
    private String description;
    private Long employeeId;

    public static OnboardTrainingTemplateItemGrandChild of(Long cid, String uid, OnboardTrainingTemplateItemGrandChildDTO dto) {
        OnboardTrainingTemplateItemGrandChild obj = OnboardTrainingTemplateItemGrandChild.builder()
                .id(dto.getId())
                .key(dto.getKey())
                .templateId(dto.getTemplateId())
                .itemId(dto.getItemId())
                .itemChildrenId(dto.getItemChildrenId())
                .name(dto.getName())
                .deadline(dto.getDeadline())
                .completion(dto.getCompletion())
                .description(dto.getDescription())
                .employeeId(dto.getEmployeeId())
                .build();
        return obj;
    }
}
