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
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.service.InterviewCheckListService;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;

@RestController
@RequestMapping("interview")
@RequiredArgsConstructor
@Tag(name = "InterviewCheckList", description = "API for CRUD Interview CheckList")
public class InterviewCheckListApi {
    private final InterviewCheckListService service;
    private final InterviewCheckListRepo repo;



    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create InterviewCheckList"
            , description = "API for create InterviewCheckList"
            , tags = { "InterviewCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createByPositionOrg(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @RequestBody InterviewCheckListDTO dto){
        try {
            return ResponseUtils.handlerSuccess(service.createByPositionOrg(cid, uid,dto.getPositionId(), dto.getOrgId(), dto.getInterviewerId(), dto.getRating(), dto.getInterviewDate(), dto.getResult()));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }


    @PostMapping("/create-profile-by-interviewResult-id")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create checkList by interviewResultId"
            , description = "API for create checkList by interviewResultId"
            , tags = "InterviewCheckList")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createCheckListByInterviewResultId(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param interviewResultId ")
            @RequestBody InterviewResult interviewResult){
        try{
            return ResponseUtils.handlerSuccess(service.createByInterviewResult(cid, uid, interviewResult));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/hang-over-checklist-by-interviewerId")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "handOver profile"
            , description = "API for handOver profile"
            , tags = { "ProfileCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity handOverProfile(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @RequestBody InterviewCheckListDTO dto){
        try {
            return ResponseUtils.handlerSuccess(service.handOverProfile(cid, uid, dto.getCheckListId(), dto.getInterviewerId(), dto.getInterviewDate()));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }


}
