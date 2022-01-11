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
    private String title;
    private String code;
    private Long titleId;
    private Long positionId;
    private Long levelId;
    private Long quantity;
    private Long industryId; // danh muc dung chung
    private Long collaborationType; //full-time, part-time, free-time
    private Long minExperience; // kinh nghiem toi thieu
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
    Map<String,Object> positionObj;
    Map<String,Object> titleObj;
    Map<String,Object> levelObj;
    Map<String,Object> currencyObj;
    Map<String,Object> industryObj;
    private EmployeeDTO receiptNameObj;

}
