package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.mapping.Map;
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
import vn.ngs.nspace.recruiting.share.dto.RecruitmentChannelDTO;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class RecruitmentChannelService {
    private final RecruitmentChannelRepo repo;

    public RecruitmentChannelService(RecruitmentChannelRepo repo) {
        this.repo = repo;
    }


    public RecruitmentChannelDTO createOrUpdate(Long cid, String uid, RecruitmentChannelDTO request){
        if (request.getId() != null){
            RecruitmentChannel curr = repo.findByCompanyIdAndId(cid, request.getId()).orElseThrow(() -> new EntityNotFoundException(Candidate.class,request));
            MapperUtils.copyWithoutAudit(request,curr );
            if (curr.isNew()){
                curr.setUpdateBy(uid);
            }

            curr = repo.save(curr);

        }
        else {
            RecruitmentChannel recruitmentChannel = RecruitmentChannel.of(cid, uid, request);
            recruitmentChannel.setCompanyId(cid);
            recruitmentChannel.setCreateBy(uid);

            repo.save(recruitmentChannel);
        }
        return request;
    }

}
