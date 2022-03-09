package vn.ngs.nspace.recruiting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateItemChildrenDTO;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardTrainingTemplateItemChildren extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String key;
    private Long templateId;
    private Long itemId;
    private String name;
    private float completion;
    private Integer deadline;
    private String description;
    private Long employeeId;

    public static OnboardTrainingTemplateItemChildren of(Long cid, String uid, OnboardTrainingTemplateItemChildrenDTO dto) {
        OnboardTrainingTemplateItemChildren obj = OnboardTrainingTemplateItemChildren.builder()
                .id(dto.getId())
                .key(dto.getKey())
                .templateId(dto.getTemplateId())
                .itemId(dto.getItemId())
                .name(dto.getName())
                .deadline(dto.getDeadline())
                .completion(dto.getCompletion())
                .description(dto.getDescription())
                .employeeId(dto.getEmployeeId())
                .build();
        return obj;
    }

}
