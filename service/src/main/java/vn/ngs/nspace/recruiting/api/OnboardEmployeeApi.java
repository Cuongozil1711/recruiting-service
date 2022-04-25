package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.service.OnboardEmployeeService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

@RestController
@RequestMapping("onboard-employee")
@Tag(name = "Onboard Employee", description = "Onboard Employee API ")
public class OnboardEmployeeApi {
    private final OnboardEmployeeService onboardEmployeeService;

    public OnboardEmployeeApi(OnboardEmployeeService onboardEmployeeService) {
        this.onboardEmployeeService = onboardEmployeeService;
    }

    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Filter list employee onboard by state,code,name,gender,org "
            , description = "Filter list employee onboard"
            , tags = {"OnboardEmployee", "OnboardEmployeeFilter"}
    )
    @GetMapping(value = "/filter")
    public ResponseEntity filterListEmployeeOnboard(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , OnboardEmployeeFilterRequest request
            , Pageable pageable
    ) {
        try {
            Page<CandidateDTO> candidateDTOS = this.onboardEmployeeService.filterEmployeeOnboard(cid, uid, request, pageable);

            return ResponseUtils.handlerSuccess(candidateDTOS);
        } catch (Exception exception) {
            return ResponseUtils.handlerException(exception);
        }

    }
}
