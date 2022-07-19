package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.repo.DemarcationRepo;
import vn.ngs.nspace.recruiting.share.dto.DemarcationDTO;
import vn.ngs.nspace.recruiting.share.dto.DemarcationSearchDTO;
import vn.ngs.nspace.recruiting.service.DemarcationService;

import java.util.List;

@RestController
@RequestMapping("demarcation")
@RequiredArgsConstructor
@Tag(name = "Demarcation", description = "Define demarcation")
public class DemarcationApi {

    private final DemarcationRepo repo;
    private final DemarcationService service;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Demarcation"
            , description = "Search all by orgId, levelId, titleId, positionId, timeFrom, timeTo of Demarcation"
            , tags = { "DemarcationConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description="Payload all by orgId, levelId, titleId, positionId of Demarcation")
            @RequestBody DemarcationSearchDTO demarcationSearchDTO
            , Pageable pageable) {
        try {
            Page<Demarcation> page = repo.search(
                    demarcationSearchDTO.getOrgId(),
                    demarcationSearchDTO.getLevelId(),
                    demarcationSearchDTO.getPositionId(),
                    demarcationSearchDTO.getTitleId(),
//                    demarcationSearchDTO.getDateFrom(),
//                    demarcationSearchDTO.getDateTo(),
                    pageable
                    );
            return ResponseUtils.handlerSuccess(new PageImpl(page.getContent(), pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create demarcation"
            , description = "API for create demarcation"
            , tags = { "DemarcationConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid,
            @Parameter(description="Payload DTO to create")  @RequestBody DemarcationDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/update")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Create demarcation"
            , description = "API for create demarcation"
            , tags = { "DemarcationConfig" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid,
            @Parameter(description="Payload DTO to create")  @RequestBody DemarcationDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(service.update(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list Demarcation",
            description = "API for delete list Demarcation")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "delete list Demarcation",
            description = "API for delete list Demarcation")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getId(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            , @PathVariable("id") Long id){
        try {
            return ResponseUtils.handlerSuccess(repo.findById(id).get());
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
}
