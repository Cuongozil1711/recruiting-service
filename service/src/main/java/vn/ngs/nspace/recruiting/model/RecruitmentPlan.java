package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Kế hoạch tuyển dụng
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentPlan extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    @Size(max = 15)
    private String code;
    private String name;
    private Date startDate;
    private Date endDate;
    private String state;

    public static RecruitmentPlan of(Long cid, String uid, RecruitmentPlanDTO dto){
        RecruitmentPlan build = RecruitmentPlan.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(dto.getState())
                .build();

        build.setCompanyId(cid);
        build.setCreateBy(uid);
        build.setUpdateBy(uid);
        return build;
    }
}
