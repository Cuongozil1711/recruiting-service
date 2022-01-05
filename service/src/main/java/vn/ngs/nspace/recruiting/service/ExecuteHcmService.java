package vn.ngs.nspace.recruiting.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.utils.HttpUtils;

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

    public OrgResp getOrgResp(String requestUserId, long companyId) {
        HttpEntity<ResponseEntity> request = getRequest(requestUserId, companyId);
        try {
//            String uri = UserServiceURL + "/user/get-user-permission";
            String uri = HcmServiceURL + "/hcm/org/byIds";
            BaseResponse<OrgResp> response = HttpUtils.internalRequest(uri, HttpMethod.GET, request, OrgResp.class);
            return response.getData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
