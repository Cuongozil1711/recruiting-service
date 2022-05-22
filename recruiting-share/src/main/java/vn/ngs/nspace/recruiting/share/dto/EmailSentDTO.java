package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmailSentDTO {
    private Long id;
    private String refType;
    private String refId;
    private String subject;
    private Date date;
    private String content;
    private String fromEmail;
    private String toEmail;
    private String typeOnboard;
    private Long templateId; // tên mẫu email đã gửi
    private Long emailSettingId;
    private String uid;
    private String type;
    private String mails;
    private String candidates;
}
