package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Kế hoạch tuyển dụng
public class RecruitmentPlanOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    @Size(max = 15)
    private String code;
    private String type; //in-plan , out-plan
    private String solutionSuggestType; //in-company, out
    private Long orgId;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long pic; // employeeId
    private Long supporterId; // employeeId
    private Long quantity;
    private String businessAddition;
    private Date startDate;
    private Date deadline;
    private Date escalateDate;
    private Long reasonId;
    private String description;
    private String state;
}
