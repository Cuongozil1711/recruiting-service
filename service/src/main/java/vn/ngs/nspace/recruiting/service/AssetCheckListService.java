package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.AssetCheckList;
import vn.ngs.nspace.recruiting.model.RecruitmentPlanOrder;
import vn.ngs.nspace.recruiting.repo.AssetCheckListRepo;
import vn.ngs.nspace.recruiting.share.dto.AssetCheckListDTO;

import javax.transaction.Transactional;

@Service
@Transactional
public class AssetCheckListService {
    private final AssetCheckListRepo repo;
    private final ExecuteHcmService _hcmService;

    public AssetCheckListService(AssetCheckListRepo repo, ExecuteHcmService hcmService) {
        this.repo = (AssetCheckListRepo) repo;
        _hcmService = hcmService;
    }


    public void valid(AssetCheckListDTO dto){
    }

    public AssetCheckListDTO create(Long cid, String uid, AssetCheckListDTO dto) throws Exception {
//        valid(dto);
        AssetCheckList assetCheckList = AssetCheckList.of(cid, uid, dto);

        repo.save(assetCheckList);
        return MapperUtils.map(assetCheckList,dto);
    }

    public AssetCheckListDTO updateAssetChecklist(Long cid, Long id, AssetCheckListDTO assetCheckListDTO) {
        valid(assetCheckListDTO);
        AssetCheckList curr = repo.findByCompanyIdAndId(cid, id).orElse(new AssetCheckList());
        MapperUtils.copyWithoutAudit(assetCheckListDTO,curr);

        repo.save(curr);
        return MapperUtils.map(curr,AssetCheckListDTO.class);
    }
}
