package vn.ngs.nspace.recruiting.api;

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
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.service.RecruitmentChannelService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("recruitment-channel")
@RequiredArgsConstructor
public class RecruitmentChannelApi {
    private final RecruitmentChannelService _service;

    @PutMapping("/update")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity updateFilter(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @RequestBody RecruitmentChannel request){
        try {
            return ResponseUtils.handlerSuccess(_service.update(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

}
