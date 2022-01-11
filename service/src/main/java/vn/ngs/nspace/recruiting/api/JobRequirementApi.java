package vn.ngs.nspace.recruiting.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
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
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody JobRequirementDTO jobRequirementDTO){
       try {
           JobRequirement jobRequirement = service.create(cid, uid, jobRequirementDTO);
           return ResponseUtils.handlerSuccess(jobRequirement);
       }catch (Exception e){
          return ResponseUtils.handlerException(e);
       }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity getById(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable(value = "id") Long id){
        try{
            JobRequirement jobRequirement = repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(JobRequirement.class, id));
            return ResponseUtils.handlerSuccess(jobRequirement);
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/byIds")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity getByIds(@RequestHeader Long cid
            , @RequestHeader String uid
            , Pageable pageable){
        try{
          Page<JobRequirement> jobRequirement = (Page<JobRequirement>) repo.getAllByCompanyAndStatus(cid, pageable);
          List<JobRequirementDTO> dtos = service.toDTOs(cid,uid,jobRequirement.getContent());
          Page<Map<String,Object>> resp = new PageImpl(dtos,pageable, dtos.size());

          return ResponseUtils.handlerSuccess(resp);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity update(@RequestHeader Long cid
            , @RequestHeader String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody JobRequirementDTO dto){
        try{
           JobRequirementDTO jobRequirementDTO = service.update(cid,uid, id, dto);
           return ResponseUtils.handlerSuccess(jobRequirementDTO);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

}
