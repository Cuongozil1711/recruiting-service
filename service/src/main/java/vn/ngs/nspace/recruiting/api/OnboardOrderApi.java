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
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.dto.BaseResponse;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.request.OnboardEmployeeFilterRequest;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.service.OnboardOrderService;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("onboard-order")
@RequiredArgsConstructor
@Tag(name = "OnboardOrder", description = "Onboard Order API ")
public class OnboardOrderApi {
    private final OnboardOrderService _service;
    private final OnboardOrderRepo _repo;
    private final ExecuteHcmService _hcmService;
    private final EmailSentRepo _repoEmail;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all Onboard Order"
            , description = "Search by condition : employeeId, buddy, jobApplicationId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity search(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long employeeId = MapUtils.getLong(condition, "employeeId", -1l);
            Long buddy = MapUtils.getLong(condition, "buddy", -1l);
            Long jobApplicationId = MapUtils.getLong(condition, "jobApplicationId", -1l);
            Long positionId = MapUtils.getLong(condition, "positionId", -1l);
            Long titleId = MapUtils.getLong(condition, "titleId", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);
            String search = MapUtils.getString(condition, "search", "");
            BaseResponse<Map<String, Object>> obj = _hcmService.search(uid, cid, search);
            obj.getData();
            Map<String, Object> data = obj.getData();
            List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
            List<EmployeeDTO> empLoyees = new ArrayList<>();
            for (Map<String, Object> o: content) {
                EmployeeDTO empLoyee = new EmployeeDTO();
                Long id = MapUtils.getLong(o, "id", 0l);
                empLoyee.setId(id);
                empLoyees.add(empLoyee);
            }
            List<Long> empIds = empLoyees.stream().map(EmployeeDTO::getId).collect(Collectors.toList());
            if(condition != null && !condition.isEmpty()){
                Page<OnboardOrder> page = _repo.search(cid, buddy, positionId, titleId, orgId, jobApplicationId, empIds, pageable);
                List<OnboardOrderDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
                return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));

            }
            else {
                Page<OnboardOrder> page = _repo.searchAll(cid,buddy, employeeId, jobApplicationId, pageable);
                List<OnboardOrderDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
                return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
            }

        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/filter")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "filter all Onboard Order"
            , description = "filter by condition : employeeId, buddy, jobApplicationId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity filter(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload filter") @RequestBody OnboardEmployeeFilterRequest request
            , Pageable pageable
    ){
        try{
            Long employeeId = request.getEmployeeId();
            Long buddy = request.getBuddy();
            Long jobApplicationId = request.getJobApplicationId();
            Long positionId = request.getPositionId();
            Long titleId = request.getTitleId();
            Long orgId = request.getOrgId();
            String search = request.getSearch();
            BaseResponse<Map<String, Object>> obj = _hcmService.filter(uid, cid, request);
            obj.getData();
            Map<String, Object> data = obj.getData();
            List<Map<String, Object>> content = (List<Map<String, Object>>) data.get("content");
            List<EmployeeDTO> empLoyees = new ArrayList<>();
            for (Map<String, Object> o: content) {
                EmployeeDTO empLoyee = new EmployeeDTO();
                Long id = MapUtils.getLong(o, "id", 0l);
                empLoyee.setId(id);
                empLoyees.add(empLoyee);
            }
            List<Long> empIds = empLoyees.stream().map(EmployeeDTO::getId).collect(Collectors.toList());
                Page<OnboardOrder> page = _repo.search(cid, buddy, positionId, titleId, orgId, jobApplicationId, empIds, pageable);
                List<OnboardOrderDTO> dtos = _service.toDTOs(cid, uid, page.getContent());
                return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create single Onboard Order"
            , description = "Create single Onboard Order"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Onboard Order by Id "
            , description = "Create single Onboard Order, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "View Onboard Order by Id "
            , description = "View single Onboard Order, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id){
        try {
            OnboardOrder order = _repo.findByCompanyIdAndId(cid, id).orElse(new OnboardOrder());
            return ResponseUtils.handlerSuccess(_service.toDTOWithObj(cid, uid, order));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/check-list/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get check list by onboard Id"
            , description = "Get check list by onboard Id"
            , tags = { "OnboardOrder", "OnboardCheckList" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getCheckListByOnboardId(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id){
        try {
            return ResponseUtils.handlerSuccess(_service.getOnboardOrderCheckList(cid, uid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

//    @PostMapping("/create-buddy-mentor")
//    @ActionMapping(action = Permission.CREATE)
//    @Operation(summary = "Create Buddy and Mentor by OnboardId"
//            , description = "Create Buddy and Mentor by OnboardId"
//            , tags = { "OnboardOrder" }
//    )
//    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
//            , schema = @Schema(implementation = String.class))
//    protected ResponseEntity createBuddyAndMentor(
//            @Parameter(description = "Id of Company") @RequestHeader Long cid
//            , @Parameter(description = "Id of User") @RequestHeader String uid
//            , @RequestBody OnboardOrderDTO dto) {
//        try {
//            return ResponseUtils.handlerSuccess(_service.createBuddyByOnbodrdId(cid, uid, dto.getEmployeeId(), dto.getBuddy(), dto.getMentorId()));
//        } catch (Exception ex) {
//            return ResponseUtils.handlerException(ex);
//        }
//    }

    @PutMapping("/update-buddy-mentor/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update Buddy and Mentor by OnboardId "
            , description = "Update single Buddy and Mentor, path param is OnboardOrderId"
            , tags = { "OnboardOrder" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity updateBuddyAndMentor(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Path Variable") @PathVariable(value = "id") Long id
            , @RequestBody OnboardOrderDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.updateBuddyByOnboardId(cid, uid, id, dto.getBuddy(), dto.getMentorId()));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

//    @PostMapping("/by-cycle")
//    @ActionMapping(action = Permission.VIEW)
//    protected ResponseEntity getAssetCheckList(@RequestHeader Long cid
//            , @RequestHeader String uid
//            , @RequestBody Map<String, Object> filter) {
//
//        return null;


    @PostMapping("/get-email-by-onboarId")
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "get many records info email",
            description = "can get list email")

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getEmailByOnboarId(
            @Parameter(description="id of company") @RequestHeader Long cid
            , @Parameter(description="id of user") @RequestHeader String uid
            ,@Parameter(description="Payload DTO to get email") @RequestBody Map<String, Object> condition ) {
        try {
            String toEmail = MapUtils.getString(condition, "toEmail", "");
            String refType = MapUtils.getString(condition, "refType", "");
            String type = MapUtils.getString(condition, "type", "");
            List<EmailSent> list = _repoEmail.findByCompanyIdAndToEmailAndRefTypeAndTypeOnboard(cid, toEmail, refType, type);

            return ResponseUtils.handlerSuccess(list);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }

    }
}
