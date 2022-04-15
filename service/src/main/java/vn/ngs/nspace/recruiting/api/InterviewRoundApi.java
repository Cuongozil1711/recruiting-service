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
import vn.ngs.nspace.recruiting.service.EmailSettingService;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;

@RestController
@RequestMapping("email-setting")
@Tag(name = "EmailSetting", description = "Setting for email of recruiting")
public class EmailSettingApi {

    @Autowired
    EmailSettingService _service;
    private final EmailSettingRepo _repo;

    public EmailSettingApi(EmailSettingRepo repo) {
        _repo = repo;
    }

    @PostMapping("/all")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "List all Email Setting"
            , description = "Have no condition, find all !"
            , tags = { "EmailSetting" }
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
    @Operation(summary = "Get Email setting by ID"
            , description = "Have no condition, find all !"
            , tags = { "EmailSetting" }
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
    @Operation(summary = "Create email Setting"
            , description = "Create email Setting"
            , tags = { "EmailSetting" })
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody EmailSettingDTO request) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update email Setting by id"
            , description = "Update email Setting by id, all data save in configs param (JSON Object)"
            , tags = { "EmailSetting" })
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            , @Parameter(description = "Payload") @RequestBody EmailSettingDTO request){
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
