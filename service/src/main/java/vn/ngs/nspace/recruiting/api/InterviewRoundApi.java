package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.repo.InterviewRoundRepo;
import vn.ngs.nspace.recruiting.service.EmailSettingService;
import vn.ngs.nspace.recruiting.service.InterviewRoundService;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewRoundDTO;

@RestController
@RequestMapping("interview-round")
@Tag(name = "InterviewRound", description = "Setting for Interview round")
public class InterviewRoundApi {

    @Autowired
    InterviewRoundService _service;
    private final InterviewRoundRepo _repo;

    public InterviewRoundApi(InterviewRoundRepo repo) {
        _repo = repo;
    }

    @PostMapping("/all")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "List all Interview round Setting"
            , description = "Have no condition, find all !"
            , tags = { "InterviewRound" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid) {
        try{
            return ResponseUtils.handlerSuccess(_repo.findByCompanyId(cid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Interview round by ID"
            , description = "Have no condition, find all !"
            , tags = { "InterviewRound" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id) {
        try{
            return ResponseUtils.handlerSuccess(_repo.findByCompanyIdAndId(cid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create Interview round Setting"
            , description = "Create Interview round Setting"
            , tags = { "InterviewRound" })
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody InterviewRoundDTO request) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update  Interview round by id"
            , description = "Update Interview round by id, all data save in configs param (JSON Object)"
            , tags = { "InterviewRound" })
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            , @Parameter(description = "Payload") @RequestBody InterviewRoundDTO request){
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
