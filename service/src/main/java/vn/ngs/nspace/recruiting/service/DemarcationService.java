package vn.ngs.nspace.recruiting.service;


import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.repo.DemarcationRepo;
import vn.ngs.nspace.recruiting.share.dto.DemarcationDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DemarcationService {

    private final DemarcationRepo demarcationRepo;

    public DemarcationService(DemarcationRepo demarcationRepo){
        this.demarcationRepo = demarcationRepo;
    }

    public DemarcationDTO update(Long cid, String uId, DemarcationDTO demarcationDTO){
        try {
            Demarcation demarcation = demarcationRepo.findById(demarcationDTO.getId()).get();
            if(demarcation == null) throw new BusinessException("No Find Demarcation");
            demarcation.setName(demarcationDTO.getName());
            demarcation.setOrgId(demarcationDTO.getOrgId());
            demarcation.setLevelId(demarcationDTO.getLevelId());
            demarcation.setTitleId(demarcationDTO.getTitleId());
            demarcation.setPositionId(demarcationDTO.getPositionId());
            demarcation.setDemarcationDate(demarcationDTO.getDemarcationDate());
            demarcation.setSumDemarcation(demarcationDTO.getSumDemarcation());
            demarcation.setUpdateBy(uId);
            demarcation.setCompanyId(cid);
            demarcation = demarcationRepo.save(demarcation);
            return toDTO(demarcation);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public DemarcationDTO create(Long cId, String uId, DemarcationDTO demarcationDTO){
        try {
            Demarcation demarcation = Demarcation.of(cId, uId, demarcationDTO);
            demarcation.setCreateBy(uId);
            demarcation.setCreateDate(new Date());
            demarcation.setStatus(Constants.ENTITY_ACTIVE);
            demarcation = demarcationRepo.save(demarcation);
            return toDTO(demarcation);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public void delete(Long cId, String uid, List<Long> ids){
        try {
            for(Long id: ids){
                Demarcation demarcation = demarcationRepo.findById(id).get();
                if(demarcation != null){
                    demarcation.setStatus(Constants.ENTITY_INACTIVE);
                    demarcation.setUpdateBy(uid);
                    demarcation.setCompanyId(cId);
                    demarcation.setModifiedDate(new Date());
                    demarcationRepo.save(demarcation);
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    /* convert model object to DTO before response */
    public DemarcationDTO toDTO(Demarcation demarcation){
        DemarcationDTO dto = MapperUtils.map(demarcation, DemarcationDTO.class);
        return dto;
    }
}
