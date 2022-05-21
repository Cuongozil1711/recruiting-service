package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.InterviewCheckListTemplateItemV2Service;
import vn.ngs.nspace.recruiting.service.v2.InterviewCheckListTemplateV2Service;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;

/**
 * api liên quan thiết lập phỏng vấn
 */

@RestController
@RequestMapping("interview-template")
@RequiredArgsConstructor
@Tag(name = "config template review", description = "config template review")
public class InterviewTemplateApi {

    private final InterviewCheckListTemplateV2Service checkListTemplateV2Service;

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update interview template"
            , description = "API for update interview template"
            , tags = { "InterviewTemplate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id
            , @RequestBody InterviewCheckListTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(checkListTemplateV2Service.update(cid, uid,  dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
