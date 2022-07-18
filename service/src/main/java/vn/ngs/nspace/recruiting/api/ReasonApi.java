package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.Reason;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.ReasonRepo;
//import vn.ngs.nspace.recruiting.service.OnboardOrderService;
import vn.ngs.nspace.recruiting.service.ReasonService;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("reason")
@RequiredArgsConstructor
@Tag(name = "Reason", description = "Reason API ")
public class ReasonApi {
    private final ReasonService _service;
    private final ReasonRepo _repo;

    @GetMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Reason with type"
            , description = "Search all Reason "
            , tags = { "Reason"  }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @RequestParam(value = "type", defaultValue = "") String type
            , @RequestParam(value = "search", defaultValue = "#") String search
            , Pageable pageable) {
        try{
            Page<Map<String, Object>> results = _repo.search(cid, type, StringUtils.lowerCase(search), pageable);
            return ResponseUtils.handlerSuccess(results);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
    @GetMapping("/find/all")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Reason with type"
            , description = "Search all Reason "
            , tags = { "Reason"  }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity searchAll(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , Pageable pageable) {
        try{
            Page<Map<String, Object>> results = _repo.searchAll(cid,  pageable);
            return ResponseUtils.handlerSuccess(results);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/list/{type}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Reason with type"
            , description = "List all Reason with type in pathParam"
            , tags = { "Reason"  }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Type of reason") @PathVariable String type) {
        try{
            List<ReasonDTO> dtos = _service.toDTOs(cid, uid, _repo.findByCompanyIdAndType(cid, type));
            return ResponseUtils.handlerSuccess(dtos);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Reason"
            , description = "Create single Reason"
            , tags = { "Reason" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody ReasonDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Reason by Id "
            , description = "Create single Reason, path param is reasonId"
            , tags = { "Reason" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody ReasonDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "View Reason by Id "
            , description = "View single Reason, path param is ReasonId"
            , tags = { "Reason" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id){
        try {
            Reason order = _repo.findByCompanyIdAndId(cid, id).orElse(new Reason());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid, order));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
