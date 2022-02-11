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
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.service.EmailSentService;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;

import java.util.Map;

@RestController
@RequestMapping("email")
@Tag(name = "Email", description = "API for call send email")
public class EmailSentApi {

    @Autowired
    EmailSentService _service;
    private final EmailSentRepo _repo;

    public EmailSentApi(EmailSettingRepo repo, EmailSentRepo repo1) {

        _repo = repo1;
    }

    @PostMapping("/send")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "List all Email Setting"
            , description = "Have no condition, find all !"
            , tags = { "Email" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody Map<String, Object> content) {
        try{
            EmailSent es = new EmailSent();
            MapperUtils.map(content, es);
//            _service.create(cid, uid, es);
            return ResponseUtils.handlerSuccess(content);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Email Sent by ID"
            , description = "Get Email Sent by ID"
            , tags = { "Email" }
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
}
