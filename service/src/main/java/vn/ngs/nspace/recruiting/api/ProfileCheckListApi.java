package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListRepo;
import vn.ngs.nspace.recruiting.repo.ProfileCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.service.ProfileCheckListService;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("profile")
@RequiredArgsConstructor
@Tag(name = "ProfileCheckList", description = "API for CRUD Profile CheckList")
public class ProfileCheckListApi {
    private final ProfileCheckListService _service;
    private final OnboardOrderRepo _repoOnboard;

    @PostMapping("/create")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "create profile"
            , description = "API for create profile"
            , tags = { "ProfileCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createRecruitingPlanOrders(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @RequestBody ProfileCheckListDTO dto){
        try {
            return ResponseUtils.handlerSuccess(_service.createByPositionTitleContract(cid, uid,dto.getPositionId(), dto.getTitleId(), dto.getContractType(), dto.getEmployeeId(), dto.getReceiptDate(), dto.getDescription(), dto.getSenderId()));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

        @PostMapping("/create-profile-by-onboard-id")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create Profile by OnboardID"
            , description = "API for create profile by onboadrId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createProfileByOnboardId(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param onboardId")
            @RequestBody OnboardOrder onboardOrder){
        try{
            return ResponseUtils.handlerSuccess(_service.createByOnboardOrder(cid, uid, onboardOrder));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/hang-over-profile-by-onboardId/{id}")
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
            , @Parameter(description = "Path Variable")
            @PathVariable(value = "id") Long id
            , @RequestBody List<ProfileCheckListDTO> listDTOS){
        try {
            return ResponseUtils.handlerSuccess(_service.handOverProfile(cid, uid, id, listDTOS));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
