package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.HttpUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.JobRequirement;
import vn.ngs.nspace.recruiting.repo.JobRequirementRepo;
import vn.ngs.nspace.recruiting.service.JobRequirementService;
import vn.ngs.nspace.recruiting.share.dto.JobRequirementDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("job-requirement")
@RequiredArgsConstructor
public class JobRequirementApi {
    private final JobRequirementService service;
    private final JobRequirementRepo repo;

    @PostMapping("")
    @ActionMapping(action = Permission.CREATE)
    @Operation( summary = "create Job Requirement"
            , description = "API for create Job Requirement")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader Long cid
            , @Parameter(description="ID of user")
             @RequestHeader String uid
            ,  @Parameter(description="Payload dto to create")
             @RequestBody JobRequirementDTO jobRequirementDTO){
       try {
           JobRequirementDTO jobRequirement = service.create(cid, uid, jobRequirementDTO);
           return ResponseUtils.handlerSuccess(jobRequirement);
       }catch (Exception e){
          return ResponseUtils.handlerException(e);
       }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation( summary = "get one Job Requirement by Id"
            , description = "API for get Job Requirement by Id")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company") @RequestHeader Long cid
             ,@Parameter(description="ID of user") @RequestHeader String uid
            , @PathVariable(value = "id") Long id){
        try{
            JobRequirement jobRequirement = repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(JobRequirement.class, id));
            return ResponseUtils.handlerSuccess(jobRequirement);
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation( summary = "search Job Requirement "
            , description = "API for get all Job Requirement by title, code and positionId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key", schema = @Schema(implementation = String.class))
    protected ResponseEntity search(@Parameter(description="ID of company") @RequestHeader Long cid
            ,@Parameter(description="ID of user") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestParam(name = "search") String search
            , Pageable pageable){
        try{
//            String title = MapUtils.getString(condition, "title", "all");
//            String code = MapUtils.getString(condition, "code", "all");
//            Long positionId = MapUtils.getLong(condition,"positionId", -1l);


          Page<JobRequirement> jobRequirement =  repo.search(cid,search, pageable);
          List<JobRequirementDTO> dtos = service.toDTOs(cid,uid,jobRequirement.getContent());
          Page<Map<String,Object>> resp = new PageImpl(dtos,pageable, dtos.size());

          return ResponseUtils.handlerSuccess(resp);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation( summary = "update Job Requirement "
            , description = "API for update Job Requirement ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key", schema = @Schema(implementation = String.class))
    protected ResponseEntity update(@Parameter(description="ID of company") @RequestHeader Long cid
            ,@Parameter(description="ID of user") @RequestHeader String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody JobRequirementDTO dto){
        try{
           JobRequirementDTO jobRequirementDTO = service.update(cid,uid, id, dto);
           return ResponseUtils.handlerSuccess(jobRequirementDTO);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation( summary = "delete Job Requirement "
            , description = "API for delete List Job Requirement ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key", schema = @Schema(implementation = String.class))
    protected ResponseEntity delete(@Parameter(description="ID of company") @RequestHeader Long cid
            ,@Parameter(description="ID of user") @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            service.delete(cid, uid, ids);
            return ResponseUtils.handlerSuccess(HttpStatus.OK);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

}
