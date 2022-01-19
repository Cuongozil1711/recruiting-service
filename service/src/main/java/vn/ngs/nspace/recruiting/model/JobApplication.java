package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Quy trình phỏng vấn ứng viên, được liên kết tới danh sách ứng viên
public class JobApplication extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long candidateId;
    private Long positionId;
    private Long titleId;
    private Long orgId;
    private String contractType;
    private String state; // interview, offer, requested, cancelled, done

    public static JobApplication of(Long cid, String uid, JobApplicationDTO dto){
        JobApplication builder = JobApplication.builder()
                .id(dto.getId())
                .candidateId(dto.getCandidateId())
                .positionId(dto.getPositionId())
                .titleId(dto.getTitleId())
                .orgId(dto.getOrgId())
                .contractType(dto.getContractType())
                .state(dto.getState()).build();

        return builder;
    }
}
