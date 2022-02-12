package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.*;
import vn.ngs.nspace.recruiting.service.*;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;

@RestController
@RequestMapping("cost")
@RequiredArgsConstructor
@Tag(name = "Cost", description = "API for cost")
public class CostApi {

    private final CostService _service;
    private final CostRepo _repo;
    private final ExecuteHcmService _hcmService;

    @PostMapping("/summary")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all interview template"
            , description = "interview search by %name% format"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity summary(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload to search with positionId, orgId, ")
            @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        try{
            Long year = MapUtils.getLong(condition, "year", -1l);
            Long orgId = MapUtils.getLong(condition, "orgId", -1l);

            Page<Map<String, Object>> page = _repo.getSummaryByOrgAndYear(cid, orgId, year, pageable);
            List<Map<String, Object>> dtos = new ArrayList<>();
            Set<Long> orgIds = new HashSet<>();
            page.getContent().forEach(o -> {
                Map<String, Object> newData = new HashMap<>(o);
                if(newData.containsKey("org_id")){
                    orgIds.add(MapUtils.getLong(newData, "org_id"));
                }
                dtos.add(newData);
            });

            Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
            for(Map<String, Object> dto : dtos){
                dto.put("org", mapOrg.get(MapUtils.getLong(dto, "org_id")));
            }
            return ResponseUtils.handlerSuccess(new PageImpl(dtos, pageable, page.getTotalElements()));
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @GetMapping("/summary-by-org-and-year")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search all interview template"
            , description = "interview search by %name% format"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity summaryByOrgAndYear(
            @Parameter(description="ID of company") @RequestHeader("cid") long cid
            , @Parameter(description="ID of company") @RequestHeader("uid") String uid
            , @RequestParam(value = "orgId") Long orgId
            , @RequestParam(value = "year") Long year) {
        try{
            List<Cost> costs = _repo.findByCompanyIdAndOrgIdAndYearAndStatus(cid, orgId, year, Constants.ENTITY_ACTIVE);
            List<CostDTO> dtos = _service.toDTOs(cid, uid, costs);

            List<Map<String, Object>> datas = new ArrayList<>();
            for(CostDTO dto : dtos){
                Map<String, Object> splitMonth = _service.splitAmountTo12Months(cid, uid, dto);
                Map<String, Object> data = JsonObject.mapFrom(dto).getMap();
                data.putAll(splitMonth);
                datas.add(data);
            }
            return ResponseUtils.handlerSuccess(datas);
        }catch (Exception e){
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    @Operation(summary = "Create cost"
            , description = "API for create interview template"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity create(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="Payload DTO to create")  @RequestBody CostDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("/{id}")
    @ActionMapping(action = Permission.UPDATE)
    @Operation(summary = "Update cost"
            , description = "API for update cost"
            , tags = { "Cost" }
    )
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity update(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path")  @PathVariable(value = "id") Long id
            , @RequestBody CostDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, id, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Get cost by Id"
            , description = "API for get Cost by Id"
            , tags = { "Cost" }
    )

    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key"
            , schema = @Schema(implementation = String.class))
    protected ResponseEntity getById(
            @Parameter(description="ID of company")
            @RequestHeader("cid") long cid
            , @Parameter(description="ID of company")
            @RequestHeader("uid") String uid
            , @Parameter(description="param in path") @PathVariable(value = "id") Long id) {
        try {
            Cost cost = _repo.findByCompanyIdAndId(cid, id).orElse(new Cost());
            return ResponseUtils.handlerSuccess(_service.toDTOs(cid, uid, Collections.singletonList(cost)));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
