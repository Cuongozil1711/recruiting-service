package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanRequestDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentPlanRequest extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long recruitmentPlanId;
    private Long recruitmentRequestId;
    private Date deadline; // hạn hoàn thành
    private Long picId; // người chịu trách nhiệm

    public static RecruitmentPlanRequest of(String uid, Long cid, RecruitmentPlanRequestDTO dto) {
        RecruitmentPlanRequest planRequest = RecruitmentPlanRequest.builder()
                .id(dto.getId())
                .recruitmentPlanId(dto.getRecruitmentPlanId())
                .recruitmentRequestId(dto.getRequestId())
                .build();

        planRequest.setUpdateBy(uid);
        planRequest.setCompanyId(cid);
        planRequest.setCreateBy(uid);

        return planRequest;
    }
}
