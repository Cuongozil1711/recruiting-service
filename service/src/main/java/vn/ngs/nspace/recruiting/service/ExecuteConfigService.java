package vn.ngs.nspace.recruiting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Constants;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.dto.BaseResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class  ExecuteConfigService {
    @Value("${nspace.service.config.url:nothing}")
    public String ConfigServiceURL;
    @Value("${nspace.service.config.key:nothing}")
    public String ConfigServiceKey;


    protected HttpEntity<ResponseEntity> getRequest(String requestUserId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", ConfigServiceKey);
        headers.set("uid", requestUserId);
        headers.set("cid", String.valueOf(companyId));
        HttpEntity<ResponseEntity> request = new HttpEntity<>(headers);
        return request;
    }

    protected HttpHeaders createHeader(String userId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", ConfigServiceKey);
        headers.set("uid", userId);
        headers.set("cid", String.valueOf(companyId));
        return headers;
    }

    public Map<Long, Map<String, Object>> getCategoryByIds(String requestUserId, long companyId, Set<Long> ids){
        try {
            URI uri = new URI(ConfigServiceURL + "/category/ids?application=hcm-service");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload = new HashMap<>();
            payload.put("ids",ids);
            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<Map<Long, Map<String, Object>>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<Long, Map<String, Object>>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<Long, Map<String, Object>>> resp = (BaseResponse<Map<Long, Map<String, Object>>>) response.getBody();
            return resp.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Map<String, Map<String, Object>> getTerritories(String uid, Long cid, Set<String> codes){
        try {
            if(codes != null && !codes.isEmpty()){
                URI uri = new URI(ConfigServiceURL + "/territory/by-codes");
                HttpMethod method = HttpMethod.POST;
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = createHeader(uid,cid);
                Map<String,Object> payload = new HashMap<>();
                payload.put("codes",codes);
                HttpEntity request = new HttpEntity<>(payload,headers);

                ParameterizedTypeReference<BaseResponse<Map<String, Map<String, Object>>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<String, Map<String, Object>>>>() {};
                ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
                BaseResponse<Map<String, Map<String, Object>>> resp = (BaseResponse<Map<String, Map<String, Object>>>) response.getBody();
                return resp.getData();
            }
            return new HashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getEmailConfigById(String uid, Long cid, Long id){
        try {
            URI uri = new URI(ConfigServiceURL + "/notice-config/" + id);
            HttpMethod method = HttpMethod.GET;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(uid,cid);
            HttpEntity request = new HttpEntity<>(headers);

            ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<String, Object>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<String, Object>> resp = (BaseResponse<Map<String, Object>>) response.getBody();
            return resp.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
