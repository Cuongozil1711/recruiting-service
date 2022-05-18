package vn.ngs.nspace.recruiting.api.v2;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.schedule.ScheduleTaskCommand;
import vn.ngs.nspace.recruiting.service.ExecuteNoticeService;
import vn.ngs.nspace.recruiting.service.v2.ScheduleEmailSentService;

import java.util.Date;
import java.util.Map;


@RestController
@RequestMapping("v2")
@RequiredArgsConstructor
@Tag(name = "ScheduleSentEmail", description = "Schedule Sent email API")
public class ScheduleEmailSentV2Api {

    private final ScheduleEmailSentService service;
    private final ExecuteNoticeService noticeService;

    @PostMapping("/schedule-test")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get Email Sent by ID"
            , description = "Get Email Sent by ID"
            , tags = {"Email"}
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity autoSendEmail(
            @Parameter(description = "Id of Company") @RequestHeader Long cid
            , @Parameter(description = "Id of User") @RequestHeader String uid
            , @Parameter(description = "Payload of record") @RequestBody Map<String, Object> payload) {
        try {
            Date schedule_date = MapUtils.getDate(payload, "date");
            ScheduleTaskCommand scheduleAction = new ScheduleTaskCommand();
            scheduleAction.setCompanyId(cid);
            scheduleAction.setEvent("schedule_mail");
            scheduleAction.setAction("schedule_mail");
            scheduleAction.setExecuteTime(schedule_date);
            scheduleAction.setTaskId(182L);
            scheduleAction.setActionId(1791866326582272L);
            service.createEmailSchedule(scheduleAction);

            return ResponseUtils.handlerSuccess(schedule_date);
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
