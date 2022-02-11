//package vn.ngs.nspace.recruiting.service;
//
//import io.vertx.core.json.JsonObject;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import vn.ngs.nspace.kafka.dto.EventRequest;
//import vn.ngs.nspace.kafka.service.EventPublish;
//import vn.ngs.nspace.lib.annotation.ExecuteTime;
//import vn.ngs.nspace.lib.utils.StaticContextAccessor;
//import vn.ngs.nspace.lib.utils.StringUtil;
//import vn.ngs.nspace.task.core.data.GetConfigData;
//import vn.ngs.nspace.task.core.data.UserData;
//import vn.ngs.nspace.task.core.dto.InvolveDTO;
//import vn.ngs.nspace.task.core.model.TaskEntity;
//import vn.ngs.nspace.task.core.service.TaskService;
//import vn.ngs.nspace.task.core.utils.Constants;
//
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//public class ExecuteEventService {
//
//    @Value("${nspace.notice:notice}")
//    public String noticeTopic;
//
//    public String getEvent() {
//        return Constants.EventType.notice.name();
//    }
//
//    public JsonObject getTemplate(TaskEntity taskEntity, String action) {
//        String entityName = Pattern.compile("(?=\\p{Upper})").splitAsStream(taskEntity.getClass().getSimpleName()).map(item -> item.toLowerCase()).collect(Collectors.joining("_"));
//        StringBuffer code = new StringBuffer(entityName);
//        code.append(".").append(taskEntity.getType());
//        return StaticContextAccessor.getBean(GetConfigData.class).getTemplate(code.toString(), taskEntity.getCompanyId(), action);
//    }
//
//    public Map<String, Map<String, Object>> getLocaleUsers(Set<String> users) {
//        return StaticContextAccessor.getBean(UserData.class).getLocaleUsers(users);
//    }
//
//    public Map<String, Object> getData(Long companyId, Long taskId, String locale, Map<String, Object> taskProps, Map<String, Object> fieldValues, Set<InvolveDTO> involves, boolean useToFill) {
//        Map<String, Object> mapperValue = new HashMap<>();
//        Map<String, Map<String, Object>> dictionaries = StaticContextAccessor.getBean(GetConfigData.class).getFieldDictionaries(companyId, getEntityName());
//        locale = StringUtils.isEmpty(locale) ? Constants.LOCALE_EN : locale;
//        taskProps = transformData(taskProps, useToFill);
//
//        for (String prop : taskProps.keySet()) {
//            StringBuffer column = new StringBuffer();
//            column.append(Pattern.compile("(?=\\p{Upper})").splitAsStream(prop).map(item -> item.toLowerCase()).collect(Collectors.joining("_")));
//            mapperValue.put(searchDictionaries(companyId, locale, column.toString(), dictionaries), taskProps.get(prop));
//        }
//        if (fieldValues != null) {
//            for (String code : fieldValues.keySet()) {
//                mapperValue.put(searchDictionaries(companyId, locale, code, dictionaries), fieldValues.get(code));
//            }
//        }
//
//        return mapperValue;
//    }
//
//    @ExecuteTime
//    public void onEvent(Object fromObject, JsonObject event) {
//        List<EventRequest> events = new ArrayList<>();
//        String action = event.getString(Constants.HISTORY_ACTION_KEY);
//        JsonObject payload = event.getJsonObject(Constants.PAYLOAD_KEY, new JsonObject());
//        List<String> noticeRoles = payload.getJsonArray(Constants.TO_KEY) != null ? payload.getJsonArray(Constants.TO_KEY).getList() : new ArrayList<>();
//        Set<String> userIds = new HashSet<>();
//        if (involves != null && noticeRoles != null) {
//            involves.forEach(involve -> {
//                if (noticeRoles.isEmpty() || noticeRoles.contains(involve.getRole())) {
//                    if (involve.getInvolveType().equals(Constants.INVOLVE_TYPE_USER)) {
//                        userIds.addAll(involve.getInvolveIds());
//                    } else if (involve.getInvolveType().equals(Constants.INVOLVE_TYPE_GROUP)) {
//                        for (String groupId : involve.getInvolveIds()) {
//                            List<String> groupUserIds = StaticContextAccessor.getBean(UserData.class).getUserIdsByGroupId(Long.valueOf(groupId));
//                            userIds.addAll(groupUserIds);
//                        }
//                    } else if (involve.getInvolveType().equals(Constants.INVOLVE_TYPE_COMPANY)) {
//                        for (String companyId : involve.getInvolveIds()) {
//                            List<String> companyUserIds = StaticContextAccessor.getBean(UserData.class).getUserIdsByCompanyId(Long.valueOf(companyId));
//                            userIds.addAll(companyUserIds);
//                        }
//                    }
//                }
//            });
//        }
//        if (noticeRoles.isEmpty() || noticeRoles.contains(Constants.FILTER_ROLE_REQUESTER)) {
//            if (!StringUtils.isEmpty(taskEntity.getRequestedBy())) {
//                userIds.add(taskEntity.getRequestedBy());
//            }
//        }
//        if (noticeRoles.isEmpty() || noticeRoles.contains(Constants.FILTER_ROLE_RESPONSIBLE)) {
//            if (!StringUtils.isEmpty(taskEntity.getResponsibleId())) {
//                userIds.add(taskEntity.getResponsibleId());
//            }
//        }
//
//        Map<String, Map<String, Object>> users = getLocaleUsers(userIds);
//        if (userIds != null && !userIds.isEmpty()) {
//            if (payload.containsKey(Constants.NOTICE_CHANNEL)) {
//                JsonObject templateConfig = getTemplate(taskEntity, action);
//                List<String> channels = payload.getJsonArray(Constants.NOTICE_CHANNEL).getList();
//                if (!channels.isEmpty()) {
//                    Map<String, Object> mapFieldValues = StaticContextAccessor.getBean(TaskService.class).getFieldValues(taskEntity, "");
//                    Map<String, Object> taskProps = JsonObject.mapFrom(taskEntity).getMap();
//                    users.keySet().forEach(locale -> {
//                        channels.forEach(channel -> {
//                            String key = locale + "." + channel;
//                            if (templateConfig.containsKey(key)) {
//                                JsonObject template = templateConfig.getJsonObject(key);
//                                if (template != null) {
//                                    Map<String, Object> mapperValue = getData() StaticContextAccessor.getBean(TaskService.class).getData(taskEntity.getCompanyId(), taskEntity.getId(), locale, taskProps, mapFieldValues, involves, true);
//                                    Set<String> toUsers = users.getOrDefault(locale, new ConcurrentHashMap()).keySet();
//                                    if (toUsers != null && !toUsers.isEmpty()) {
//                                        events.add(createNotice(taskEntity.getCompanyId(), channel, template, toUsers, mapperValue));
//                                    }
//                                }
//                            }
//                        });
//                    });
//                }
//            }
//        }
//
//        if (!events.isEmpty()) {
//            events.parallelStream().forEach(item -> {
//                StaticContextAccessor.getBean(EventPublish.class).publish(noticeTopic, item);
//            });
//        }
//    }
//
//    private EventRequest<Map<String, Object>> createNotice(Long cid, String channel, JsonObject template, Set<String> to, Map<String, Object> mapper) {
//        EventRequest eventRequest = new EventRequest();
//        eventRequest.setEvent(channel);
//        JsonObject payload = new JsonObject();
//        String subject = StringUtil.replace(mapper, template.getString(Constants.TEMPLATE_TITLE));
//        String content = StringUtil.replace(mapper, template.getString(Constants.TEMPLATE_CONTENT));
//        payload.put(Constants.TEMPLATE_SUBJECT, subject);
//        payload.put(Constants.TEMPLATE_CONTENT, content);
//        payload.put(Constants.TO_KEY, to);
//        payload.put(Constants.COMPANY_KEY, cid);
//
//        eventRequest.setPayload(payload.getMap());
//        return eventRequest;
//    }
//
//    private String searchDictionaries(Long companyId, String locale, String code, Map<String, Map<String, Object>> dictionaries) {
//        if (dictionaries == null || dictionaries.isEmpty()) {
//            return code;
//        }
//        List<String> keys = new ArrayList<>();
//        //org key
//        StringBuffer key = new StringBuffer();
//        key.append(code).append("#");
//        key.append(locale).append("#");
//        key.append(companyId).append("#");
//        keys.add(key.toString());
//        //key with companyId = 0
//        key = new StringBuffer();
//        key.append(code).append("#");
//        key.append(locale).append("#");
//        key.append(0L).append("#");
//        keys.add(key.toString());
//        //key with locale default = en
//        key = new StringBuffer();
//        key.append(code).append("#");
//        key.append(Constants.LOCALE_EN).append("#");
//        key.append(companyId).append("#");
//        keys.add(key.toString());
//        //key with locale default = en and with companyId = 0
//        key = new StringBuffer();
//        key.append(code).append("#");
//        key.append(Constants.LOCALE_EN).append("#");
//        key.append(0L).append("#");
//        keys.add(key.toString());
//
//        for (String dictionaryKey : keys) {
//            if (dictionaries.containsKey(dictionaryKey) && dictionaries.get(dictionaryKey) != null) {
//                Object localeCode = dictionaries.get(dictionaryKey).get(Constants.CODE_LOCALE_KEY);
//                if (localeCode == null) {
//                    localeCode = dictionaries.get(dictionaryKey).get(Constants.CODE_KEY);
//                }
//                return localeCode.toString();
//            }
//        }
//
//        return code;
//    }
//}
