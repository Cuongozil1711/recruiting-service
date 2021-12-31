package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Column;
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
//chi tiet yeu cau tuyen dung
public class JobRequirement extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Size(max = 255)
    private String title;
    private String code;
    private Long titleId;
    private Long positionId;
    private Long levelId;

    private Long quantity;
    private Long industryId; // danh muc dung chung
    private Long collaborationType; //full-time, part-time, free-time
    private Long minExperience; // kinh nghiem toi thieu
    private String gender;
    private String salaryRange; //in-range, more-than, equals
    private Double salaryFrom;
    private Double salaryTo;
    private Long currencyId;
    private String location; //mutil-text

    @Column(columnDefinition = "text")
    private String description;

    @Column(columnDefinition = "text")
    private String jobRequirement;

    @Column(columnDefinition = "text")
    private String skillRequirement;
    @Column(columnDefinition = "text")
    private String benefitDescription;

    private Date receiptDeadline;
    private String receiptName;
    @Size(max=15)
    private String receiptPhone;
    private String receiptEmail;
}
