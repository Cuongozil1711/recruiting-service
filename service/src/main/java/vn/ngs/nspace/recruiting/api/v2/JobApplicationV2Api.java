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
import vn.ngs.nspace.recruiting.service.v2.JobApplicationV2Service;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationDTO;
import vn.ngs.nspace.recruiting.share.dto.JobApplicationOnboardDTO;
import vn.ngs.nspace.recruiting.share.request.JobApplicationFilterRequest;

@RestController
@RequestMapping("v2/job-application")
@RequiredArgsConstructor
@Tag(name = "Candidate", description = "Candidate API")
public class JobApplicationV2Api {

    private final JobApplicationV2Service service;

    @PostMapping("list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all JobApplication Order"
            , description = "Search by condition : name, gender, wardCode, phone, email,..."
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity getPage(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody JobApplicationFilterRequest request
            , Pageable page
    ) {
        try {
            Page<JobApplicationDTO> jobApplicationDTOS = service.getPage(uid, cid, request, page);

            return ResponseUtils.handlerSuccess(jobApplicationDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get JobApplication by Id"
            , description = "Get JobApplication by Id"
            , tags = {"Candidate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record") @PathVariable(value = "id") Long id) {
        try {
            JobApplicationDTO jobApplicationDTO = service.getById(uid, cid, id);
            return ResponseUtils.handlerSuccess(jobApplicationDTO);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
