package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateDTO {
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
    private String codeRecruting;
    private Long orgRecrutingId;
    private Long roomRecrutingId;
    private Integer graduationYear;
    private Long educationLevel; // danh muc dung chung
    private String educateLocation;
    private String industry;

    private Long language;
    private String languageLevel;
    private String code;

    private String experience;
    private String experienceUnit; // months, years

    private String lastCompanyName;
    private String lastPosition;
    private Date lastCompanyDateFrom;
    private Date lastCompanyDateTo;

    private Long applyPositionId;
    private Date applyDate;
    private Long cvSourceId;
    private String introduceBy;
    private String salaryUnit;
    private Date onboardDate;
    private Long offerSalary;
    private Long titleId;
    // count all states
    private Long countInit;
    private Long countRecruited;
    private Long countArchive;
    private Long countInterviewed;
    private Long countApproved;
    private Long countAppointment;
    private Long countOnboard;
    private Long countStaff;
    private Long countDenied;
    //
    private List<String> tags;
    private Long employeeId;
    private Integer status;
    private String state;
    private Long involveId;

    private EmployeeDTO employeeObj;
    private Map<String, Object> genderObj;
    private Map<String, Object> wardCodeObj;
    private Map<String, Object> districtCodeObj;
    private Map<String, Object> provinceCodeObj;
    private Map<String, Object> countryCodeObj;
    private Map<String, Object> educateLevelObj;
    private Map<String, Object> applyPositionIdObj;
    private Map<String, Object> cvSourceObj;
    private Map<String, Object> titleObj;
    private List<Map<String, Object>> CountPositionApply;
}
