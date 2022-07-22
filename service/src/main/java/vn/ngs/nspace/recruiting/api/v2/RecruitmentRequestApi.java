package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.container.impl.metadata.spi.JobExecutorXml;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentRequestService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDemarcationDTO;
import vn.ngs.nspace.recruiting.share.request.RecruitmentFilterRequest;
import vn.ngs.nspace.recruiting.share.request.RecruitmentRequestFilterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentRequestService;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentRequestDTO;

import java.util.List;

@RestController
@RequestMapping("recruitment-request")
@RequiredArgsConstructor
@Tag(name = "Recruitment request", description = "Recruitment request")
public class RecruitmentRequestApi {

    private final RecruitmentRequestService recruitmentRequestService;

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create recruitment request",
            description = "Create recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestDTO dto
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.createRecruitmentRequest(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }



    @PutMapping()
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruitment request",
            description = "Update recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestDTO dto
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.updateRecruitmentRequest(cid, uid, dto));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update recruitment request",
            description = "Update recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity delete(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.deleteRecruitmentRequest(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search recruitment request",
            description = "Search recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity getPage(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody RecruitmentRequestFilterRequest request
            , Pageable page
    ) {
        try {
            Page<RecruitmentRequestDTO> recruitmentRequestDTOS = recruitmentRequestService.getPage(cid, uid, request, page);
            return ResponseUtils.handlerSuccess(recruitmentRequestDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Detail recruitment request",
            description = "Detail recruitment request",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity detail(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @PathVariable(value = "id") long id
    ) {
        try {
            RecruitmentRequestDTO recruitmentRequestDTO = recruitmentRequestService.detailRecruitmentRequest(cid, uid, id);
            return ResponseUtils.handlerSuccess(recruitmentRequestDTO);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("list-new")
    @ActionMapping(action = Permission.VIEW)
    public ResponseEntity getList(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
    ) {
        try {
            List<RecruitmentRequestDTO> list = recruitmentRequestService.getAllByState(cid, uid);

            return ResponseUtils.handlerSuccess(list);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("get-by-plan/{id}")
    @ActionMapping(action = Permission.VIEW)
    public ResponseEntity getByPlan(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @PathVariable("id") Long planId
    ) {
        try {
            List<RecruitmentRequestDTO> list = recruitmentRequestService.getAllByPlan(cid, uid, planId);

            return ResponseUtils.handlerSuccess(list);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search Detail recruitment with Demarcation",
            description = "Search Detail recruitment with Demarcation",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader("cid") Long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody() RecruitmentRequestDemarcationDTO recruitmentRequestDemarcationDTO
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentRequestService.search(uid, cid, recruitmentRequestDemarcationDTO));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }
}
