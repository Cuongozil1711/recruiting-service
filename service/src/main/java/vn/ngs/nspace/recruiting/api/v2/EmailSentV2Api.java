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
import vn.ngs.nspace.recruiting.service.v2.EmailSentV2Service;
import vn.ngs.nspace.recruiting.share.request.EmailSentRequest;

@RestController
@RequestMapping("v2/sent-email")
@RequiredArgsConstructor
@Tag(name = "SentEmail", description = "Sent email API")
public class EmailSentV2Api {

    private final EmailSentV2Service emailSentV2Service;

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Sent email"
            , description = "Sent email"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody EmailSentRequest request) {
        try {
            emailSentV2Service.SentEmail(cid, uid, request);
            return ResponseUtils.handlerSuccess();
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
