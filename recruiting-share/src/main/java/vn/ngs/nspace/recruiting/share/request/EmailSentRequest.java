package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class EmailSentRequest {
    private Long templateId; // mẫu mail
    private Long emailSettingId; // mẫu config setting
    private List<Long> candidateIds; // mẫu config setting
    private List<String> mails; // danh dách mail người nhận
    private String content;
    private String title;
    private Date date;
}
