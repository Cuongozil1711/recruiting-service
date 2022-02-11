package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.service.ProfileCheckListTemplateService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("profile-template")
@RequiredArgsConstructor
@Tag(name = "TemplateConfig", description = "Define template to get profile from Employee")
public class ProfileCheckListTemplateApi {
    private final ProfileCheckListTemplateService _service;
    private final ProfileCheckListTemplateRepo _repo;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileCheckListTemplateService.class);

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all profile template"
            , description = "Profile search by %name% format"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description="ID of company")
                @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
                @RequestHeader("uid") String uid
            , @Parameter(description="Payload to search with positionId, titleId, contractTypeId")
                @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long titleId = MapUtils.getLong(condition, "titleId", -1l);
            Long contractTypeId = MapUtils.getLong(condition, "contractTypeId", -1l);
            Page<ProfileCheckListTemplate> page = _repo.search(cid, positionId, titleId, pageable);
            List<ProfileCheckListTemplateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create profile template"
            , description = "API for create profile template"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTO to create")  @RequestBody ProfileCheckListTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update profile template"
            , description = "API for update profile template"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id
            , @RequestBody ProfileCheckListTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get profile by Id"
            , description = "API for get Profile template by Id"
            , tags = { "TemplateConfig" }
            , responses = {
                    @ApiResponse(description = "Profile with id is response OK Wrap in BaseResponse"
                                , content = @Content(mediaType = "application/json"
                                                    , schema = @Schema(implementation = BaseResponse.class ))),
                    @ApiResponse(content = @Content(mediaType = "application/json"
                                                    , schema = @Schema(implementation = ProfileCheckListTemplateDTO.class))
                                                    , responseCode = "200"
                                                    , description = "success" )})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path") @PathVariable(value = "id") Long id){
        try {
            ProfileCheckListTemplate template = _repo.findByCompanyIdAndId(cid, id).orElse(new ProfileCheckListTemplate());
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, Collections.singletonList(template)));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/profile-check-list-template")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "get profile check list template for Candidate",
            description = "API get profile check list template for Candidate")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getProfileCheckListTemplate(
            @Parameter(description = "ID of company") @RequestHeader Long cid
            , @Parameter(description = "condition of param body")  @RequestBody Map<String, Object>condition
    ){
        try {
            Long positionId = MapUtils.getLong(condition, "positionId", null);
            Long titleId = MapUtils.getLong(condition, "titleId", null);
            String contractType = MapUtils.getString(condition, "contractType",null );
            List<ProfileCheckListTemplate> results = _repo.findProfileCheckListTemplate(cid, positionId,titleId,contractType);//.orElse(new ProfileCheckListTemplate());
            return ResponseUtils.handlerSuccess(results);
            //return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/profile-check-list-update-status/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update profile template"
            , description = "API for update profile template"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity profileCheckListUpdateStatus(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id
            , @RequestBody ProfileCheckListTemplateDTO dto) {
        try {
            LOGGER.info("dto is "+dto);
            return ResponseUtils.handlerSuccess(_service.updateStatus(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

//    @PostMapping("/by-cycle")
//    @ActionMapping(action = Permission.VIEW)
//    protected ResponseEntity getAssetCheckList(@RequestHeader Long cid
//            , @RequestHeader String uid
//            , @RequestBody Map<String, Object> filter) {
//
//        return null;

}
