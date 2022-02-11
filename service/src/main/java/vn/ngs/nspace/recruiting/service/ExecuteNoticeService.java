package vn.ngs.nspace.recruiting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.ngs.nspace.lib.dto.BaseResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ExecuteNoticeService {
    @Value("${nspace.service.notice.url:nothing}")
    public String ServiceURL;
    @Value("${nspace.service.notice.key:nothing}")
    public String ServiceKey;
    public static final String host = "smtp.office365.com";
    public static final int port = 587;
    public static final String tls =  "true";

    protected HttpEntity<ResponseEntity> getRequest(String requestUserId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", ServiceURL);
        headers.set("uid", requestUserId);
        headers.set("cid", String.valueOf(companyId));
        HttpEntity<ResponseEntity> request = new HttpEntity<>(headers);
        return request;
    }

    protected HttpHeaders createHeader(String userId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", ServiceKey);
        headers.set("uid", userId);
        headers.set("cid", String.valueOf(companyId));
        return headers;
    }

    public void publishEmail(String requestUserId, long companyId
            , String username, String password, String subject, String content
            , Set<String> toUserIds, Set<String> toEmails){
        try {
            URI uri = new URI(ServiceURL + "/publish/notice");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> cmd = new HashMap<>();
            Map<String,Object> payload = new HashMap<>();
            Map<String,Object> from = new HashMap<>();
            from.put("username", username);
            from.put("password", password);
            from.put("from", username);
            from.put("host", host);
            from.put("port", port);
            from.put("tls", tls);
            payload.put("cid", companyId);
            payload.put("from", from);
            payload.put("subject", subject);
            payload.put("content", content);
            payload.put("to", toUserIds);
            payload.put("emails", toEmails);

            cmd.put("channel","email");
            cmd.put("payload", payload);


            HttpEntity request = new HttpEntity<>(cmd,headers);

            ParameterizedTypeReference<BaseResponse<Object>> responeType = new ParameterizedTypeReference<BaseResponse<Object>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
