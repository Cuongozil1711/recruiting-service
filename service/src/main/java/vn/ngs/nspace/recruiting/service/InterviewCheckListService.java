package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class InterviewCheckListService {
    private final InterviewCheckListRepo repo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewResultRepo resultRepo;

    public InterviewCheckListService(InterviewCheckListRepo repo, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateRepo templateRepo, InterviewResultRepo resultRepo) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.templateRepo = templateRepo;
        this.resultRepo = resultRepo;
    }

    public void valid (InterviewCheckListDTO dto){
        if(dto.getInterviewerId() == null){
            throw new BusinessException("invalid-interviewer");
        }
        if(dto.getCheckListId() == null){
            throw new BusinessException("invalid-checklist");
        }
        if(dto.getInterviewResultId() == null){
            throw new BusinessException("invalid-interview-result");
        }
        if(dto.getRating() == null){
            throw new BusinessException("invalid-rating");
        }
        if(StringUtils.isEmpty(dto.getResult())){
            throw new BusinessException("invalid-result");
        }
        if(dto.getInterviewDate() == null){
            throw new BusinessException("invalid-interview-date");
        }

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


    public InterviewCheckListDTO createByInterviewResult(Long cid, String uid, InterviewResult interviewResult) {

//        Date interviewDate = new Date();
//        Long id = interviewResult.getId();
//        Long orgId = interviewResult.
//
//        InterviewCheckList checkList = resultRepo.getInterviewResult(cid, id).orElseThrow(()-> new BusinessException("not found Result"));
//
//        return createByPositionOrg(cid, uid, checkList.getCheckListId(), checkList.getOrgId(), job.getEmployeeId(), interviewDate, "", 0l);
    return null;

    }


    public List<InterviewCheckListDTO> createByPositionOrg(long cid, String uid, Long positionId, Long orgId, Long interviewerId, Double rating, Date interviewDate, String result) {
        List<InterviewCheckListDTO> checkLists = new ArrayList<>();
        List<InterviewCheckListTemplate> templates = templateRepo.searchConfigTemplate(cid, positionId, orgId);

        InterviewCheckListTemplate template = templates.get(0);
        List<InterviewCheckListTemplateItem> items = itemRepo.findByCompanyIdAndTemplateId(cid, template.getId());
        for (InterviewCheckListTemplateItem item: items ) {

            InterviewCheckListDTO checkListDTO = new InterviewCheckListDTO();
            checkListDTO = MapperUtils.map(item, checkListDTO);
            checkListDTO.setInterviewerId(interviewerId);
            checkListDTO.setInterviewDate(interviewDate);
            checkListDTO.setRating(rating);
            checkListDTO.setResult(result);

            checkLists.add(create(cid, uid, checkListDTO));
        }
        return checkLists;
    }

    public InterviewCheckListDTO handOverProfile(long cid, String uid, Long checkListId, Long interviewerId, Date interviewDate) {

        if(checkListId == null){
            throw new BusinessException("invalid-checklist");
        }
        if(interviewerId == null){
            throw new BusinessException("invalid-employeeId");
        }
        InterviewCheckList curr = repo.findByCompanyIdAndCheckListIdAndInterviewerId(cid, checkListId, interviewerId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewerId));;

        curr = repo.save(curr);
        return  toDTO(curr);
    }


    public InterviewCheckListDTO toDTO(InterviewCheckList obj){
        return MapperUtils.map(obj, InterviewCheckListDTO.class);
    }

}
