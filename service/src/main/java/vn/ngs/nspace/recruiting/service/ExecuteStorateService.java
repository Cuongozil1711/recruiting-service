package vn.ngs.nspace.recruiting.service;

import io.vertx.core.buffer.Buffer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;

import java.net.URI;

@Component
public class ExecuteStorateService {
    @Value("${nspace.service.storage.URL:nothing}")
    public String StorageServiceURL;
    @Value("${nspace.service.storage.key:nothing}")
    public String StorageServiceKey;

    protected HttpHeaders createHeader(String userId, Long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("uid", userId);
        headers.set("cid", String.valueOf(companyId));
        headers.set("key", StorageServiceKey);
        return headers;
    }

    protected HttpEntity<ResponseEntity> getRequest(String requestUserId, long companyId) {
        final HttpHeaders headers = new HttpHeaders();
        headers.set("key", StorageServiceKey);
        headers.set("uid", requestUserId);
        headers.set("cid", String.valueOf(companyId));
        HttpEntity<ResponseEntity> request = new HttpEntity<>(headers);
        return request;
    }
    public void uploadFile( MultipartFile file) throws RuntimeException {
        try {

            URI uri = new URI(StorageServiceURL + "/file/upload/8aaa81f77fde9ba7017fdef1e13d000e");
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap body
                    = new LinkedMultiValueMap<>();
            body.add("files", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .postForEntity(uri, requestEntity, String.class);

        } catch (Exception e){
            BaseResponse baseResponse = Buffer.buffer(((HttpClientErrorException.BadRequest) e).getResponseBodyAsByteArray()).toJsonObject().mapTo(BaseResponse.class);
            String message = baseResponse.getMessage();
            throw new BusinessException(message);
        }
    }
//

}