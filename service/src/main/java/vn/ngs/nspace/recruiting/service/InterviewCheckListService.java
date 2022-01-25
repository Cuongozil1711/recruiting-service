package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.model.ProfileCheckList;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;

@Service
@Transactional
public class InterviewCheckListService {
    private final InterviewCheckListRepo repo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewCheckListTemplateRepo templateRepo;

    public InterviewCheckListService(InterviewCheckListRepo repo, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateRepo templateRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.templateRepo = templateRepo;
    }

    public void valid (InterviewCheckListDTO dto){

    }

    public InterviewCheckListDTO create(Long cid, String uid, InterviewCheckListDTO request) throws BusinessException {
        valid(request);
        InterviewCheckList exists = repo.findByCompanyIdAndCheckListIdAndInterviewerIdAndStatus(cid, request.getCheckListId(), request.getInterviewerId(), Constants.ENTITY_ACTIVE).orElse(new InterviewCheckList());
        if(!exists.isNew()){
            return toDTO(exists);
        }
        InterviewCheckList obj = InterviewCheckList.of(cid, uid, request);
        obj.setStatus(Constants.ENTITY_ACTIVE);
        obj.setCompanyId(cid);
        obj.setUpdateBy(uid);
        obj.setCreateBy(uid);

        return toDTO(repo.save(obj));
    }

    public InterviewCheckListDTO toDTO(InterviewCheckList obj){
        return MapperUtils.map(obj, InterviewCheckListDTO.class);
    }

}
