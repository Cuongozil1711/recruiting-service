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

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)

//Kế hoạch tuyển dụng
public class RecruitmentPlan extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    @Size(max = 15)
    private String code;
    private Long totalRecruted;
    private Long totalSumQuanity;
    private Long totalSumRecrutingAll;
    private String name; //in-plan , out-plan
    private Date startDate;
    private Date endDate;
    private Long sumQuanity;
    private Long sumRecruting;
    private Long sumRecrutingAll;
    private String state;
    private String recruited;

    public static RecruitmentPlan of(Long cid, String uid, RecruitmentPlanDTO dto){
        RecruitmentPlan build = RecruitmentPlan.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .sumQuanity(dto.getSumQuanity())
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .state(dto.getState())
                .recruited(dto.getRecruited())
                .sumRecruting(dto.getSumRecruting())
                .sumRecrutingAll(dto.getSumRecrutingAll())
                .totalSumRecrutingAll(dto.getTotalSumRecrutingAll())
                .totalRecruted(dto.getTotalRecruted())
                .totalSumQuanity(dto.getTotalSumQuanity())
                .build();

        build.setCompanyId(cid);
        build.setCreateBy(uid);
        build.setUpdateBy(uid);
        return build;
    }

    public static RecruitmentPlanDTO toDTO(RecruitmentPlan obj) {
        return RecruitmentPlanDTO.builder()
                .totalSumRecrutingAll(obj.getTotalSumRecrutingAll())
                .totalRecruted(obj.getTotalRecruted())
                .totalSumQuanity(obj.getTotalSumQuanity())
                .build();
    }
}
