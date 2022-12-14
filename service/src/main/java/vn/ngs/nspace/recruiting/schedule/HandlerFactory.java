package vn.ngs.nspace.recruiting.schedule;

import com.lmax.disruptor.EventHandler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.disruptor.DisruptorEvent;
import vn.ngs.nspace.kafka.dto.EventRequest;
import vn.ngs.nspace.recruiting.service.v2.ScheduleEmailSentService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Xử lý dữ liệu trong quá trình trao đổi sự kiên
 */

@Log4j2
@Service
public class HandlerFactory implements EventHandler<DisruptorEvent<String, byte[]>> {

    private static Map<String, Handler> mapper = new ConcurrentHashMap<>();
    private static String MAIL_KEY = "schedule_mail";
    private final ScheduleEmailSentService service;

    // nhận và lọc sự kiện gửi mail
    @Autowired
    public HandlerFactory(ScheduleEmailSentService noticeService) {
        this.service = noticeService;
        mapper.put(MAIL_KEY, new NotificationHandler(noticeService));
    }

    @Override
    public void onEvent(DisruptorEvent<String, byte[]> event, long sequence, boolean endOfBatch) {
        try {
            System.out.println("event:" + event);
            Buffer record = Buffer.buffer(event.getConsumerRecord().value());
            EventRequest<Map<String, Object>> eventRequest = Json.decodeValue(record, EventRequest.class);
            mapper.get(eventRequest.getEvent()).process(eventRequest.getPayload());
        } catch (Exception ex) {
            log.error(ex);
        }
    }
}
