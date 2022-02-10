package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateItemRepo;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListTemplateRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.ProfileCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class InterviewCheckListService {
    private final InterviewCheckListRepo repo;
    private final InterviewCheckListTemplateItemRepo itemRepo;
    private final InterviewCheckListTemplateRepo templateRepo;
    private final InterviewResultRepo resultRepo;
    private final ExecuteHcmService hcmService;
    private final ExecuteConfigService configService;


    public InterviewCheckListService(InterviewCheckListRepo repo, InterviewCheckListTemplateItemRepo itemRepo, InterviewCheckListTemplateRepo templateRepo, InterviewResultRepo resultRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.itemRepo = itemRepo;
        this.templateRepo = templateRepo;
        this.resultRepo = resultRepo;
        this.hcmService = hcmService;
        this.configService = configService;
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

//    public InterviewCheckListDTO create(Long cid, String uid, InterviewCheckListDTO request) throws BusinessException {
//        valid(request);
//        InterviewCheckList exists = repo.findByCompanyIdAndCheckListId(cid, request.getCheckListId(), request.getInterviewerId(), Constants.ENTITY_ACTIVE).orElse(new InterviewCheckList());
//        if(!exists.isNew()){
//            return toDTO(exists);
//        }
//        InterviewCheckList obj = InterviewCheckList.of(cid, uid, request);
//        obj.setStatus(Constants.ENTITY_ACTIVE);
//        obj.setCompanyId(cid);
//        obj.setUpdateBy(uid);
//        obj.setCreateBy(uid);
//
//        return toDTO(repo.save(obj));
//    }


    public List<InterviewCheckListDTO> createOrUpdate(long cid, String uid, Long interviewResultId, List<InterviewCheckListDTO> dtos) {

        List<InterviewCheckList> checkLists = repo.findByCompanyIdAndInterviewResultId(cid, interviewResultId);
        List<Long> checkListIdOfDto = dtos.stream().map(dto -> dto.getCheckListId()).collect(Collectors.toList());
        List<Long> checkListIdExists = checkLists.stream().map(dto -> dto.getCheckListId()).collect(Collectors.toList());

        List<Long> listCheckListIdForCreate = new ArrayList<>(checkListIdOfDto);

        listCheckListIdForCreate.removeAll(checkListIdExists);

        List<InterviewCheckList> listOfProfileCheckList = new ArrayList<>();

        for (Long checkListId : listCheckListIdForCreate) {
            InterviewCheckList profileCheckList = new InterviewCheckList();

            InterviewCheckListDTO dto = dtos.stream().filter(el -> el.getCheckListId() == checkListId).collect(Collectors.toList()).get(0);

            if (dto != null) {
                profileCheckList.setCompanyId(cid);
                profileCheckList.setCheckListId(checkListId);
                profileCheckList.setInterviewerId(dto.getInterviewerId());
                profileCheckList.setInterviewDate(dto.getInterviewDate());

                listOfProfileCheckList.add(profileCheckList);
            }
        }

        List<Long> listCheckListForUpdate = new ArrayList<>(checkListIdOfDto);

        listCheckListForUpdate.retainAll(checkListIdExists); // lay danh sach checkListId da ton tai

        for (Long checkListId : listCheckListForUpdate) {
            InterviewCheckList interviewCheckList = checkLists.stream().filter(el -> el.getCheckListId() == checkListId).collect(Collectors.toList()).get(0);

            if (interviewCheckList != null) {
                interviewCheckList.setInterviewDate(new Date());

                listOfProfileCheckList.add(interviewCheckList);
            }
        }


        // Luu vao Database
        if (listOfProfileCheckList != null && !listOfProfileCheckList.isEmpty()) {
            listOfProfileCheckList = repo.saveAll(listOfProfileCheckList);
        }

        return toDTOs(cid, uid, listOfProfileCheckList);

    }
    public List<InterviewCheckListDTO> toDTOs(Long cid, String uid, List<InterviewCheckList> objs){
        List<InterviewCheckListDTO> dtos = new ArrayList<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> employeeIds = new HashSet<>();
        objs.forEach(o -> {
            if(o.getCheckListId() != null){
                categoryIds.add(o.getCheckListId());
            }
            if(o.getInterviewerId() != null){
                employeeIds.add(o.getInterviewerId());
            }

            dtos.add(toDTO(o));
        });
        List<EmployeeDTO> employeeDTOS = hcmService.getEmployees(uid, cid, employeeIds);
        Map<Long, Map<String, Object>> mapCategory = configService.getCategoryByIds(uid, cid, categoryIds);

        for(InterviewCheckListDTO dto: dtos){
            if(dto.getCheckListId() != null){
                dto.setCheckListObj(mapCategory.get(dto.getCheckListId()));
            }
            if(dto.getInterviewerId() != null){
                dto.setInterviewerObj(employeeDTOS.stream().filter(e -> {
                    return CompareUtil.compare(e.getId(), dto.getInterviewerId());
                }).findAny().orElse(null) );
            }

        }

        return dtos;
    }

    public InterviewCheckListDTO toDTO(InterviewCheckList obj){
        return MapperUtils.map(obj, InterviewCheckListDTO.class);
    }

}
