package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;

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
public class RecruitmentRequest extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long orgId;
    private Long titleId;  //category
    private Long positionId;  //category
    private Long levelId;  //category
    private Long pic; // employeeId
    private String type; //trong kế hoạch ngoài kế hoạch
    private String typeRequest; //loại yêu cầu
    private Integer quantity;
    private String name; // ten phieu
    //// TODO: new
    private String code;
    private Long orgDeptId;
    private Long groupId;
    private Long contractTypeId;
    private Date startDate;
    private Date endDate;
    private String workType;
    private String workArea;
    private Long salaryType; //category
    private Long fromSalary;
    private Long toSalary;
    private Long currencyUnit;
    private Long gender; //category
    private Long degree; //category (trình độ bằng cấp)
    private Integer fromAge;
    private Integer toAge;
    private String jobDescription; // mô tả công việc
    private String otherRequirement; // yêu cầu
    private String benefit; // quyền lợi
    private String state;
    private Integer dateRequestMonth;
    private Integer dateRequestYear;
    private Long demarcationId;

    public static RecruitmentRequest of (Long cid, String uid, RecruitmentRequestDTO dto) {
        RecruitmentRequest recruitmentRequest = RecruitmentRequest.builder()
                .code(dto.getCode())
                .orgId(dto.getOrgId())
                .state(dto.getState())
                .orgDeptId(dto.getOrgDeptId())
                .groupId(dto.getGroupId())
                .positionId(dto.getPositionId())
                .titleId(dto.getTitleId())
                .levelId(dto.getLevelId())
                .name(dto.getName())
                .typeRequest(dto.getTypeRequest())
                .contractTypeId(dto.getContractTypeId())
                .type(dto.getType())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .workType(dto.getWorkType())
                .workArea(dto.getWorkArea())
                .salaryType(dto.getSalaryType())
                .fromSalary(dto.getFromSalary())
                .toSalary(dto.getToSalary())
                .currencyUnit(dto.getCurrencyUnit())
                .gender(dto.getGender())
                .degree(dto.getDegree())
                .fromAge(dto.getFromAge())
                .toAge(dto.getToAge())
                .jobDescription(dto.getJobDescription())
                .otherRequirement(dto.getOtherRequirement())
                .benefit(dto.getBenefit())
                .dateRequestMonth(dto.getDateRequestMonth())
                .dateRequestYear(dto.getDateRequestYear())
                .build();
        recruitmentRequest.setCompanyId(cid);
        recruitmentRequest.setCreateBy(uid);
        recruitmentRequest.setUpdateBy(uid);

        return recruitmentRequest;
    }

}


