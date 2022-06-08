package vn.ngs.nspace.recruiting.service.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.recruiting.model.RecruitmentChannel;
import vn.ngs.nspace.recruiting.repo.RecruitmentChannelRepo;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecruitmentChannelV2Service {

    private final RecruitmentChannelRepo recruitmentChannelRepo;

    public List<RecruitmentChannel> getByCompanyId(Long cid, String uid) {
        return recruitmentChannelRepo.findByCompanyIdAndStatus(cid, Constants.ENTITY_ACTIVE);
    }

}
