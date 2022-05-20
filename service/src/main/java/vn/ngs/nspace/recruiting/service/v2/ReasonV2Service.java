package vn.ngs.nspace.recruiting.service.v2;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Reason;
import vn.ngs.nspace.recruiting.repo.ReasonRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReasonV2Service {
    private final ReasonRepo repo;
    private final ExecuteHcmService hcmService;
    private final ExecuteConfigService configService;

    public ReasonV2Service(ReasonRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.hcmService = hcmService;
        this.configService = configService;
    }

    public ReasonDTO create(Long cid, String uid, ReasonDTO reasonDTO) {
        return null;
    }

    public ReasonDTO update(Long cid, String uid, ReasonDTO reasonDTO) {
        return null;
    }

    public Page<ReasonDTO> getPage(Long cid, String uid, Pageable pageable) {
        return null;
    }

    public ReasonDTO getById(Long cid, String uid, Long id) {
        return null;
    }

    public ReasonDTO toDTO(Reason reason) {
        return MapperUtils.map(reason, ReasonDTO.class);
    }

    public List<ReasonDTO> toDTOs(List<Reason> reasons) {

        return reasons.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
