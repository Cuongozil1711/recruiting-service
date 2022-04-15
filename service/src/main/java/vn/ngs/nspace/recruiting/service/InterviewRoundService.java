package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewRound;
import vn.ngs.nspace.recruiting.repo.InterviewRoundRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewRoundDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;

@Service
@Transactional
public class InterviewRoundService {
    private final InterviewRoundRepo repo;

    public InterviewRoundService(InterviewRoundRepo repo) {
        this.repo = repo;
    }

    /* create object */
    public InterviewRoundDTO create(Long cid, String uid, InterviewRoundDTO request) throws BusinessException {
        InterviewRound obj = InterviewRound.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCreateBy(uid);
        obj.setUpdateBy(uid);
        obj.setCompanyId(cid);
        obj = repo.save(obj);
        return MapperUtils.map(obj, InterviewRoundDTO.class);
    }

    /* update by id object */
    public InterviewRoundDTO update(Long cid, String uid, Long id, InterviewRoundDTO request) throws BusinessException {
        InterviewRound curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(InterviewRound.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        return MapperUtils.map(curr, InterviewRoundDTO.class);
    }
}
