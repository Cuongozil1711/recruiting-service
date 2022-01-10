package vn.ngs.nspace.recruiting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.utils.HttpUtils;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ExecuteHcmService {
    @Value("${nspace.service.hcm.URL:nothing}")
    public String HcmServiceURL;
    @Value("${nspace.service.hcm.key:nothing}")
    public String HcmServiceKey;

    protected HttpHeaders createHeader(String userId, Long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("uid", userId);
        headers.set("cid", String.valueOf(companyId));
        headers.set("key", HcmServiceKey);
        return headers;
    }

    protected HttpEntity<ResponseEntity> getRequest(String requestUserId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", HcmServiceKey);
        headers.set("uid", requestUserId);
        headers.set("cid", String.valueOf(companyId));
        HttpEntity<ResponseEntity> request = new HttpEntity<>(headers);
        return request;
    }

    public List<OrgResp> getOrgResp(String requestUserId, long companyId, Set<Long> ids) {
        try {

            URI uri = new URI(HcmServiceURL + "/generic/org/byIds") ;
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload = new HashMap<>();
            payload.put("orgIds",ids);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<List<OrgResp>>> responeType = new ParameterizedTypeReference<BaseResponse<List<OrgResp>>>() {};
            ResponseEntity resp = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<List<OrgResp>> response = (BaseResponse<List<OrgResp>>) resp.getBody();
            return response.getData();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<EmployeeDTO> getEmployees(String requestUserId, Long companyId, Set<Long> empIds){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/employee-profile/byIds");
            HttpMethod method = HttpMethod.GET;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload  = new HashMap<>();
            payload.put("empIds", empIds);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<List<EmployeeDTO>>> responeType = new ParameterizedTypeReference<BaseResponse<List<EmployeeDTO>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<List<EmployeeDTO>> resp = (BaseResponse<List<EmployeeDTO>>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
