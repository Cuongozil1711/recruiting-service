package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.DemarcationDTO;

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
// bảng định biên
public class Demarcation extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private Long orgId;
    private Long levelId;
    private Long titleId;
    private Long positionId;
    private Date demarcationDate;
    private Integer sumDemarcation;

    public static Demarcation of(Long cId, String uId, DemarcationDTO dto){
        Demarcation demarcation =
                Demarcation.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .orgId(dto.getOrgId())
                        .levelId(dto.getLevelId())
                        .titleId(dto.getTitleId())
                        .positionId(dto.getPositionId())
                        .demarcationDate(dto.getDemarcationDate())
                        .sumDemarcation(dto.getSumDemarcation()).build();
        demarcation.setCompanyId(cId);
        demarcation.setUpdateBy(uId);
        return demarcation;
    }
}
