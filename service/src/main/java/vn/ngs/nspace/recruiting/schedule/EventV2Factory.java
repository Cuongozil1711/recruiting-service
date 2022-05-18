package vn.ngs.nspace.recruiting.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.dto.EventRequest;
import vn.ngs.nspace.kafka.service.EventPublish;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;

/**
 * Lớp đẩy thông tin đến service schedule
 */

@Service
@RequiredArgsConstructor
public class EventV2Factory {
    private final String SCHEDULE_TOPIC = "schedule_request";

    public void publishSchedule(EventRequest message) {
        publish(SCHEDULE_TOPIC, message);
    }

    public void publish(String topic, EventRequest message) {
        StaticContextAccessor.getBean(EventPublish.class).publish(topic, message);
    }
}
