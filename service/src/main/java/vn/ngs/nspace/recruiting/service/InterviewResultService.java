package vn.ngs.nspace.recruiting.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.Constants;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.InterviewCheckList;
import vn.ngs.nspace.recruiting.model.InterviewResult;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.InterviewCheckListRepo;
import vn.ngs.nspace.recruiting.repo.InterviewResultRepo;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewResultDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@Log4j2
public class InterviewResultService {
    private final InterviewResultRepo _repo;
    private final InterviewCheckListRepo checkListRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public InterviewResultService(InterviewResultRepo repo, InterviewCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        _repo = repo;
        this.checkListRepo = checkListRepo;
        _hcmService = hcmService;
        _configService = configService;
    }


    public void valid(InterviewResultDTO dto){
        if (dto.getCandidateId() == null){
            throw new BusinessException("invalid-candidateId");
        }
        if (dto.getInterviewDate() == null){
            throw new BusinessException("invalid-date");
        }
        if (StringUtils.isEmpty(dto.getName())){
            throw new BusinessException("invalid-name");
        }
        if (StringUtils.isEmpty(dto.getState())){
            throw new BusinessException("invalid-state");
        }

    }


    public InterviewResultDTO create(Long cid, String uid, InterviewResultDTO dto) {
            valid(dto);
        InterviewResult interviewResult = InterviewResult.of(cid,uid,dto);
        interviewResult.setCompanyId(cid);
        interviewResult.setCreateBy(uid);
        interviewResult = _repo.save(interviewResult);
        return toDTO(interviewResult);
    }



    private InterviewResultDTO toDTO(InterviewResult interviewResult) {
        InterviewResultDTO dto = MapperUtils.map(interviewResult, InterviewResultDTO.class);
        return dto;
    }



    public InterviewResultDTO update(Long cid, String uid, Long id, InterviewResultDTO dto) {
        valid(dto);
        InterviewResult curr = _repo.findByCompanyIdAndId(cid,id).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, id));
        MapperUtils.copyWithoutAudit(dto,curr);
        curr.setUpdateBy(uid);
        curr = _repo.save(curr);

        return toDTO(curr);

    }

    public List<InterviewCheckListDTO> getInterviewCheckList(Long cid, String uid, Long interviewResultId, InterviewResultDTO dto) {
//        InterviewResult interviewResult = _repo.findByCompanyIdAndId(cid, interviewResultId).orElseThrow(() -> new EntityNotFoundException(InterviewResult.class, interviewResultId));
        List<InterviewCheckList> checkLists = checkListRepo.findByCompanyIdAndInterviewResultId(cid,interviewResultId);
        List<InterviewCheckList> finalList = new ArrayList<>();
        InterviewCheckList exist = checkLists.stream().filter(c -> { return CompareUtil.compare(interviewResultId, c.getInterviewResultId());})
                .findAny().orElse(new InterviewCheckList());
        if (exist.isNew()){
            exist.setCompanyId(cid);
            exist.setCreateBy(uid);
            exist.setStatus(Constants.ENTITY_ACTIVE);
            exist.setInterviewDate(dto.getInterviewDate());
            exist.setResult(dto.getState());
            exist.setInterviewResultId(dto.getId());
            exist = checkListRepo.save(exist);

        }
        finalList.add(exist);
        return checkListDTOS(cid, uid, finalList);
    }
    public List<InterviewResultDTO> toDTOs(Long cid, String uid, List<InterviewResult> objs) {
        List<InterviewResultDTO> dtos = new ArrayList<>();
        objs.forEach(obj -> {
            dtos.add(toDTO(obj));
        });
        return dtos;

    }

    public InterviewCheckListDTO interviewCheckListDTO(InterviewCheckList interviewCheckList){
        InterviewCheckListDTO dto = MapperUtils.map(interviewCheckList, InterviewCheckListDTO.class);
        return dto;
    }
    public List<InterviewCheckListDTO> checkListDTOS(Long cid, String uid, List<InterviewCheckList> objs){
        List<InterviewCheckListDTO> dtos = new ArrayList<>();
        objs.forEach(obj -> {
            dtos.add(interviewCheckListDTO(obj));
        });
        return dtos;
    }


}
