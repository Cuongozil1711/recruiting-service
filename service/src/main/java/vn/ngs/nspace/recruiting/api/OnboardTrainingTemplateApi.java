package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.OnboardTrainingTemplate;
import vn.ngs.nspace.recruiting.repo.OnboardTrainingTemplateRepo;
import vn.ngs.nspace.recruiting.service.OnboardTrainingTemplateService;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingTemplateDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("onboard-tranning-template")
@Tag(name = "TemplateConfig", description = "Define template to get onboardTranning from Employee")
public class OnboardTrainingTemplateApi {
    private final OnboardTrainingTemplateService _service;
    private final OnboardTrainingTemplateRepo _reppo;

    public OnboardTrainingTemplateApi(OnboardTrainingTemplateService service, OnboardTrainingTemplateRepo reppo) {
        _service = service;
        _reppo = reppo;
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create onboardTranning template"
            , description = "API for create onboardTranning template"
            , tags = { "TemplateConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTO to create")  @RequestBody OnboardTrainingTemplateDTO dto) {
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
            Page<OnboardTrainingTemplate> page = _reppo.search(cid, positionId, titleId, pageable);
            List<OnboardTrainingTemplateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
