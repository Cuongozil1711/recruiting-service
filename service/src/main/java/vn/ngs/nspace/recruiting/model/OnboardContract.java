package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CandidateToDoDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardContractDTO;

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
//Thông tin đánh giá ứng viên
public class OnboardContract extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Long onboardOrderId;
    private Long contractId;

    public static OnboardContract of(Long cid, String uid, OnboardContractDTO dto) {
        OnboardContract candidateTodo = OnboardContract.builder()
                .id(dto.getId())
                .onboardOrderId(dto.getOnboardOrderId())
                .contractId(dto.getContractId())
                .build();
        return candidateTodo;
    }



}


