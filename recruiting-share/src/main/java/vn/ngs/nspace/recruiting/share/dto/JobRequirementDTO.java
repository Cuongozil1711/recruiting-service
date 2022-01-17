package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class JobRequirementDTO {
    private Long id;
    private String createBy;
    private String title;
    private String code;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long quantity;
    private Long industryId; // danh muc dung chung
    private String specialized;
    private String collaborationType; //full-time, part-time, free-time
    private Long minExperience; // kinh nghiem toi thieu
    private String minExperienceUnit;
    private Long gender;
    private String salaryRange; //in-range, more-than, equals
    private Double salaryFrom;
    private Double salaryTo;
    private Long currencyId;
    private String location; //mutil-text
    private String description;
    private String jobRequirement;
    private String skillRequirement;
    private String benefitDescription;
    private Date receiptDeadline;
    private Long receiptName;
    private String receiptPhone;
    private String receiptEmail;
    private Map<String,Object> positionObj;
    private Map<String,Object> titleObj;
    private Map<String,Object> levelObj;
    private Map<String,Object> currencyObj;
    private Map<String,Object> industryObj;
    private EmployeeDTO receiptNameObj;
    private Map<String,Object> createByObj;
    private Date createDate;
    private Integer status;
}
