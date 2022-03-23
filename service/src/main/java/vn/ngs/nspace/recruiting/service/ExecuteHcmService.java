package vn.ngs.nspace.recruiting.service;

import camundajar.impl.com.google.gson.JsonObject;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.hcm.share.dto.request.EmployeeReq;
import vn.ngs.nspace.hcm.share.dto.response.EmployeeResp;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;
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
//    public void throwCalloutException(Exception e, String defaultMessage) throws BusinessException {
//        String message;
//        try {
//            String responseBody = ((HttpClientErrorException.BadRequest) e).getResponseBodyAsString();
//            JsonObject response = new JsonObject(responseBody).;
//            message = String.valueOf(response.get("message"));
//            if (!StringUtils.isEmpty(message) & message.contains("BusinessException") && message.indexOf(":") > 0) {
//                message = message.substring(message.indexOf(":") + 1).trim();
//            }
//        } catch (Exception ex) {
//            throw new BusinessException(defaultMessage);
//        }
//        if (message != null) {
//            throw new BusinessException(message);
//        }
//    }

    public List<OrgResp> getOrgResp(String requestUserId, long companyId, Set<Long> ids) throws RuntimeException {
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
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload  = new HashMap<>();
            payload.put("ids", empIds);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<List<EmployeeDTO>>> responeType = new ParameterizedTypeReference<BaseResponse<List<EmployeeDTO>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<List<EmployeeDTO>> resp = (BaseResponse<List<EmployeeDTO>>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Map<Long, EmployeeDTO> getMapEmployees(String requestUserId, Long companyId, Set<Long> empIds){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/employee-profile/mapByIds");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload  = new HashMap<>();
            payload.put("ids", empIds);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<Map<Long, EmployeeDTO>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<Long, EmployeeDTO>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<Long, EmployeeDTO>> resp = (BaseResponse<Map<Long, EmployeeDTO>>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Map<Long, OrgResp> getMapOrgs(String requestUserId, Long companyId, Set<Long> orgIds){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/org/mapByIds");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload  = new HashMap<>();
            payload.put("orgIds", orgIds);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<Map<Long, OrgResp>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<Long, OrgResp>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<Long, OrgResp>> resp = (BaseResponse<Map<Long, OrgResp>>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    public EmployeeResp createEmployee(String requestUserId, Long companyId, EmployeeReq employeeReq) throws RuntimeException {
        try {

            URI uri = new URI(HcmServiceURL + "/generic/employee-profile");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            HttpEntity request = new HttpEntity<>(employeeReq, headers);

            ParameterizedTypeReference<BaseResponse<EmployeeResp>> responeType = new ParameterizedTypeReference<BaseResponse<EmployeeResp>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<EmployeeResp> resp = (BaseResponse<EmployeeResp>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            BaseResponse baseResponse = Buffer.buffer(((HttpClientErrorException.BadRequest) e).getResponseBodyAsByteArray()).toJsonObject().mapTo(BaseResponse.class);
            String message = baseResponse.getMessage();
            throw new BusinessException(message);
        }
    }

    public EmployeeResp updateEmployee(String requestUserId, Long companyId, EmployeeReq employeeReq){
        try {
            long id  = employeeReq.getEmployee().getId();
            Logger logger = LoggerFactory.getLogger(ExecuteHcmService.class);
            logger.debug("id "+id);
            if(id>0) {
                URI uri = new URI(HcmServiceURL + "/generic/employee-profile/" + id);
                HttpMethod method = HttpMethod.PUT;
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = createHeader(requestUserId, companyId);
                HttpEntity request = new HttpEntity<>(employeeReq, headers);

                ParameterizedTypeReference<BaseResponse<EmployeeResp>> responeType = new ParameterizedTypeReference<BaseResponse<EmployeeResp>>() {
                };
                ResponseEntity response = restTemplate.exchange(uri, method, request, responeType);
                BaseResponse<EmployeeResp> resp = (BaseResponse<EmployeeResp>) response.getBody();
                return resp.getData();
            }
            return null;
        } catch (Exception e){
            BaseResponse baseResponse = Buffer.buffer(((HttpClientErrorException.BadRequest) e).getResponseBodyAsByteArray()).toJsonObject().mapTo(BaseResponse.class);
            String message = baseResponse.getMessage();
            throw new BusinessException(message);
        }
    }

    public  Map<String, List<Map<String, Object>>> getMapByTypeCodes(String requestUserId, Long companyId, EmployeeReq employeeReq){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/map-by-type-codes");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            HttpEntity request = new HttpEntity<>(employeeReq, headers);

            ParameterizedTypeReference<BaseResponse<Map<String, List<Map<String, Object>>>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<String, List<Map<String, Object>>>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<String, List<Map<String, Object>>>> resp = (BaseResponse<Map<String, List<Map<String, Object>>>>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public Object getContract(String requestUserId, Long companyId, Long contractId){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/contract/" + contractId);
            HttpMethod method = HttpMethod.GET;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            HttpEntity request = new HttpEntity<>(headers);

            ParameterizedTypeReference<BaseResponse<Object>> responeType = new ParameterizedTypeReference<BaseResponse<Object>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Object> resp = (BaseResponse<Object>) response.getBody();
            return resp.getData();

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public BaseResponse<Map<String, Object>> search(String requestUserId, Long companyId, String keyword){
        try {
            URI uri = new URI(HcmServiceURL + "/generic/employee-profile/filter?page=0&size=999");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            Map<String,Object> payload  = new HashMap<>();
//            Map<String, Object> orgFilter= new HashMap<>();
//            orgFilter.put("allChildren", true);
//           orgFilter.put("id", null);
            payload.put("keyword", keyword);
//            payload.put("orgFilter", orgFilter);

            HttpEntity request = new HttpEntity<>(payload,headers);

            ParameterizedTypeReference<BaseResponse<Map<String, Object>>> responeType = new ParameterizedTypeReference<BaseResponse<Map<String, Object>>>() {};
            ResponseEntity response = restTemplate.exchange(uri,method,request,responeType);
            BaseResponse<Map<String, Object>> resp = (BaseResponse<Map<String, Object>>) response.getBody();
            return resp;

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
