package vn.ngs.nspace.recruiting.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.service.EmailSettingService;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;

@RestController
@RequestMapping("email-setting")
public class EmailSettingApi {

    @Autowired
    EmailSettingService _service;
    private final EmailSettingRepo _repo;

    public EmailSettingApi(EmailSettingRepo repo) {
        _repo = repo;
    }

    @PostMapping("/all")
    @ActionMapping(action = Permission.VIEW)
    protected ResponseEntity search(@RequestHeader Long cid
            , @RequestHeader String uid) {
        try{
            return ResponseUtils.handlerSuccess(_repo.findByCompanyId(cid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping()
    @ActionMapping(action = Permission.CREATE)
    protected ResponseEntity create(@RequestHeader Long cid
            , @RequestHeader String uid
            , @RequestBody EmailSettingDTO request) {
        try {
            return ResponseUtils.handlerSuccess(_service.create(cid, uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }


    @PutMapping("{id}")
    @ActionMapping(action = Permission.UPDATE)
    protected ResponseEntity update(@RequestHeader("cid") long cid
            , @RequestHeader("uid") String uid
            , @PathVariable(value = "id") Long id
            , @RequestBody EmailSettingDTO request){
        try {
           return ResponseUtils.handlerSuccess(_service.update(cid, uid, id,request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
