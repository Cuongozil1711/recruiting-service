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
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.repo.CandidateFilterRepo;
import vn.ngs.nspace.recruiting.service.CandidateFilterService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.CandidateFilterDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("candidate-filter")
@RequiredArgsConstructor
@Tag(name = "Candidate-Filter", description = "Candidate-Filter API")
public class CandidateFilterApi {
    private final CandidateFilterRepo repo;
    private final CandidateFilterService service;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "List all Candidate Filter"
            , description = "List all Candidate-Filter"
            , tags = { "Candidate-Filter" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> request
            , Pageable pageable) {

        try {
            if (request != null && !request.isEmpty()){
                List<Map<String, Object>> datas = (List<Map<String, Object>>) MapUtils.getObject(request, "datas");
                String search = MapUtils.getString(request, "search", "");
                List<String> lstCreateBy = new ArrayList<>();
                if(datas != null && !datas.isEmpty()){
                    for (Map<String, Object> data: datas) {
                        String createBy =  MapUtils.getString(data, "createBy", "");
                        if(createBy != null && !createBy.isEmpty()){
                            lstCreateBy.add(createBy);
                        }
                    }

                }
                if (lstCreateBy.isEmpty()){
                    Page<CandidateFilter> page = repo.searchName(cid, search, pageable);
                    List<CandidateFilterDTO> dtos = service.toDTOs(cid, uid, page.getContent());
                    return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
                }
                else {
                    Page<CandidateFilter> page = repo.search(cid, lstCreateBy, search, pageable);
                    List<CandidateFilterDTO> dtos = service.toDTOs(cid, uid, page.getContent());
                    return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
                }
            }
            else {
                Page<CandidateFilter> page = repo.searchAll(cid, pageable);
                List<CandidateFilterDTO> dtos = service.toDTOs(cid, uid, page.getContent());
                return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
            }

        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create single Candidate Filter"
            , description = "create single Candidate-Filter"
            , tags = { "Candidate-Filter" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create( @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody CandidateFilter dto){
        try{
            return ResponseUtils.handlerSuccess(service.create(cid, uid, dto));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "update Candidate Filter by Id"
            , description = "update Candidate-Filter by Id"
            , tags = { "Candidate-Filter" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(  @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id
            , @RequestBody CandidateFilter dto){
        try{
            return ResponseUtils.handlerSuccess(service.update(cid, uid, id,dto));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }

    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Candidate-Filter by Id"
            , description = "Get Candidate-Filter by Id"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(@Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Id of record")  @PathVariable(value = "id") Long id){
        try {
            CandidateFilter candidateFilter = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(CandidateFilter.class, id));
            return ResponseUtils.handlerSuccess(candidateFilter);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list Candidate-Filter by Ids"
            , description = "Delete list Candidate-Filter by Ids"
            , tags = { "Candidate" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity delete(@Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try{
            service.delete(cid,uid,ids);
            return ResponseUtils.handlerSuccess();
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
}
