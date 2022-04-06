package vn.ngs.nspace.recruiting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.dto.EventRequest;
import vn.ngs.nspace.kafka.service.EventPublish;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;

@Service
@RequiredArgsConstructor
public class EventFactory {
    private final String SCHEDULE_TOPIC = "schedule_recruiting_mail";

    public void publishSchedule(EventRequest message) {
        publish(SCHEDULE_TOPIC, message);
    }

    public void publish(String topic, EventRequest message) {
        StaticContextAccessor.getBean(EventPublish.class).publish(topic, message);
    }
}
