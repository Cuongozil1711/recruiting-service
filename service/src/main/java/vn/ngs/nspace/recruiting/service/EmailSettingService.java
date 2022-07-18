package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSettingRepo;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmailSettingService {
    private final EmailSettingRepo repo;

    public EmailSettingService(EmailSettingRepo repo) {
        this.repo = repo;
    }

    /* create object */
    public EmailSettingDTO create(Long cid, String uid, EmailSettingDTO request){
        try {
            EmailSetting obj = EmailSetting.of(cid, uid, request);
            obj.setStatus(Constants.ENTITY_ACTIVE);
            obj.setCreateBy(uid);
            obj.setUpdateBy(uid);
            obj.setCompanyId(cid);
            obj = repo.save(obj);

            return MapperUtils.map(obj, EmailSettingDTO.class);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    /* update by id object */
    public EmailSettingDTO update(Long cid, String uid, Long id, EmailSettingDTO request)  {
        try {
            EmailSetting curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(EmailSetting.class, id));
            MapperUtils.copyWithoutAudit(request, curr);
            curr.setUpdateBy(uid);
            curr = repo.save(curr);

            return MapperUtils.map(curr, EmailSettingDTO.class);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }
}
