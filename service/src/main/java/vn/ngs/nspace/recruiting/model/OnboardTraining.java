package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Bảng danh sách các đầu mục thử việc bao gồm các thông tin thử việc, liên kết tới thông tin nhân viên
public class OnboardTraining extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long onboardOrderId;
    private Long employeeId;
    private String result;
    private float evaluate;
    private String finalResult;
    private String selfAssessment;
    private String commentCBQL;
    private String commentHr;

    public static OnboardTraining of(Long cid, String uid, OnboardTrainingDTO dto) {
        OnboardTraining obj = OnboardTraining.builder()
                .id(dto.getId())
                .onboardOrderId(dto.getOnboardOrderId())
                .employeeId(dto.getEmployeeId())
                .result(dto.getResult())
                .finalResult(dto.getFinalResult())
                .selfAssessment(dto.getSelfAssessment())
                .commentCBQL(dto.getCommentCBQL())
                .commentHr(dto.getCommentHr())
                .build();
        return obj;
    }

}
