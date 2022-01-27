package vn.ngs.nspace.recruiting.api;

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
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateTodo;
import vn.ngs.nspace.recruiting.repo.CandidateToDoRepo;
import vn.ngs.nspace.recruiting.service.CandidateToDoService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.CandidateToDoDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("candidate-to-do")
@RequiredArgsConstructor
@Tag(name = "Candidate-To-Do", description = "Candidate-To-Do API")
public class CandidateToDoApi {
    private final CandidateToDoRepo repo;
    private final CandidateToDoService service;



    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all CandidateToDo"
            , description = "Search by condition : "
            , tags = { "Candidate-To-Do" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Condition: title, responsibleId,....")
            @RequestBody Map<String,Object> search
            , Pageable pageable) {
        try {
            String title = MapUtils.getString(search,"title","all");
            Long candidateId = MapUtils.getLong(search,"candidateId", -1l);
            Long responsibleId = MapUtils.getLong(search, "responsibleId", -1l);


            Page<CandidateTodo> page = repo.search(cid, title,candidateId,responsibleId, pageable);
            List<CandidateToDoDTO> dtos = service.toDTOs(cid, uid, page.getContent());
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
    @PostMapping("")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create CandidateToDo "
            , description = "create single candidate to do"
            , tags = { "Candidate-To-Do" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "payload DTO to create") @RequestBody CandidateToDoDTO dto
            ){

        try {
            return ResponseUtils.handlerSuccess(service.create(cid,uid,dto));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }


    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "view single CandidateToDo by Id "
       , description = "view single candidateToDo by Id"
            , tags = { "Candidate-To-Do" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            ,@Parameter(description = "Id of record") @PathVariable(value = "id") Long id
    ){
        try {
            CandidateTodo candidateTodo = repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(CandidateTodo.class,id));
            return ResponseUtils.handlerSuccess(service.toDTO(candidateTodo));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "update single CandidateToDo by Id "
            , description = "update single candidateToDo by Id"
            , tags = { "Candidate-To-Do" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))

    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            ,@Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            ,@Parameter(description = "Payload DTO to update") @RequestBody CandidateToDoDTO dto
    ){
        try{
            return ResponseUtils.handlerSuccess(service.update(cid, uid, id,dto));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list CandidateToDo",
            description = "API for delete list CandidateToDo"
            ,tags = "Candidate-To-Do")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            ,@Parameter(description = "List id of record")
             @RequestBody List<Long> ids){
        try {
            service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
}
