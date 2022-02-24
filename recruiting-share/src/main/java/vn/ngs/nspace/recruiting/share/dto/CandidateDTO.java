package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
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

    private Long educationLevel; // danh muc dung chung
    private String educateLocation;
    private String industry;

    private String language;
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

    private List<String> tags;
    private Long employeeId;
    private Integer status;

    private EmployeeDTO employeeObj;
    private Map<String, Object> genderObj;
    private Map<String, Object> wardCodeObj;
    private Map<String, Object> districtCodeObj;
    private Map<String, Object> provinceCodeObj;
    private Map<String, Object> countryCodeObj;
    private Map<String, Object> educateLevelObj;
    private Map<String, Object> applyPositionIdObj;
    private Map<String, Object> cvSourceObj;
}
