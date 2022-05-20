package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.recruiting.repo.InterviewInvolveRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.InterviewInvolveDTO;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class InterviewInvolveV2Service {
    private final InterviewInvolveRepo repo;
    private final ExecuteHcmService hcmService;
    private final ExecuteConfigService configService;

    public InterviewInvolveV2Service(InterviewInvolveRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.hcmService = hcmService;
        this.configService = configService;
    }


    public Page<InterviewInvolveDTO> getPage(Long cid, String uid, String search, Page page) {
        return null;
    }

    public InterviewInvolveDTO create(Long cid, String uid, InterviewInvolveDTO interviewInvolveDTO) {
        return null;
    }

    public InterviewInvolveDTO update(Long cid, String uid, InterviewInvolveDTO interviewInvolveDTO) {
        return null;
    }

    public InterviewInvolveDTO getById(String uid, Long cid, Long id) {
        return null;
    }

    public void delete(Long cid, String uid, List<Long> ids) {

    }
}
