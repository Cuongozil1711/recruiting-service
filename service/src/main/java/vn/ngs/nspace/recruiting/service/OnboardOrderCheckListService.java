package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.model.OnboardOrderCheckList;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.OnboardOrderDTO;

import javax.transaction.Transactional;

@Service
@Transactional
public class OnboardOrderCheckListService {
    private final OnboardOrderCheckListRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public OnboardOrderCheckListService(OnboardOrderCheckListRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public OnboardOrderCheckListDTO changeState(Long cid, String ui, OnboardOrderCheckListDTO dto){
        OnboardOrderCheckList curr = repo.findByCompanyIdAndIdAndEmployeeId(cid, dto.getId(), dto.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException(OnboardOrderCheckList.class, dto.getEmployeeId()));
        curr.setState(dto.getState());
        repo.save(curr);
        return toDTO(curr);
    }

    public OnboardOrderCheckListDTO toDTO(OnboardOrderCheckList obj){
        OnboardOrderCheckListDTO dto = MapperUtils.map(obj, OnboardOrderCheckListDTO.class);
        return dto;
    }
}
