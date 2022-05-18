package vn.ngs.nspace.recruiting.schedule;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.recruiting.service.v2.ScheduleEmailSentService;

import java.util.Map;

/**
 * NotificationHandler là lớp xử lý của
 * service sau khi nhận được sự kiện
 * từ schedule bắn ngược lại
 */

@Service
public class NotificationHandler implements Handler {

    private final ScheduleEmailSentService noticeService;

    public NotificationHandler(ScheduleEmailSentService noticeService) {
        this.noticeService = noticeService;
    }

    // Hàm xử lý dự dữ liệu nhận được khi lắng nghe sự kiện từ serivce schedule
    @Override
    public void process(Map<String, Object> data) {
        try {
            ScheduleTaskCommand scheduleDTO = Json.decodeValue(new JsonObject(data).toBuffer(), ScheduleTaskCommand.class);

            noticeService.sentEmailAuto(scheduleDTO.getCompanyId(), scheduleDTO.getTaskId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
