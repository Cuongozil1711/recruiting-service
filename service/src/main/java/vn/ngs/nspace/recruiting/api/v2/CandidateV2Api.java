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
import vn.ngs.nspace.recruiting.service.v2.CandidateV2Service;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.request.CandidateFilterRequest;

@RestController
@RequestMapping("v2/candidate")
@RequiredArgsConstructor
@Tag(name = "Candidate", description = "Candidate API")
public class CandidateV2Api {

    private final CandidateV2Service candidateService;

    @PostMapping("list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Candidate Order"
            , description = "Search by condition : name, gender, wardCode, phone, email,..."
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    public ResponseEntity getList(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody CandidateFilterRequest request
            , Pageable page
    ) {
        try {
            Page<CandidateDTO> candidateDTOPage = candidateService.getPage(uid,cid, request, page);

            return ResponseUtils.handlerSuccess(candidateDTOPage);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/count")
    public ResponseEntity getCountAll(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
    ){
        try {
            return ResponseUtils.handlerSuccess(candidateService.getCount(cid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
