package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.model.RecruitmentChannel;
import vn.ngs.nspace.recruiting.repo.CandidateFilterRepo;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentChannelRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class RecruitmentChannelService {
    private final RecruitmentChannelRepo repo;

    public RecruitmentChannelService(RecruitmentChannelRepo repo) {
        this.repo = repo;
    }

    /* update by id object */
    public RecruitmentChannel update(Long cid, String uid, RecruitmentChannel request) throws BusinessException {
        if(request.getId() != null && request.getId() != 0l){
            request = repo.findByCompanyIdAndId(cid, request.getId()).orElse(new RecruitmentChannel());
        }
        RecruitmentChannel obj = RecruitmentChannel.of(cid, uid, request);
        if(obj.isNew()){
            obj.setCreateBy(uid);
        }
        obj.setUpdateBy(uid);
        return repo.save(obj);
    }
}
