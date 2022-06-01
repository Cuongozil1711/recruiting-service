package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.Map;

/**
 * Kế hoạch tuyển dụng ứng với các mã yêu cầu
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentPlanOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String fromCode;
    private Date timeFrom;
    private Date timeTo;
    private Long room;
    private Date endDate;
    private String type; //in-plan , out-plan
    private String solutionSuggestType;
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
                .fromCode(dto.getFromCode())
                .endDate(dto.getEndDate())
                .timeFrom(dto.getTimeFrom())
                .timeTo(dto.getTimeTo())
                .room(dto.getRoom())
                .type(dto.getType())
                .solutionSuggestType(dto.getSolutionSuggestType())
                .orgId(dto.getOrgId())
                .titleId(dto.getTitleId())
                .levelId(dto.getLevelId())
                .positionId(dto.getPositionId())
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

        build.setCompanyId(cid);
        build.setCreateBy(uid);
        build.setUpdateBy(uid);
            return build;
    }
    public RecruitmentPlanOrderDTO toDTOOder() {
        return RecruitmentPlanOrderDTO.builder()
                .id(this.getId())
                .fromCode(this.getFromCode())
                .endDate(this.getEndDate())
                .timeFrom(this.getTimeFrom())
                .timeTo(this.getTimeTo())
                .room(this.getRoom())
                .type(this.getType())
                .solutionSuggestType(this.getSolutionSuggestType())
                .orgId(this.getOrgId())
                .titleId(this.getTitleId())
                .levelId(this.getLevelId())
                .positionId(this.getPositionId())
                .pic(this.getPic())
                .supporterId(this.getSupporterId())
                .quantity(this.getQuantity())
                .businessAddition(this.getBusinessAddition())
                .startDate(this.getStartDate())
                .deadline(this.getDeadline())
                .escalateDate(this.getEscalateDate())
                .reasonId(this.getReasonId())
                .description(this.getDescription())
                .state(this.getState())
                .build();
    }
}
