package vn.ngs.nspace.recruiting.api.v2;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.ScheduleTypeService;
import vn.ngs.nspace.recruiting.share.dto.ScheduleTypeDTO;

import java.util.List;

@RestController
@RequestMapping("schedule-type")
@RequiredArgsConstructor
@Tag(name = "Schedule Type", description = "Schedule API")
public class ScheduleTypeApi {
    private final ScheduleTypeService _service;

    @GetMapping("/list")
    @ActionMapping(action = Permission.VIEW)
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getListSchedule(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestParam(value = "search", defaultValue = "") String search
            , Pageable page
    ) {
        try {
            Page<ScheduleTypeDTO> datas = _service.getPageSchedule(search, page);
            return ResponseUtils.handlerSuccess(datas);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Create list ScheduleType"
            , description = "Create list ScheduleType"
            , tags = {"ScheduleType"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity createScheduleType(
            @RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody ScheduleTypeDTO scheduleTypeDTO
    ) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, scheduleTypeDTO));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PutMapping("/delete")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "delete list Schedule type",
            description = "API for delete list Schedule type")
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    private ResponseEntity deteleList(
            @Parameter(description = "ID of company")
            @RequestHeader Long cid
            ,@Parameter(description = "ID of userID")
            @RequestHeader String uid
            , @RequestBody List<Long> ids){
        try {
            _service.delete(cid, uid , ids);
            return ResponseUtils.handlerSuccess();
        } catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }
}
