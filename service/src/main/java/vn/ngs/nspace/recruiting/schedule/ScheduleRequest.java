package vn.ngs.nspace.recruiting.schedule;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.kafka.dto.EventRequest;

import java.util.Date;

/**
 * Kế thừa lớp sự kiện
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequest extends EventRequest<ScheduleTaskCommand> {
    String id;
    String cmd;
    String channel;
    Date executeTime;
}
