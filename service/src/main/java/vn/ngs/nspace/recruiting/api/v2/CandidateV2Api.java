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
import vn.ngs.nspace.recruiting.share.dto.JobApplicationOnboardDTO;
import vn.ngs.nspace.recruiting.share.request.CandidateFilterRequest;

import java.util.List;

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
            Page<CandidateDTO> candidateDTOPage = candidateService.getPage(uid, cid, request, page);

            return ResponseUtils.handlerSuccess(candidateDTOPage);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/count")
    public ResponseEntity getCountAll(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
    ) {
        try {
            return ResponseUtils.handlerSuccess(candidateService.getCount(cid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Candidate by Id"
            , description = "Get Candidate by Id"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record") @PathVariable(value = "id") Long id) {
        try {
            CandidateDTO candidateDTO = candidateService.getById(uid, cid, id);
            return ResponseUtils.handlerSuccess(candidateDTO);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    /**
     * c???p nh???t cv
     * @param cid
     * @param uid
     * @param id
     * @param dto
     * @return
     */

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Candidate by Id"
            , description = "Update Candidate by Id"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record") @PathVariable(value = "id") Long id
            , @RequestBody CandidateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(candidateService.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    /**
     * t???o m???i cv
     * @param cid
     * @param uid
     * @param dto
     * @return
     */

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Candidate"
            , description = "Create single Candidate"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody CandidateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(candidateService.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    /**
     * import danh s??ch ???ng vi??n
     * @param cid
     * @param uid
     * @param dtos
     * @return
     */

    @PostMapping("/create-list")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create list Candidate"
            , description = "Create list Candidate"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "List of candidate") @RequestBody List<CandidateDTO> dtos) {
        try {
            return ResponseUtils.handlerSuccess(candidateService.create(cid, uid, dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    /**
     * danh s??ch th??? t???c theo ???ng vi??n
     */
    @GetMapping("onboard/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get list onboard with JobApplication by Id"
            , description = "Get list onboard with JobApplication by Id"
            , tags = {"JobApplication"}
    )
    public ResponseEntity onboard(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record") @PathVariable(value = "id") Long id
    ) {
        try {
            JobApplicationOnboardDTO dto = candidateService.getJobApplicationOnboard(cid, uid, id);

            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    /**
     * c???p nh???t th??? t???c onboard theo ???ng vi??n
     * @param cid
     * @param uid
     * @param id
     * @return
     */

    @PutMapping("onboard/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "update state list onboard with JobApplication by Id"
            , description = "update state list onboard with JobApplication by Id"
            , tags = {"JobApplication"}
    )
    public ResponseEntity updateOnboard(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record") @PathVariable(value = "id") Long id
            , @RequestBody JobApplicationOnboardDTO request
    ) {
        try {
            JobApplicationOnboardDTO dto = candidateService.updateJobApplicationOnboard(cid, uid, request);

            return ResponseUtils.handlerSuccess(dto);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
