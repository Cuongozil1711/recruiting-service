package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.service.InterviewCheckListService;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;

import java.util.List;

@RestController
@RequestMapping("interview")
@RequiredArgsConstructor
@Tag(name = "InterviewCheckList", description = "API for CRUD Interview CheckList")
public class InterviewCheckListApi {
    private final InterviewCheckListService service;
    private final InterviewCheckListRepo repo;



    @PostMapping("/createbyposition")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create InterviewCheckList"
            , description = "API for create InterviewCheckList"
            , tags = { "InterviewCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @RequestBody InterviewCheckListDTO dto){
        try {
            return ResponseUtils.handlerSuccess(service.createByPositionOrg(cid, uid,dto.getPositionId(), dto.getOrgId(), dto.getTitleId(), dto.getInterviewerId(), dto.getRating(), dto.getInterviewDate(), dto.getResult()));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create checkList"
            , description = "API for create checkList"
            , tags = { "InterviewCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity handOverProfile(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @PathVariable("id") Long id
            , @RequestBody List<InterviewCheckListDTO> dtos){
        try {
            return ResponseUtils.handlerSuccess(service.createOrUpdate(cid, uid, id, dtos));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }


    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Interview Check List by InterviewResultId"
            , description = "API for Interview Check List  by InterviewResultId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getInterviewResultCheckList(
            @Parameter(description="id of company") @RequestHeader Long cid
            ,@Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="param in path") @PathVariable(value = "id") Long id){
        try{
            List<InterviewCheckList> lst = repo.findByCompanyIdAndInterviewResultId(cid, id);
            return ResponseUtils.handlerSuccess(lst);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }


}



