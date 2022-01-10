package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

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

    public static RecruitmentPlanOrder of(Long cid, String uid, RecruitmentPlanOrderDTO dto){
        RecruitmentPlanOrder build = RecruitmentPlanOrder.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .type(dto.getType())
                .solutionSuggestType(dto.getSolutionSuggestType())
                .orgId(dto.getOrgId())
                .titleId(dto.getTitleId())
                .levelId(dto.getTitleId())
                .pic(dto.getPic())
                .supporterId(dto.getSupporterId())
                .quantity(dto.getQuantity())
                .businessAddition(dto.getBusinessAddition())
                .startDate(dto.getStartDate())
                .deadline(dto.getDeadline())
                .escalateDate(dto.getEscalateDate())
                .reasonId(dto.getReasonId())
                .description(dto.getDescription())
                .state(dto.getState())
                .build();
            return build;
    }
}
