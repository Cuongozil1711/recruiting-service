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
import vn.ngs.nspace.recruiting.service.v2.OnboardOrderV2Service;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardWithStateDTO;
import vn.ngs.nspace.recruiting.share.request.CandidateFilterRequest;
import vn.ngs.nspace.recruiting.share.request.OnboardCandidateFilter;

/**
 * api thủ tục onboard
 */

@RestController
@RequestMapping("onboard-order")
@RequiredArgsConstructor
@Tag(name = "Interview Check List", description = "Interview Check List API")
public class OnboardOrderV2Api {

    private final OnboardOrderV2Service orderV2Service;

    @GetMapping("list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all onboard Order"
            , description = "Search by "
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getList(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestParam(required = false, name = "search") String search
            , Pageable page
    ) {
        try {
            Page<OnboardWithStateDTO> onboardWithStateDTOS = orderV2Service.getPageOnboard(cid, uid, search, page);

            return ResponseUtils.handlerSuccess(onboardWithStateDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all onboard Order"
            , description = "Search by "
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getListCandidateOnboard(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody OnboardCandidateFilter filter
            ,Pageable pageable
            ) {
        try {
            Page<OnboardOrderDTO> onboardOrderDTOS = orderV2Service.getJobApplicationOnboardPage(cid, uid,filter,id, pageable);
            return ResponseUtils.handlerSuccess(onboardOrderDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
