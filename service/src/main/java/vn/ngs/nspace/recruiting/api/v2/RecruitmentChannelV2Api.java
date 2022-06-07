package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentChannelV2Service;
import vn.ngs.nspace.recruiting.share.request.RecruitmentNewsFilterRequest;

@RestController
@RequestMapping("v2/recruitment-channel")
@RequiredArgsConstructor
@Tag(name = "Recruitment channel", description = "Recruitment channel")
public class RecruitmentChannelV2Api {

    private final RecruitmentChannelV2Service channelV2Service;

    @GetMapping("/list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search recruitment channel",
            description = "Search recruitment channel",
            tags = {"Recruitment channel"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity getListRecruitmentChannel(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
            @Parameter(description = "Id of User") @RequestHeader("uid") String uid
    ) {
        try {
            return ResponseUtils.handlerSuccess(channelV2Service.getByCompanyId(cid, uid));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/change-state")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Change state recruitment channel",
            description = "Change state recruitment channel",
            tags = {"Recruitment channel"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity changeStateRecruitmentChannel(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
            @Parameter(description = "Id of User") @RequestHeader("uid") String uid
    ) {
        try {
            return ResponseUtils.handlerSuccess();
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
