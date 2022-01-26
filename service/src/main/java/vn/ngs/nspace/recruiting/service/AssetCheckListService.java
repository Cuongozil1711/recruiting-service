package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.repo.AssetCheckListRepo;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AssetCheckListService {
    private final AssetCheckListRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public AssetCheckListService(AssetCheckListRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(AssetCheckListDTO dto){
        if(dto.getAssetId() == null){
            throw new BusinessException("invalid-asset");
        }
        if (dto.getEmployeeId() == null){
            throw new BusinessException("invalid-employee");
        }
    }

    public AssetCheckListDTO create(Long cid, String uid, AssetCheckListDTO request) throws BusinessException {
        valid(request);
        AssetCheckList assetCheckList = AssetCheckList.of(cid, uid, request);
        assetCheckList.setCompanyId(cid);
        assetCheckList.setCreateBy(uid);
        assetCheckList.setUpdateBy(uid);
        assetCheckList.setStatus(Constants.ENTITY_ACTIVE);

        assetCheckList = repo.save(assetCheckList);
        return toDTO(assetCheckList);
    }

    public AssetCheckListDTO update(Long cid, String uid, Long id, AssetCheckListDTO request) throws BusinessException{
        valid(request);
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
        List<AssetCheckList> assetCheckLists = repo.findByCompanyIdAndOnboardOrderId(cid, onboardId);

        List<Long> listAssetIdOfDto = listDTOS.stream().map(dto -> dto.getAssetId()).collect(Collectors.toList());
        List<Long> assetIdExists = assetCheckLists.stream().map(dto -> dto.getAssetId()).collect(Collectors.toList());

        List<Long> listAssetIdForCreate = new ArrayList<>(listAssetIdOfDto);

        listAssetIdForCreate.removeAll(assetIdExists); // loai bo danh sach asset da ton tai

        List<AssetCheckList> listOfAssetCheckList = new ArrayList<>(); // Tao 1 array de luu tru ban ghi can luu vao Database

        // Tao moi danh sach AssetCheckList
        for (Long assetId : listAssetIdForCreate) {
            AssetCheckList assetCheckList = new AssetCheckList();

            AssetCheckListDTO dto = listDTOS.stream().filter(el -> el.getAssetId() == assetId).collect(Collectors.toList()).get(0);

            if (dto != null) {
                assetCheckList.setCompanyId(cid);
                assetCheckList.setAssetId(assetId);
                assetCheckList.setEmployeeId(dto.getEmployeeId());
                assetCheckList.setReceiptDate(dto.getReceiptDate());

                listOfAssetCheckList.add(assetCheckList);
            }
        }
        // Ket thuc tao moi

        // Update danh sach AssetCheckList
        List<Long> listAssetIdForUpdate = new ArrayList<>(listAssetIdOfDto);

        listAssetIdForUpdate.retainAll(assetIdExists); // lay danh sach asset da ton tai

        for (Long assetId : listAssetIdForUpdate) {
            AssetCheckList assetCheckList = assetCheckLists.stream().filter(el -> el.getAssetId() == assetId).collect(Collectors.toList()).get(0);

            if (assetCheckList != null) {
                assetCheckList.setReceiptDate(new Date());

                listOfAssetCheckList.add(assetCheckList);
            }
        }
        // Ket thuc update

        // Luu vao Database
        if (listOfAssetCheckList != null && !listOfAssetCheckList.isEmpty()) {
            listOfAssetCheckList = repo.saveAll(listOfAssetCheckList);
        }

        return toDTOs(cid, uid, listOfAssetCheckList);
    }

    public AssetCheckListDTO toDTOWithObj (Long cid, String uid,  AssetCheckList obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }
    public AssetCheckListDTO toDTO(AssetCheckList obj){
        return MapperUtils.map(obj, AssetCheckListDTO.class);
    }
}
