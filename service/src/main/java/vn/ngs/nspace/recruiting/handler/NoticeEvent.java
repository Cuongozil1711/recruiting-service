package vn.ngs.nspace.recruiting.handler;
import io.vertx.core.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.kafka.dto.EventRequest;
import vn.ngs.nspace.kafka.service.EventPublish;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.lib.utils.StringUtil;
import vn.ngs.nspace.task.core.data.GetConfigData;
import vn.ngs.nspace.task.core.data.UserData;
import vn.ngs.nspace.task.core.utils.Constants;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NoticeEvent {
    @Value("${nspace.notice:notice}")
    public String noticeTopic;

    public void send(Long companyId, String userId, String templateType, String action, Map<String, Object> entityData, Set<String> involves) {
        List<EventRequest> events = new ArrayList<>();
        if (involves != null && !involves.isEmpty()) {
            Map<String, Map<String, Object>> users = StaticContextAccessor.getBean(UserData.class).getLocaleUsers(involves);
            if (!entityData.containsKey("action_user")) {
                Map<String, Object> mapActionUser = StaticContextAccessor.getBean(UserData.class).getUsers(new HashSet<>(Collections.singletonList(userId)));
                if (mapActionUser != null) {
                    Map<String, Object> actionUser = (Map<String, Object>) mapActionUser.get(userId);
                    if (actionUser != null && actionUser.containsKey("fullName")) {
                        entityData.put("action_user", actionUser.get("fullName"));
                    }
                }
            }
            JsonObject templateConfig = StaticContextAccessor.getBean(GetConfigData.class).getTemplate(templateType, companyId, action);
            users.keySet().forEach(locale -> {
                String key = locale + "." + Constants.WEB;
                if (templateConfig.containsKey(key)) {
                    JsonObject template = templateConfig.getJsonObject(key);
                    if (template != null) {
                        Set<String> toUsers = users.getOrDefault(locale, new ConcurrentHashMap()).keySet();
                        if (toUsers != null && !toUsers.isEmpty()) {
                            events.add(createNotice(companyId, Constants.WEB, template, toUsers, entityData));
                        }
                    }
                }
            });
        }

        if (!events.isEmpty()) {
            events.parallelStream().forEach(item -> {
                StaticContextAccessor.getBean(EventPublish.class).publish(noticeTopic, item);
            });
        }
    }

    private EventRequest<Map<String, Object>> createNotice(Long cid, String channel, JsonObject template, Set<String> to, Map<String, Object> mapper) {
        EventRequest eventRequest = new EventRequest();
        eventRequest.setEvent(channel);
        JsonObject payload = new JsonObject();
        String subject = StringUtil.replace(mapper, template.getString(Constants.TEMPLATE_TITLE));
        String content = StringUtil.replace(mapper, template.getString(Constants.TEMPLATE_CONTENT));
        payload.put(Constants.TEMPLATE_SUBJECT, subject);
        payload.put(Constants.TEMPLATE_CONTENT, content);
        payload.put(Constants.TO_KEY, to);
        payload.put(Constants.COMPANY_KEY, cid);
        eventRequest.setPayload(payload.getMap());
        return eventRequest;
    }
}
