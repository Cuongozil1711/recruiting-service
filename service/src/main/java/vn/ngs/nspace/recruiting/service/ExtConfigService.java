package vn.ngs.nspace.recruiting.service;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.utils.HttpUtils;

import java.util.List;
import java.util.Map;

@Component
@Getter
public class ExtConfigService {
    @Value("${nspace.service.config.url}")
    public String ConfigServiceURL;
    @Value("${nspace.service.config.headers}")
    public String ConfigServiceHeaders;

    public JsonObject getTemplate(String userId, long companyId, String code, String action, String application) throws Exception {
        try {
            JsonObject headers = new JsonObject(ConfigServiceHeaders);
            headers.put("cid", companyId);
            String url = ConfigServiceURL + "/notice-config/byCodeAction?code=" + code + "&action=" + action + "&application=" + application;
            BaseResponse<List> baseResponse = HttpUtils.internalGetRequest(url, headers, List.class);
            if (baseResponse.isSuccess()) {
                List<Map<String, Object>> templates = (new JsonArray((List) baseResponse.getData())).getList();
                JsonObject mapper = new JsonObject();
                templates.stream().forEach((item) -> {
                    if (item.containsKey("locale")) {
                        String locale = item.get("locale").toString();
                        String channel = item.get("channel").toString();
                        String key = locale + "." + channel;
                        mapper.put(key, JsonObject.mapFrom(item));
                    }

                });

                JsonObject configTemplate = JsonObject.mapFrom(mapper.getMap());
                if (configTemplate == null || configTemplate.isEmpty()) {
                    configTemplate = JsonObject.mapFrom(mapper.getMap());
                }

                return configTemplate;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

        return null;
    }
}
