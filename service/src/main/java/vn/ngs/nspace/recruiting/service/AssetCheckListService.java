package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.AssetCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class AssetCheckListService {
    private final AssetCheckListRepo repo;
    private final OnboardOrderRepo orderRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public AssetCheckListService(AssetCheckListRepo repo, OnboardOrderRepo orderRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.orderRepo = orderRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(Long cid, AssetCheckListDTO dto){
        orderRepo.findByCompanyIdAndId(cid, dto.getOnboardOrderId()).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, dto.getOnboardOrderId()));
        if(dto.getEmployeeId() == null || dto.getEmployeeId() == 0l){
            throw new BusinessException("employee-not-found");
        }
        if(dto.getAssetId() == null || dto.getAssetId() == 0l){
            throw new BusinessException("asset-not-found");
        }
    }

    public List<AssetCheckListDTO> create(Long cid, String uid, List<AssetCheckListDTO> listDTOS) throws BusinessException{
        List<AssetCheckList> list = new ArrayList<>();
        for (AssetCheckListDTO dto: listDTOS) {
            valid(cid, dto);
            AssetCheckList curr = AssetCheckList.of(cid, uid, dto);
            curr.setCompanyId(cid);
            curr.setCreateBy(uid);
            curr.setUpdateBy(uid);
            curr.setStatus(Constants.ENTITY_ACTIVE);

            curr = repo.save(curr);

            list.add(curr);
        }
        return toDTOs(cid, uid, list);
    }

    public AssetCheckListDTO update(Long cid, String uid, Long id, AssetCheckListDTO request) throws BusinessException{
        valid(cid, request);
        AssetCheckList curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(AssetCheckList.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);

        curr = repo.save(curr);
        return toDTO(curr);
    }

    public List<AssetCheckListDTO> toDTOs(Long cid, String uid, List<AssetCheckList> objs){
        List<AssetCheckListDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        objs.forEach(o -> {
            if(o.getAssetId() != null){
                categoryIds.add(o.getAssetId());
            }
            if(o.getEmployeeId() != null){
                employeeIds.add(o.getEmployeeId());
            }
            if(o.getSenderId() != null){
                employeeIds.add(o.getSenderId());
            }

            dtos.add(toDTO(o));
        });
        List<EmployeeDTO> employeeDTOS = _hcmService.getEmployees(uid, cid, employeeIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);

        for(AssetCheckListDTO dto : dtos){
            if(dto.getAssetId() != null){
                dto.setAssetObj(mapCategory.get(dto.getAssetId()));
            }
            if(dto.getEmployeeId() != null){
                dto.setEmployeeObj(employeeDTOS.stream().filter(e -> {
                    return CompareUtil.compare(e.getId(), dto.getEmployeeId());
                }).findAny().orElse(null) );
            }
            if(dto.getSenderId() != null){
                dto.setSenderObj(employeeDTOS.stream().filter(e -> {
                    return CompareUtil.compare(e.getId(), dto.getSenderId());
                }).findAny().orElse(null) );
            }
        }

        return dtos;
    }

    public List<AssetCheckListDTO> handOverAsset(Long cid, String uid, Long onboardId, List<AssetCheckListDTO> listDTOS) {
        List<AssetCheckListDTO> returnData = new ArrayList<>();
        for(AssetCheckListDTO dto : listDTOS){
            dto.setOnboardOrderId(onboardId);
            if(dto.getId() != null && dto.getId() != 0l){
                repo.findByCompanyIdAndId(cid, dto.getId()).orElseThrow(() -> new EntityNotFoundException(AssetCheckList.class, dto.getId()));
                if(!StringUtils.isEmpty(dto.getCmd()) && dto.getCmd().equals(Constants.CMD_TABLE_ACTION.DELETE.toString())){
                    dto.setStatus(Constants.ENTITY_INACTIVE);
                }
                update(cid, uid, dto.getId(), dto);
            }else{

                create(cid, uid, Collections.singletonList(dto));
            }
            if(StringUtils.isEmpty(dto.getCmd()) || !dto.getCmd().equals(Constants.CMD_TABLE_ACTION.DELETE.toString())){
                returnData.add(dto);
            }
        }

        return returnData;
    }

    public AssetCheckListDTO toDTOWithObj (Long cid, String uid,  AssetCheckList obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }
    public AssetCheckListDTO toDTO(AssetCheckList obj){
        return MapperUtils.map(obj, AssetCheckListDTO.class);
    }
}
