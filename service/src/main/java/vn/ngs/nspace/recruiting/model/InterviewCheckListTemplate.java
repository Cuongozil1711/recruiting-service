package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * entity thông tin mẫu đánh giá ứng viên
 */

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Thông tin mẫu đánh giá ứng viên
public class InterviewCheckListTemplate extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name; // tên mẫu đánh giá
    private String code; // mã mẫu đánh giá
    private Long orgId;  // tổ chức
    private Long positionId; // vị trí
    private Long titleId; // chức danh
    private Date startDate; // ngày bắt đầu
    private Date endDate; // ngày kết thúc

    public static InterviewCheckListTemplate of(Long cid, String uid, InterviewCheckListTemplateDTO dto){
        InterviewCheckListTemplate obj = InterviewCheckListTemplate.builder()
                .id(dto.getId())
                .orgId(dto.getOrgId())
                .positionId(dto.getPositionId())
                .code(dto.getCode())
                .name(dto.getName())
                .startDate(dto.getStartDate())
                .titleId(dto.getTitleId())
                .endDate(dto.getEndDate())
                .build();
        return obj;

    }
}
