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
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardTraining;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.service.OnboardTrainingService;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardTrainingDTO;

import java.util.List;

@RestController
@RequestMapping("onboard-traning")
@RequiredArgsConstructor
@Tag(name = "OnboardTraning", description = "API for CRUD Onboard Traning")
public class OnboardTrainingApi {
    private final OnboardTrainingService _service;
    private final OnboardOrderRepo _repoOnboard;

    @GetMapping ("/create-onboard-traning-by-onboard-id/{id}")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create Onboard Traning by OnboardID"
            , description = "API for create Onboard Traning by onboadrId")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity createOnboardTraningByOnboardId(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id){
        try{
            return ResponseUtils.handlerSuccess(_service.createByOnboardOrder(cid, uid, id));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping ("/update-by-onboardId/{id}")
    @ActionMapping(action = Permission.CREATE)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(@RequestHeader Long cid
            , @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody OnboardTrainingDTO dto) {
        try {

            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
