package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.InterviewCheckListTemplateV2Service;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.request.InterviewTemplateFilterRequest;

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
            , tags = {"InterviewTemplate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @RequestBody InterviewCheckListTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(checkListTemplateV2Service.update(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all interview template"
            , description = "interview search by %name% format"
            , tags = {"InterviewTemplate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @RequestBody InterviewTemplateFilterRequest request
            , Pageable pageable) {
        try {
            Page<InterviewCheckListTemplateDTO> page = checkListTemplateV2Service.getPage(cid, request, pageable);
            return ResponseUtils.handlerSuccess(page);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }

    }
}
