package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.CostV2Service;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;

import java.util.List;

@RestController
@RequestMapping("v2/cost")
@RequiredArgsConstructor
public class CostV2Api {

    private final CostV2Service costV2Service;

    @ActionMapping(action = Permission.CREATE)
    @PostMapping("")
    protected ResponseEntity create(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody List<CostDTO> costDTOs
    ) {
        try {
            List<CostDTO> costDTOS = costV2Service.creates(cid, uid, costDTOs);
            return  ResponseUtils.handlerSuccess(costDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @ActionMapping(action = Permission.UPDATE)
    @PutMapping("")
    protected ResponseEntity update(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid
            , @Parameter(description = "Id of User") @RequestHeader("uid") String uid
            , @RequestBody List<CostDTO> costDTOs
    ) {
        try {
            List<CostDTO> costDTOS = costV2Service.updates(cid, uid, costDTOs);
            return  ResponseUtils.handlerSuccess(costDTOS);
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
