package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Column;
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
//Thiết lập danh sách hồ sơ cần hoàn thiện
public class EmailSent extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String refType;
    private String refId;
    private String subject;
    private Date date;
    @Column(columnDefinition = "text")
    private String content;
    private String fromEmail;
    private String toEmail;

    public static EmailSent of(Long cid, String uid, EmailSent sent){
        EmailSent obj = EmailSent.builder()
                .id(sent.getId())
                .refType(sent.getRefType())
                .refId(sent.getRefId())
                .subject(sent.getSubject())
                .date(sent.getDate())
                .content(sent.getContent())
                .fromEmail(sent.getFromEmail())
                .toEmail(sent.getToEmail())
                .build();

        return obj;
    }
}
