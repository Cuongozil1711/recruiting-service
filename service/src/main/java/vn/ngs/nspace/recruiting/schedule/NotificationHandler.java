package vn.ngs.nspace.recruiting.schedule;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.service.EmailSentService;

import java.util.Map;

@Service
public class NotificationHandler implements Handler {

    private  EmailSentService service;
    public NotificationHandler(EmailSentService service) {
        this.service = service;
    }

    @Override
    public void process(Map<String, Object> data) {
        try {
            ScheduleTaskCommand scheduleDTO = Json.decodeValue(new JsonObject(data).toBuffer(), ScheduleTaskCommand.class);
            System.out.println("scheduleDTO {0}"+scheduleDTO);
            service.sendMail(scheduleDTO.getCompanyId(),scheduleDTO.getTaskId());
            //service.remindTaskNotices(scheduleDTO.getCompanyId(), scheduleDTO.getTaskId(), scheduleDTO.getEvent());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
