package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.EmailSent;
import vn.ngs.nspace.recruiting.model.EmailSetting;
import vn.ngs.nspace.recruiting.repo.EmailSentRepo;
import vn.ngs.nspace.recruiting.share.dto.EmailSettingDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;

@Service
@Transactional
public class EmailSentService {
    private final EmailSentRepo repo;

    public EmailSentService(EmailSentRepo repo) {

        this.repo = repo;
    }

    /* create object */
    public EmailSent create(Long cid, String uid, EmailSent request) throws BusinessException {
        EmailSent obj = EmailSent.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCreateBy(uid);
        obj.setUpdateBy(uid);
        obj.setCompanyId(cid);
        obj = repo.save(obj);

        return obj;
    }
}
