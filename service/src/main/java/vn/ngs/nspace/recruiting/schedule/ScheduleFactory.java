package vn.ngs.nspace.recruiting.schedule;

import com.lmax.disruptor.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.KafkaDisruptor;
import vn.ngs.nspace.recruiting.service.EmailSentService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleFactory {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.group:eventbus}")
    private String group;
    @Value("${nspace.scheduleTopic:request-schedule}")
    public String scheduleTopic;

    private final EmailSentService service;

    @PostConstruct
    public void load() {
        List<EventHandler> handlers = new ArrayList<>();
        handlers.add(new HandlerFactory(service));
        KafkaDisruptor.build(scheduleTopic, bootstrapServers, group, handlers.toArray(EventHandler[]::new));
    }
}
