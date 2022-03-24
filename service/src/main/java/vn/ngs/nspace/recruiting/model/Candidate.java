package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

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
//Danh sách ứng viên, được nhập hoặc import từ hệ thống
public class Candidate extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String fullName;
    private String avatar;
    private Date birthDate;
    private Long gender;
    private String phone;
    private String email;

    private String address;
    private String wardCode;
    private String districtCode;
    private String provinceCode;
    private String countryCode;

    private Long educationLevel; // danh muc dung chung
    private String educateLocation;
    private String industry;

    private Long language;
    private String languageLevel;

    private Double experience;
    private String experienceUnit; // months, years

    private String lastCompanyName;
    private String lastPosition;
    private Date lastCompanyDateFrom;
    private Date lastCompanyDateTo;

    private Long applyPositionId;
    private Date applyDate;
    private Long cvSourceId;
    @Type(type = "list-array")
    @Column(columnDefinition = "text[]", length = 4000)
    List<String> tags;

    private Long employeeId;
    private String state;

    public static Candidate of(long cid, String uid, CandidateDTO dto){
        Candidate candidate = Candidate.builder()
                .id(dto.getId())
                .fullName(dto.getFullName())
                .avatar(dto.getAvatar())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .wardCode(dto.getWardCode())
                .districtCode(dto.getDistrictCode())
                .provinceCode(dto.getProvinceCode())
                .countryCode(dto.getCountryCode())
                .educationLevel(dto.getEducationLevel()) // danh muc dung chung
                .educateLocation(dto.getEducateLocation())
                .industry(dto.getIndustry())
                .language(dto.getLanguage())
                .languageLevel(dto.getLanguageLevel())
                .experience(dto.getExperience())
                .experienceUnit(dto.getExperienceUnit()) // months, years
                .lastCompanyName(dto.getLastCompanyName())
                .lastPosition(dto.getLastPosition())
                .lastCompanyDateFrom(dto.getLastCompanyDateFrom())
                .lastCompanyDateTo(dto.getLastCompanyDateTo())
                .applyPositionId(dto.getApplyPositionId())
                .applyDate(dto.getApplyDate())
                .cvSourceId(dto.getCvSourceId())
                .tags(dto.getTags())
                .state(dto.getState())
                .build();

        candidate.setCompanyId(cid);
        candidate.setUpdateBy(uid);
        return candidate;
    }
}
