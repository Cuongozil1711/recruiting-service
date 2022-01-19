package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Candidate;
import vn.ngs.nspace.recruiting.model.CandidateFilter;
import vn.ngs.nspace.recruiting.repo.CandidateFilterRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateDTO;
import vn.ngs.nspace.recruiting.share.dto.CandidateFilterDTO;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CandidateFilterService {
    private final CandidateFilterRepo repo;
    private final ExecuteHcmService hcmService;
    private final ExecuteConfigService configService;

    public CandidateFilterService(CandidateFilterRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.hcmService = hcmService;
        this.configService = configService;
    }

    public void valid(CandidateFilter dto) throws BusinessException {

    }

    public CandidateFilterDTO create(Long cid, String uid, CandidateFilter dto) throws BusinessException {
        valid(dto);
        CandidateFilter candidateFilter = CandidateFilter.of(cid,uid,dto);
        candidateFilter.setStatus(Constants.ENTITY_ACTIVE);
        candidateFilter.setCompanyId(cid);
        candidateFilter.setCreateBy(uid);
        candidateFilter.setUpdateBy(uid);
        candidateFilter = repo.save(candidateFilter);


        return toDTO(candidateFilter);
    }

    public CandidateFilterDTO update(Long cid, String uid, Long id, CandidateFilter dto) throws BusinessException{
        valid(dto);
        CandidateFilter curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(CandidateFilter.class, id));
        MapperUtils.copyWithoutAudit(dto,curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);
        return toDTO(curr);

    }



    public CandidateFilterDTO toDTO(CandidateFilter candidateFilter){
        CandidateFilterDTO dto = MapperUtils.map(candidateFilter, CandidateFilterDTO.class);
        return dto;
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            CandidateFilter filter = repo.findByCompanyIdAndId(cid, i).orElse(new CandidateFilter());
            if(!filter.isNew()){
                filter.setStatus(vn.ngs.nspace.recruiting.utils.Constants.ENTITY_INACTIVE);
                filter.setUpdateBy(uid);
                filter.setModifiedDate(new Date());

                repo.save(filter);
            }
        });

    }
}
