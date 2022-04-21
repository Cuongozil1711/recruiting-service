package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.task.core.model.TaskEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
//Quy trình phỏng vấn ứng viên, được liên kết tới danh sách ứng viên
public class JobApplication extends TaskEntity {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long candidateId;
    private Long positionId;
    private Long planningId;
    private String Code_Candidate;
    private Long titleId;
    private Long orgId;
    private Long planOderId;
    private String contractType;
    private Date onboardDate;
    private Long employeeId;
    private Double offerSalary;
    private String salaryUnit;
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", length = 4000)
    private List <String> introduceById;
    private Long cvSourceId;
    private String state; // interview, offer, requested, cancelled, done
    private Long requestId;

    private String codeRecruting;
    private Long orgRecrutingId;
    private Long roomRecrutingId;

    public static JobApplication of(Long cid, String uid, JobApplicationDTO dto){
        JobApplication builder = JobApplication.builder()
                .id(dto.getId())
                .candidateId(dto.getCandidateId())
                .planningId(dto.getPlanningId())
                .titleId(dto.getTitleId())
                .positionId(dto.getPositionId())
                .offerSalary(dto.getOfferSalary())
                .salaryUnit(dto.getSalaryUnit())
                .introduceById(dto.getIntroduceById())
                .orgId(dto.getOrgId())
                .onboardDate(dto.getOnboardDate())
                .cvSourceId(dto.getCvSourceId())
                .contractType(dto.getContractType())
                .planOderId(dto.getPlanOderId())
                .codeRecruting(dto.getCodeRecruting())
                .orgRecrutingId(dto.getOrgRecrutingId())
                .roomRecrutingId(dto.getRoomRecrutingId())
                .state(dto.getState()).build();


        return builder;
    }
}
