package vn.ngs.nspace.recruiting.api.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.service.v2.RecruitmentNewsService;
import vn.ngs.nspace.recruiting.share.request.RecruitmentNewsFilterRequest;

@RestController
@RequestMapping("recruitment-news")
@RequiredArgsConstructor
@Tag(name = "Recruitment news", description = "Recruitment news")
public class RecruitmentNewsApi {

    private final RecruitmentNewsService recruitmentNewsService;

    @PostMapping("/list")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search recruitment news",
            description = "Search recruitment news",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity getListRecruitmentNews(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
            @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
            @RequestBody RecruitmentNewsFilterRequest request,
            Pageable page
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentNewsService.searchRecruitmentNews(cid, uid, request, page));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

    @PostMapping("/detail/{id}")
    @ActionMapping(action = Permission.VIEW)
    @Operation(summary = "Search recruitment news",
            description = "Search recruitment news",
            tags = {"Recruitment request"})
    @Parameter(in = ParameterIn.HEADER, description = "Addition Key to bypass authen", name = "key",
            schema = @Schema(implementation = String.class))
    public ResponseEntity detailRecruitmentNews(
            @Parameter(description = "Id of Company") @RequestHeader("cid") long cid,
            @Parameter(description = "Id of User") @RequestHeader("uid") String uid,
            @PathVariable(value = "id") Long id
    ) {
        try {
            return ResponseUtils.handlerSuccess(recruitmentNewsService.detailRecruitmentNews(cid, uid, id));
        } catch (Exception e) {
            return ResponseUtils.handlerException(e);
        }
    }

}
