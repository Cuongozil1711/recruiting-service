package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentNewsDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * Tin tuyển dụng
 */

@Entity
@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentNews extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String title;
    private Long planId; // mã kế hoạch tuyển dụng
    private Long requestId; // mã yêu cầu
    private String salaryLevel; // mức lương
    private Double salaryFrom; // mức lương từ
    private Double salaryTo; // mức lương đến
    private Long unit; //loại tiền
    private String description; // mô tả công việc
    private String location; // địa điểm làm việc
    private String requirement; // yêu cầu công việc
    private Date deadlineSendCV; // hạn nộp hồ sơ
    private Long emploeeId; // họ tên
    private String phone;
    private String email;
    private String state;
    private String profit;


    public static RecruitmentNews of(Long cid,String uid,RecruitmentNewsDTO dto) {
        RecruitmentNews news = RecruitmentNews.builder()
                .code(dto.getCode())
                .title(dto.getTitle())
                .planId(dto.getPlanId())
                .requestId(dto.getRequestId())
                .salaryLevel(dto.getSalaryLevel())
                .salaryFrom(dto.getSalaryFrom())
                .salaryTo(dto.getSalaryTo())
                .unit(dto.getUnit())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .requirement(dto.getRequirement())
                .deadlineSendCV(dto.getDeadlineSendCV())
                .emploeeId(dto.getEmployeeId())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .state(dto.getState())
                .profit(dto.getProfit())
                .build();

        news.setCreateBy(uid);
        news.setCompanyId(cid);
        news.setUpdateBy(uid);

        return news;
    }
}
