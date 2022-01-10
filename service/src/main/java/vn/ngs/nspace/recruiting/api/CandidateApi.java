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
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.service.CandidateService;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("candidate")
@RequiredArgsConstructor
public class CandidateApi {
    private final CandidateService _service;
    private final CandidateRepo _repo;

    @PostMapping("/search")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity search(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody Map<String, Object> condition
            , Pageable pageable) {
        return ResponseUtils.handlerException(new BusinessException("nothing"));
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody CandidateDTO dto) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody List<CandidateDTO> dtos) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, dtos));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity updateById(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody CandidateDTO dto){
        try {
           return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,dto));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("{id}")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity getById(@RequestHeader("cid") long cid
        , @RequestHeader("uid") String uid
        , @PathVariable(value = "id") Long id){
        try {
            Candidate candidate = _repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
            return ResponseUtils.handlerSuccess(_service.toDTO(candidate));
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

}
