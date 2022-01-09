package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.repo.CandidateRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;

import javax.transaction.Transactional;

@Service
@Transactional
public class CandidateService {
    private final CandidateRepo repo;
    private final ExecuteHcmService _hcmService;

    public CandidateService(CandidateRepo repo, ExecuteHcmService hcmService) {
        this.repo = repo;
        _hcmService = hcmService;
    }

    public void valid(CandidateDTO dto) throws BusinessException {
    }

    public CandidateDTO create(Long cid, String uid, CandidateDTO dto) throws BusinessException {
        valid(dto);
        Candidate candidate = Candidate.of(cid, uid, dto);
        candidate.setCreateBy(uid);
        repo.save(candidate);
        return toDTO(candidate);
    }

    public CandidateDTO update(Long cid, String uid, Long id, CandidateDTO dto) throws BusinessException {
        valid(dto);
        Candidate curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Candidate.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr = repo.save(curr);
        return toDTO(curr);
    }

    public CandidateDTO toDTO(Candidate candidate){
        CandidateDTO dto = MapperUtils.map(candidate, CandidateDTO.class);
        return dto;
    }
}
