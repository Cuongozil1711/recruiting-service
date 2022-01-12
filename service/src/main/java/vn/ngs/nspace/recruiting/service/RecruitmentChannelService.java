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
        RecruitmentChannel curr = new RecruitmentChannel();
        if (request.getId() != null){
            curr = repo.findByCompanyIdAndId(cid, request.getId()).orElseThrow(() -> new EntityNotFoundException(RecruitmentChannel.class,request));
        }
//        if(!StringUtils.isEmpty(request.getCode())){
//            curr = repo.findByCompanyIdAndCode(cid, request.getCode()).orElse(new RecruitmentChannel());
//        }
        if(curr.isNew()){
            curr.setCompanyId(cid);
            curr.setCreateBy(uid);
        }
        curr.setUpdateBy(uid);
        MapperUtils.copyWithoutAudit(request, curr);
        return MapperUtils.map(repo.save(curr), RecruitmentChannelDTO.class);
    }
}
