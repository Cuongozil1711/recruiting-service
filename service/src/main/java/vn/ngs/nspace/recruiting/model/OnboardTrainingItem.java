package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingDTO;
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
//Bảng danh sách các đầu mục thử việc bao gồm các thông tin thử việc, liên kết tới thông tin nhân viên
public class OnboardTrainingItem extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id", strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long onboardTrainingId;
    private String result;
    private float evaluate;
    private String finalResult;
    private String selfAssessment;
    private String commentCBQL;
    private String commentHr;
    private Long itemId;
    private Long itemChildId;
    private Long itemGrandChildId;
    private String sourceTL;

    public static OnboardTrainingItem of(Long cid, String uid, OnboardTrainingItemDTO dto) {
        OnboardTrainingItem obj = OnboardTrainingItem.builder()
                .id(dto.getId())
                .onboardTrainingId(dto.getOnboardTrainingId())
                .result(dto.getResult())
                .finalResult(dto.getFinalResult())
                .selfAssessment(dto.getSelfAssessment())
                .commentCBQL(dto.getCommentCBQL())
                .commentHr(dto.getCommentHr())
                .itemId(dto.getItemId())
                .itemChildId(dto.getItemChildId())
                .itemGrandChildId(dto.getItemGrandChildId())
                .sourceTL(dto.getSourceTL())
                .build();
        return obj;
    }
}