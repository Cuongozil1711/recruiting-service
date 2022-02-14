package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.EvaluatorOnboardTranningDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingItemDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Bảng danh sách các bộ phận đánh giá các thông tin thử việc, liên kết tới thông tin nhân viên
public class EvaluatorOnboardTranning extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long onboardOrderId;
    private Long onboardTraningId;
    private Long leaderId;
    private Long evaluatorId;
    private Long hrId;
    private String leaderComment;
    private String evaluatorCommnet;
    private String hrComment;

    public static EvaluatorOnboardTranning of(Long cid, String uid, EvaluatorOnboardTranningDTO dto) {
        EvaluatorOnboardTranning obj = EvaluatorOnboardTranning.builder()
                .id(dto.getId())
                .onboardOrderId(dto.getOnboardOrderId())
                .onboardTraningId(dto.getOnboardTraningId())
                .leaderId(dto.getLeaderId())
                .evaluatorId(dto.getEvaluatorId())
                .hrId(dto.getHrId())
                .leaderComment(dto.getLeaderComment())
                .evaluatorCommnet(dto.getEvaluatorCommnet())
                .hrComment(dto.getHrComment())
                .build();
        return obj;
    }

}
