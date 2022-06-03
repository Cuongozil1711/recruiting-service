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
import vn.ngs.nspace.recruiting.service.v2.RecruitmentRequestService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;
import vn.ngs.nspace.recruiting.share.request.RecruitmentRequestFilterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentRequestService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;

@RestController
@RequestMapping("recruitment-request")
@RequiredArgsConstructor
@Tag(name = "Recruitment request", description = "Recruitment request")
public class RecruitmentRequestApi {

    private final RecruitmentRequestService recruitmentRequestService;

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create recruitment request",
            description = "Create recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity create(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestDTO dto
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.createRecruitmentRequest(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping()
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruitment request",
            description = "Update recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity update(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestDTO dto
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.updateRecruitmentRequest(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruitment request",
            description = "Update recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity delete(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.deleteRecruitmentRequest(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
    @PostMapping("list")
    @ActionMapping(action = Permission.VIEW)
    public ResponseEntity getPage(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestFilterRequest request
            , Pageable page
    ) {
        try {
            Page<RecruitmentRequestDTO> recruitmentRequestDTOS = recruitmentRequestService.getPage(cid, uid, request, page);
            return ResponseUtils.handlerSuccess(recruitmentRequestDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
