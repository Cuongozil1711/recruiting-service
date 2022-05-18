package vn.ngs.nspace.recruiting.schedule;

import com.lmax.disruptor.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.KafkaDisruptor;
import vn.ngs.nspace.recruiting.service.EmailSentService;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.service.v2.ScheduleEmailSentService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp cấu hình để lắng nghe sự kiện trả về
 */

@Service
@RequiredArgsConstructor
public class ScheduleFactory {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.group:eventbus}")
    private String group; // khai báo nhóm nhận
    @Value("${nspace.scheduleTopic:request-schedule}")
    public String scheduleTopic; // khai báo kênh nhận

    private final ScheduleEmailSentService service;

    // nhận tất cả các sự kiện trả về
    @PostConstruct
    public void load() {
        List<EventHandler> handlers = new ArrayList<>();
        handlers.add(new HandlerFactory(service));
        KafkaDisruptor.build(scheduleTopic, bootstrapServers, group, handlers.toArray(EventHandler[]::new));
    }
}
