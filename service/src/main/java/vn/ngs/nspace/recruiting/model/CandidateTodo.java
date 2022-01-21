package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.recruiting.share.dto.CandidateToDoDTO;

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
public class CandidateTodo extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date deadline;
    private Long candidateId;
    private Long responsibleId;
    private String state;

    public static CandidateTodo of(Long cid, String uid, CandidateToDoDTO dto) {
        CandidateTodo candidateTodo = CandidateTodo.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .deadline(dto.getDeadline())
                .candidateId(dto.getCandidateId())
                .responsibleId(dto.getResponsibleId())
                .state(dto.getState())
                .build();
        return candidateTodo;
    }



}


