package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.InterviewCheckListTemplate;
import vn.ngs.nspace.recruiting.model.ProfileCheckListTemplate;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.service.InterviewCheckListTemplateService;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;


import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("interview-template")
@RequiredArgsConstructor
@Tag(name = "InterviewTemplate", description = "API for InterviewTemplate ")
public class InterviewCheckListTemplateApi {
    private final InterviewCheckListTemplateService _service;
    private final InterviewCheckListTemplateRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all interview template"
            , description = "interview search by %name% format"
            , tags = { "InterviewTemplate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload to search with positionId, orgId, ")
            @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{

            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);
            Long titleId = MapUtils.getLong(condition, "titleId", -1l);

            Page<InterviewCheckListTemplate> page = _repo.search(cid, positionId, orgId, titleId, pageable);
            List<InterviewCheckListTemplateDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create interview template"
            , description = "API for create interview template"
            , tags = { "InterviewTemplate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTO to create")  @RequestBody InterviewCheckListTemplateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


//    @PutMapping("/{id}")
//    @ActionMapping(action = Permission.UPDATE)
//    @Operation(summary = "Update interview template"
//            , description = "API for update interview template"
//            , tags = { "InterviewTemplate" }
//    )
//    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
//            , schema = @Schema(implementation = String.class))
//    protected ResponseEntity update(
//            @Parameter(description="ID of company")
//            @RequestHeader("cid") long cid
//            , @Parameter(description="ID of company")
//            @RequestHeader("uid") String uid
//            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id
//            , @RequestBody InterviewCheckListTemplateDTO dto) {
//        try {
//            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
//        } catch (Exception ex) {
//            return ResponseUtils.handlerException(ex);
//        }
//    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get profile by Id"
            , description = "API for get Interview template by Id"
            , tags = { "InterviewTemplate" }
    )

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path") @PathVariable(value = "id") Long id) {
        try {
            InterviewCheckListTemplate template = _repo.findByCompanyIdAndId(cid, id).orElse(new InterviewCheckListTemplate());
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, Collections.singletonList(template)));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
    @PutMapping("/delete")
    @ActionMapping(action = Permission.DELETE)
    @Operation(summary = "delete InterviewCheckList Template"
            , description = "interviewCheckListTemp delete by id"
            , tags = {"InterviewTemplate"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity delete(
            @Parameter(description = "ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description = "ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description = "List Id")
            @RequestBody List<Long> ids) {
        try {
            _service.delete(cid, uid, ids);
            return ResponseUtils.handlerSuccess(HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}


