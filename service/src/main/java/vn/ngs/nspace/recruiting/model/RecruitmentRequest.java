package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentRequest extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long pic; // employeeId
    private String type; //trong kế hoạch ngoài kế hoạch
    private String typeRequest; //loại yêu cầu
    private Integer quantity;


}


