package vn.ngs.nspace.recruiting.schedule;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationHandler implements Handler {

    public NotificationHandler() {
    }

    @Override
    public void process(Map<String, Object> data) {
        try {
            ScheduleTaskCommand scheduleDTO = Json.decodeValue(new JsonObject(data).toBuffer(), ScheduleTaskCommand.class);
            System.out.println("scheduleDTO {0}"+scheduleDTO);
            //service.remindTaskNotices(scheduleDTO.getCompanyId(), scheduleDTO.getTaskId(), scheduleDTO.getEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
