package vn.ngs.nspace.recruiting.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.annotation.ActionMapping;
import vn.ngs.nspace.lib.utils.MapUtils;
import vn.ngs.nspace.lib.utils.ResponseUtils;
import vn.ngs.nspace.policy.utils.Permission;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.UserSetting;
import vn.ngs.nspace.recruiting.repo.CostRepo;
import vn.ngs.nspace.recruiting.repo.UserSettingRepo;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;

@RestController
@RequestMapping("user-setting")
@RequiredArgsConstructor
@Tag(name = "UserSetting", description = "API for User Setting")
public class UserSettingApi {
    private final UserSettingRepo _userSettingRepo;

    @PutMapping("")
    public ResponseEntity updateUserConfig(@RequestHeader String uid, @RequestHeader Long cid
            , @RequestBody Map<String, Object> config){
        try{
            UserSetting userSetting = _userSettingRepo
                    .findByCompanyIdAndUserId(cid, uid).orElse(new UserSetting());
            if(userSetting.isNew()){
                userSetting.setUserId(uid);
                userSetting.setCompanyId(cid);
            }
            Map<String, Object> currentConfig = userSetting.getConfig();
            if(currentConfig == null){
                currentConfig = new HashMap<>();
            }
            currentConfig.putAll(config);
            userSetting.setConfig(currentConfig);
            _userSettingRepo.save(userSetting);
            return ResponseUtils.handlerSuccess(HttpStatus.OK);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("")
    public ResponseEntity getUserConfig(@RequestHeader String uid, @RequestHeader Long cid){
        try{
            UserSetting userSetting = _userSettingRepo
                    .findByCompanyIdAndUserId(cid, uid).orElse(new UserSetting());
            return ResponseUtils.handlerSuccess(userSetting);
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
