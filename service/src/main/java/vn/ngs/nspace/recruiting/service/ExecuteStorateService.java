package vn.ngs.nspace.recruiting.service;

import io.vertx.core.buffer.Buffer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.HttpUtils;
import vn.ngs.nspace.recruiting.command.GroupCreateCommand;
import vn.ngs.nspace.recruiting.command.InitFolderCommand;
import vn.ngs.nspace.recruiting.dto.FolderDTO;
import vn.ngs.nspace.recruiting.dto.GroupDTO;
import vn.ngs.nspace.recruiting.dto.UploadTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class ExecuteStorateService {
    @Value("${nspace.service.storage.URL:nothing}")
    public String StorageServiceURL;
    @Value("${nspace.service.storage.key:nothing}")
    public String StorageServiceKey;
    @Value("${nspace.service.user.URL}")
    public String UserServiceURL;

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
    public GroupDTO createGroup(String requestUserId, long companyId, Long parentId
            , String code, String type, String name, String description) throws Exception {
        URI uri = new URI(UserServiceURL + "/api/group/create");
        HttpMethod method = HttpMethod.POST;
        RestTemplate restTemplate = new RestTemplate();

        GroupCreateCommand cmd = new GroupCreateCommand();
        cmd.setCompanyId(companyId);
        cmd.setUserId(requestUserId);
        cmd.setCode(code);
        cmd.setType(type);
        cmd.setName(name);
        cmd.setParentId(parentId);
        cmd.setLeaderId(0L);
        cmd.setDescription(description);

        HttpHeaders headers = createHeader(requestUserId, companyId);
        HttpEntity request = new HttpEntity<>(cmd, headers);
        ParameterizedTypeReference<BaseResponse<GroupDTO>> responseType = new ParameterizedTypeReference<BaseResponse<GroupDTO>>() {
        };
        BaseResponse<GroupDTO> response = null;
        ResponseEntity res = restTemplate.exchange(uri, method, request, responseType);
        response = (BaseResponse<GroupDTO>) res.getBody();

        return response.getData();
    }
    public List<FolderDTO> createFolder(String requestUserId, long companyId, List<String> groupPaths,
                                        List<String> groupPathIds, List<String> names, List<Long> gids) throws Exception {
        HttpHeaders headers = createHeader(requestUserId, companyId);
        InitFolderCommand cmd = new InitFolderCommand();
        cmd.setApplication(Constants.REFERENCE_GROUP_SERVICE_APPLICATION);
        cmd.setNames(names);
        cmd.setGids(gids);
        cmd.setGroupPaths(groupPaths);
        cmd.setGroupPathIds(groupPathIds);

        HttpEntity request = new HttpEntity<>(cmd, headers);
        String uri = StorageServiceURL + "/folder/init";
        BaseResponse<FolderDTO[]> response = HttpUtils.internalRequest(uri, HttpMethod.POST, request, FolderDTO[].class);
        return Arrays.asList(response.getData());
    }
    public void uploadFile(String requestUserId, long companyId, MultipartFile file) throws RuntimeException {
        try {


            String nameText = "abcxyz";
            GroupDTO group = createGroup(requestUserId, companyId,
                    Constants.DEFAULT_PARENT_GROUP_ID, UUID.randomUUID().toString()
                    , nameText
                    , Constants.GROUP_NAME_RECRUITING, Constants.GROUP_DESCRIPTION_RECRUITING);
            //create group
            List<String> groupPathIds = Collections.singletonList(group.getId().toString());
            List<Long> gids = Collections.singletonList(group.getId());
            List<String> names = Collections.singletonList(group.getName());
            List<String> groupPaths = Collections.singletonList(group.getName());


            List<FolderDTO> folders = createFolder(requestUserId, companyId,
                    groupPaths, groupPathIds, names, gids);
            String folderId = folders.get(0).getId();
            URI uri = new URI(StorageServiceURL + "/file/upload"+folderId);
            HttpMethod method = HttpMethod.POST;
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = createHeader(requestUserId,companyId);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap body
                    = new LinkedMultiValueMap<>();
            body.add("files", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate
                    .postForEntity(uri,requestEntity, String.class);


        } catch (Exception e){
            BaseResponse baseResponse = Buffer.buffer(((HttpClientErrorException.BadRequest) e).getResponseBodyAsByteArray()).toJsonObject().mapTo(BaseResponse.class);
            String message = baseResponse.getMessage();
            throw new BusinessException(message);
        }
    }
//

}