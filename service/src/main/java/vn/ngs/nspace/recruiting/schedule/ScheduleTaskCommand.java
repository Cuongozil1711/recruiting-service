package vn.ngs.nspace.recruiting.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ScheduleTaskCommand {
    private Long companyId;
    private Long taskId;
    private String event;
    private String action;
    private Long actionId;
    private String candidates;
    private Date executeTime;
}
