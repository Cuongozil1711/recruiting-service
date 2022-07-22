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

/**
 * Lưu thông tin cv cảu ứng viên
 */

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
    private String fullName; // họ tên
    private String avatar; // ảnh
    private Date birthDate; // sinh nhật
    private Long gender; // giới tính
    private String phone; //số điện thoại
    private String email; // email
    private String address; // địa chỉ
    private String wardCode; // mã phường xã
    private String districtCode; // mã quận huyện
    private String provinceCode; // mã tỉnh thành phố
    private String countryCode; // mã quốc gia
    private Long educationLevel; // cấp bậc giáo dục
    private String educateLocation;
    private String industry;
    private Long interviewResultId; // kết quả đánh giá
    private Long language;
    private String languageLevel;
    private String codeRecruting;
    private Long orgRecrutingId;
    private Long roomRecrutingId;

    private String experience;
    private String experienceUnit; // months, years
//    private Long countInit;
//    private Long countRecruited;
//    private Long countArchive;
//    private Long countInterviewed;
//    private Long countApproved;
//    private Long countAppointment;
//    private Long countOnboard;
//    private Long countStaff;
//    private Long countDenied;

    private String lastCompanyName;
    private String lastPosition;
    private Date lastCompanyDateFrom;
    private Date lastCompanyDateTo;
    private Integer graduationYear;
    private String salaryUnit;
//    private Date onboardDate;
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
    private String createBy;

    private Integer isBlacklist; // check is blacklist ?

    private Long employeeId;
    private String state;

    private Long recruitmentRequestId;
    private Long recruitmentPlanId;

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
                .introduceBy(dto.getIntroduceBy())
                .salaryUnit(dto.getSalaryUnit())
                .offerSalary(dto.getOfferSalary())
                .titleId(dto.getTitleId())
                .isBlacklist(dto.getIsBlacklist())
                .build();
        candidate.setCompanyId(cid);
        candidate.setUpdateBy(uid);

        return candidate;
    }
}
