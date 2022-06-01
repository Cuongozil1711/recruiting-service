package vn.ngs.nspace.recruiting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

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
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentNews extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private Long planId; // mã kế hoạch tuyển dụng
    private Long requestId; // mã yêu cầu
    private String salaryLevel; // mức lương
    private Double salaryFrom; // mức lương từ
    private Double salaryTo; // mức lương đến
    private String unit; //loại tiền
    private String description; // mô tả công việc
    private String location; // địa điểm làm việc
    private String requirement; // yêu cầu công việc
    private Date deadlineSendCV; // hạn nộp hồ sơ
    private String fullName; // họ tên
    private String phone;
    private String email;
}
