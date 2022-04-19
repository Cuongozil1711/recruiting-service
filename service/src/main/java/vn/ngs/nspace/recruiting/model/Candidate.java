package vn.ngs.nspace.recruiting.model;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

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
    private String codeRecruting;
    private Long orgRecrutingId;
    private Long roomRecrutingId;

    private String experience;
    private String experienceUnit; // months, years
    private Long countInit;
    private Long countRecruited;
    private Long countArchive;
    private Long countInterviewed;
    private Long countApproved;
    private Long countAppointment;
    private Long countOnboard;
    private Long countStaff;
    private Long countDenied;

    private String lastCompanyName;
    private String lastPosition;
    private Date lastCompanyDateFrom;
    private Date lastCompanyDateTo;
    private Integer graduationYear;
    private String salaryUnit;
    private Date onboardDate;
    private Long offerSalary;
    private Long titleId;
    private String code;
    private String introduceBy;
    private Long involveId;

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
                .codeRecruting(dto.getCodeRecruting())
                .orgRecrutingId(dto.getOrgRecrutingId())
                .roomRecrutingId(dto.getRoomRecrutingId())
                .graduationYear(dto.getGraduationYear())
                .code(dto.getCode())
                .countInit(dto.getCountInit())
                .countAppointment(dto.getCountAppointment())
                .countApproved(dto.getCountApproved())
                .countArchive(dto.getCountArchive())
                .countInterviewed(dto.getCountInterviewed())
                .countOnboard(dto.getCountOnboard())
                .countRecruited(dto.getCountRecruited())
                .countStaff(dto.getCountStaff())
                .countDenied(dto.getCountDenied())
                .introduceBy(dto.getIntroduceBy())
                .salaryUnit(dto.getSalaryUnit())
                .onboardDate(dto.getOnboardDate())
                .offerSalary(dto.getOfferSalary())
                .titleId(dto.getTitleId())
                .involveId(dto.getInvolveId())
                .build();
        candidate.setCompanyId(cid);
        candidate.setUpdateBy(uid);
        return candidate;
    }
    public CandidateDTO toDTOS(){
        return CandidateDTO.builder()
                .id(this.getId())
                .fullName(this.getFullName())
                .avatar(this.getAvatar())
                .birthDate(this.getBirthDate())
                .gender(this.getGender())
                .phone(this.getPhone())
                .email(this.getEmail())
                .address(this.getAddress())
                .wardCode(this.getWardCode())
                .districtCode(this.getDistrictCode())
                .provinceCode(this.getProvinceCode())
                .countryCode(this.getCountryCode())
                .educationLevel(this.getEducationLevel()) // danh muc dung chung
                .educateLocation(this.getEducateLocation())
                .industry(this.getIndustry())
                .language(this.getLanguage())
                .languageLevel(this.getLanguageLevel())
                .experience(this.getExperience())
                .experienceUnit(this.getExperienceUnit()) // months, years
                .lastCompanyName(this.getLastCompanyName())
                .lastPosition(this.getLastPosition())
                .lastCompanyDateFrom(this.getLastCompanyDateFrom())
                .lastCompanyDateTo(this.getLastCompanyDateTo())
                .applyPositionId(this.getApplyPositionId())
                .applyDate(this.getApplyDate())
                .cvSourceId(this.getCvSourceId())
                .tags(this.getTags())
                .state(this.getState())
                .codeRecruting(this.getCodeRecruting())
                .orgRecrutingId(this.getOrgRecrutingId())
                .roomRecrutingId(this.getRoomRecrutingId())
                .graduationYear(this.getGraduationYear())
                .code(this.getCode())
                .countInit(this.getCountInit())
                .countAppointment(this.getCountAppointment())
                .countApproved(this.getCountApproved())
                .countArchive(this.getCountArchive())
                .countInterviewed(this.getCountInterviewed())
                .countOnboard(this.getCountOnboard())
                .countRecruited(this.getCountRecruited())
                .countStaff(this.getCountStaff())
                .countDenied(this.getCountDenied())
                .introduceBy(this.getIntroduceBy())
                .salaryUnit(this.getSalaryUnit())
                .onboardDate(this.getOnboardDate())
                .offerSalary(this.getOfferSalary())
                .titleId(this.getTitleId())
                .build();

    }

    public static CandidateDTO countAllStates(Candidate obj) {
        return CandidateDTO.builder()
                .countInit(obj.getCountInit())
                .countAppointment(obj.getCountAppointment())
                .countApproved(obj.getCountApproved())
                .countArchive(obj.getCountArchive())
                .countInterviewed(obj.getCountInterviewed())
                .countOnboard(obj.getCountOnboard())
                .countRecruited(obj.getCountRecruited())
                .countStaff(obj.getCountStaff())
                .countDenied(obj.getCountDenied())
                .build();
    }
}
