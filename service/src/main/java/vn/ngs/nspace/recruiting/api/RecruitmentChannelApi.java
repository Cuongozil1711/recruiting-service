package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.RecruitmentChannel;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentChannelRepo;
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.service.RecruitmentChannelService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.utils.Constants;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentChannelDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("recruitment-channel")
@RequiredArgsConstructor
public class RecruitmentChannelApi {
    private final RecruitmentChannelService _service;
    private final RecruitmentChannelRepo _repo;

    @PutMapping("/update")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Update or create channel"
            , description = "API for update or create ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateFilter(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of user")
             @RequestHeader("uid") String uid
            , @Parameter(description="Payload dto to create or update")
             @RequestBody RecruitmentChannelDTO request){
        try {
            return ResponseUtils.handlerSuccess(_service.createOrUpdate(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/all")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get all channel"
            , description = "Get all channel ")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getFilters(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid){
        try {
            return ResponseUtils.handlerSuccess(_repo.findByCompanyIdAndStatus(cid, Constants.ENTITY_ACTIVE));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
