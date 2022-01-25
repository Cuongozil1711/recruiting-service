package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.CandidateTodo;
import vn.ngs.nspace.recruiting.repo.CandidateToDoRepo;
import vn.ngs.nspace.recruiting.share.dto.CandidateToDoDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CandidateToDoService {
    private final CandidateToDoRepo repo;
    private final ExecuteHcmService _hcmService;

    public CandidateToDoService(CandidateToDoRepo repo, ExecuteHcmService hcmService) {
        this.repo = repo;
        _hcmService = hcmService;
    }

    public void valid(CandidateToDoDTO dto){

    }
    public CandidateToDoDTO create(Long cid, String uid, CandidateToDoDTO dto) {
        valid(dto);

        CandidateTodo candidateTodo = CandidateTodo.of(cid, uid, dto);
        candidateTodo.setStatus(Constants.ENTITY_ACTIVE);
        candidateTodo.setCreateBy(uid);
        candidateTodo.setUpdateBy(uid);
        candidateTodo.setCompanyId(cid);
        candidateTodo = repo.save(candidateTodo);

        return toDTOWithObj(cid, uid, candidateTodo);

    }



    public CandidateToDoDTO update(Long cid, String uid, Long id, CandidateToDoDTO dto) {
        valid(dto);
        CandidateTodo curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(CandidateTodo.class, id));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        return toDTOWithObj(cid, uid, curr);
    }

    public void delete(Long cid, String uid, List<Long> ids) {
        ids.stream().forEach(i -> {
            CandidateTodo candidateTodo = repo.findByCompanyIdAndId(cid, i).orElse(new CandidateTodo());
            if(!candidateTodo.isNew()){
                candidateTodo.setStatus(Constants.ENTITY_INACTIVE);
                candidateTodo.setUpdateBy(uid);
                candidateTodo.setModifiedDate(new Date());

                repo.save(candidateTodo);
            }
        });
    }
    public CandidateToDoDTO toDTO(CandidateTodo candidateTodo) {

        CandidateToDoDTO dto = MapperUtils.map(candidateTodo, CandidateToDoDTO.class);
        return dto;
    }

    public CandidateToDoDTO toDTOWithObj(Long cid, String uid, CandidateTodo obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    public List<CandidateToDoDTO> toDTOs(Long cid, String uid, List<CandidateTodo> objs) {
        List<CandidateToDoDTO> dtos = new ArrayList<>();
        Set<Long> empIds = new HashSet<>();
        objs.forEach(obj -> {
            if(obj.getResponsibleId() != null){
                empIds.add(obj.getResponsibleId());
            }
            dtos.add(toDTO(obj));
        });
        List<EmployeeDTO> employees = _hcmService.getEmployees(uid,cid,empIds);
        for (CandidateToDoDTO dto : dtos){
            if (dto.getResponsibleId() != null){
                EmployeeDTO emp = employees.stream().filter(e -> CompareUtil.compare(e.getId(),dto.getResponsibleId())).findAny().orElse(new EmployeeDTO());
                dto.setResponsibleIdObj(emp);
            }
        }
        return dtos;
    }
}


