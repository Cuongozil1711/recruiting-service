package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.JobRequirementDTO;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
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
//chi tiet yeu cau tuyen dung
public class JobRequirement extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;

    @Size(max = 255)
    private String title;
    private String code;
    private Long levelId;
    private String specialized;
    private Long quantity;
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", length = 4000)
    private List<String> industryId; // danh muc dung chung
    private String collaborationType; //full-time, part-time, free-time:enum
    private Long minExperience; // kinh nghiem toi thieu:enum
    private String minExperienceUnit;//don vi
    private Long gender;
    private String salaryRange; //in-range, more-than, equals:enum
    private Double salaryFrom;
    private Double salaryTo;
    private Long currencyId;//danh muc dung chung
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
    private Long receiptName;//empId
    @Size(max=15)
    private String receiptPhone;
    private String receiptEmail;


    public static JobRequirement of(Long cid, String uid, JobRequirementDTO dto){
        JobRequirement jobRequirement = JobRequirement.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .title(dto.getTitle())
                .levelId(dto.getLevelId())
                .quantity(dto.getQuantity())
                .industryId(dto.getIndustryId())
                .specialized(dto.getSpecialized())
                .collaborationType(dto.getCollaborationType())
                .minExperience(dto.getMinExperience())
                .minExperienceUnit(dto.getMinExperienceUnit())
                .gender(dto.getGender())
                .salaryRange(dto.getSalaryRange())
                .salaryFrom(dto.getSalaryFrom())
                .salaryTo(dto.getSalaryTo())
                .currencyId(dto.getCurrencyId())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .jobRequirement(dto.getJobRequirement())
                .skillRequirement(dto.getSkillRequirement())
                .benefitDescription(dto.getBenefitDescription())
                .receiptDeadline(dto.getReceiptDeadline())
                .receiptName(dto.getReceiptName())
                .receiptPhone(dto.getReceiptPhone())
                .receiptEmail(dto.getReceiptEmail())
                .build();
        jobRequirement.setCompanyId(cid);
        jobRequirement.setCreateBy(uid);
        jobRequirement.setUpdateBy(uid);
        return jobRequirement;
    }
}
