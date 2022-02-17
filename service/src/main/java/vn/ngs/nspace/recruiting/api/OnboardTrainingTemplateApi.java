package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplate;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateRepo;
import vn.ngs.nspace.recruiting.service.OnboardTrainingTemplateService;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("onboard-tranning-template")
@Tag(name = "TemplateConfig", description = "Define template to get onboardTranning from Employee")
public class OnboardTrainingTemplateApi {
    private final OnboardTrainingTemplateService _service;
    private final OnboardTrainingTemplateRepo _reppo;
    private final OnboardOrderRepo _onboardOrderRepo;


    public OnboardTrainingTemplateApi(OnboardTrainingTemplateService service, OnboardTrainingTemplateRepo reppo, OnboardOrderRepo onboardOrderRepo) {
        _service = service;
        _reppo = reppo;
        _onboardOrderRepo = onboardOrderRepo;
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create onboardTranning template"
            , description = "API for create onboardTranning template"
            , tags = {"TemplateConfig"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "Payload DTO to create") @RequestBody OnboardTrainingTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update onboardTranning template"
            , description = "API for update onboardTranning template"
            , tags = {"TemplateConfig"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "param in path") @PathVariable(value = "id") Long id
            , @RequestBody OnboardTrainingTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all onboardTranning template"
            , description = "OnboardTranning search by %name% format"
            , tags = {"TemplateConfig"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "Payload to search with positionId, titleId, contractTypeId")
            @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try {
            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long titleId = MapUtils.getLong(condition, "titleId", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);
            Page<OnboardTrainingTemplate> page = _reppo.search(cid, positionId, titleId,orgId, pageable);
            List<OnboardTrainingTemplateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get onboardTranning by Id"
            , description = "API for get onboardTranning template by Id"
            , tags = {"TemplateConfig"}
            , responses = {
            @ApiResponse(description = "onboardTranning with id is response OK Wrap in BaseResponse"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ProfileCheckListTemplateDTO.class))
                    , responseCode = "200"
                    , description = "success")})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "param in path") @PathVariable(value = "id") Long id) {
        try {
            OnboardTrainingTemplate template = _reppo.findByCompanyIdAndId(cid, id).orElse(new OnboardTrainingTemplate());
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, Collections.singletonList(template)));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/delete")
    @ActionMapping(action = Permission.DELETE)
    @Operation(summary = "delete all onboardTranning template"
            , description = "onboardTranning delete by id"
            , tags = {"TemplateConfig"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity delete(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "Payload to search with positionId, titleId, contractTypeId")
            @RequestBody List<Long> ids) {
        try {
            _service.delete(cid, uid, ids);
            return ResponseUtils.handlerSuccess(HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("get-template-by-onboardId/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get onboardTranning by onboardOrderId"
            , description = "API for get onboardTranning template by Id"
            , tags = {"TemplateConfig"}
            , responses = {
            @ApiResponse(description = "onboardTranning with id is response OK Wrap in BaseResponse"
                    , content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(content = @Content(mediaType = "application/json"
                    , schema = @Schema(implementation = ProfileCheckListTemplateDTO.class))
                    , responseCode = "200"
                    , description = "success")})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getByOnboardOderId(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "param in path") @PathVariable(value = "id") Long id) {
        try {
            JobApplication ja = _onboardOrderRepo.getInfoOnboard(cid, id).orElseThrow(()-> new BusinessException("not found OnboardOder"));;
            List<OnboardTrainingTemplate> templates = _reppo.searchConfigTemplate(cid, ja.getPositionId(), ja.getTitleId(), ja.getOrgId());
            OnboardTrainingTemplate template = templates.get(0);
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, Collections.singletonList(template)));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/grants")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Grant template to mutil position"
            , description = "API for create onboard traning template"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity grants(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTO to grant mutil {newDatas[{}]; templateId: }")
            @RequestBody Map<String, Object> request) {
        try {
            if(!request.containsKey("newDatas")){
                throw new BusinessException("invalid-new-data");
            }
            List<Map<String, Object>> newDatas = (List<Map<String, Object>>) vn.ngs.nspace.lib.utils.MapUtils.getObject(request, "newDatas");
            Long templateId = vn.ngs.nspace.lib.utils.MapUtils.getLong(request, "templateId", 0l);
            if(templateId == 0l){
                throw new Exception("invalid-template-id");
            }

            return ResponseUtils.handlerSuccess( _service.grant(cid, uid, templateId, newDatas));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
