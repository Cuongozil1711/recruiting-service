package vn.ngs.nspace.recruiting.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Demarcation;
import vn.ngs.nspace.recruiting.repo.DemarcationRepo;
import vn.ngs.nspace.recruiting.share.dto.DemarcationDTO;
import vn.ngs.nspace.recruiting.share.dto.DemarcationSearchDTO;
import vn.ngs.nspace.recruiting.share.dto.DemarcationSearchResponseDto;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
            List<Demarcation> demarcationList = demarcationRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndStatus(demarcationDTO.getOrgId(), demarcationDTO.getLevelId(), demarcationDTO.getTitleId(), demarcationDTO.getPositionId(), 1);
            for(Demarcation demarcation : demarcationList){
                if(demarcation.getDemarcationDate().getYear() == demarcationDTO.getDemarcationDate().getYear() &&
                        demarcation.getDemarcationDate().getMonth() == demarcationDTO.getDemarcationDate().getMonth()
                ){
                    throw new BusinessException("Định biên trong tháng đã được tạo");
                }
            }
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

    public List<DemarcationSearchResponseDto> search(DemarcationSearchDTO demarcationSearchDTO, Pageable pageable){
        try {
            List<Map<String, Object>> dataSearch = demarcationRepo.search(
                    demarcationSearchDTO.getOrgId(),
                    demarcationSearchDTO.getLevelId(),
                    demarcationSearchDTO.getPositionId(),
                    demarcationSearchDTO.getTitleId(),
                    demarcationSearchDTO.getDateDemarcation(),
                    pageable
            );
            List<DemarcationSearchResponseDto> dtoList = new ArrayList<>();
            for(int i = 0; i < dataSearch.size(); i++){
                Map<String, Object> itemData = dataSearch.get(i);
                DemarcationSearchResponseDto responseDto = new DemarcationSearchResponseDto();
                responseDto.setOrgId(Long.valueOf(itemData.get("orgId").toString()));
                responseDto.setLevelId(Long.valueOf(itemData.get("levelId").toString()));
                responseDto.setTitleId(Long.valueOf(itemData.get("titleID").toString()));
                responseDto.setPositionId(Long.valueOf(itemData.get("positionId").toString()));
                responseDto.setSumDemarcation(Integer.valueOf(itemData.get("sumDemarcation").toString()));
                responseDto.setDateDemarcationYear(demarcationSearchDTO.getDateDemarcation());
                Integer[] array = new Integer[12];
                Long[] arrayId = new Long[12];
                for(int ii = 0; ii < 12; ii ++){
                    array[ii] = findSumDemarcationForMonth(responseDto.getOrgId(), responseDto.getLevelId(), responseDto.getTitleId(), responseDto.getPositionId(), ii);
                    arrayId[ii] = findSumDemarcationForId(responseDto.getOrgId(), responseDto.getLevelId(), responseDto.getTitleId(), responseDto.getPositionId(), ii);
                }
                responseDto.setSumDemarcationForMonth(array);
                responseDto.setDemarcationId(arrayId);
                dtoList.add(responseDto);
            }
            return dtoList;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Integer findSumDemarcationForMonth(Long orgId, Long levelId, Long titleId, Long positionId, int month) {
        try {
            List<Demarcation> demarcationList = demarcationRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndStatus(orgId, levelId, titleId, positionId, 1);
            for(Demarcation demarcation : demarcationList){
                if(demarcation.getDemarcationDate().getMonth() == month){
                    return demarcation.getSumDemarcation();
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
        return 0;
    }

    public Long findSumDemarcationForId(Long orgId, Long levelId, Long titleId, Long positionId, int month) {
        try {
            List<Demarcation> demarcationList = demarcationRepo.findAllByOrgIdAndLevelIdAndTitleIdAndPositionIdAndStatus(orgId, levelId, titleId, positionId, 1);
            for(Demarcation demarcation : demarcationList){
                if(demarcation.getDemarcationDate().getMonth() == month){
                    return demarcation.getId();
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
        return 0l;
    }
}
