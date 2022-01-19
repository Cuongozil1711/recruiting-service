package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.Reason;
import vn.ngs.nspace.recruiting.repo.ReasonRepo;
import vn.ngs.nspace.recruiting.share.dto.ReasonDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class ReasonService {
    private final ReasonRepo repo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public ReasonService(ReasonRepo repo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;

        _hcmService = hcmService;
        _configService = configService;
    }

    /* logic validate data before insert model */
    public void valid(ReasonDTO dto) throws BusinessException {
        if(StringUtils.isEmpty(dto.getCode())){
            throw new BusinessException("code-can-not-be-empty");
        }
        if(StringUtils.isEmpty(dto.getType())){
            throw new BusinessException("type-can-not-be-empty");
        }
    }

    /* create object */
    public ReasonDTO create(Long cid, String uid, ReasonDTO request) throws BusinessException {
        valid(request);
        Reason exists = repo.findByCompanyIdAndTypeAndCodeAndStatus(cid, request.getType(), request.getCode(), Constants.ENTITY_ACTIVE).orElse(new Reason());
        if(!exists.isNew()){
            throw new BusinessException("duplicate-data-with-this-type-and-code");
        }

        Reason r = Reason.of(cid, uid, request);
        r.setStatus(Constants.ENTITY_ACTIVE);
        r.setCreateBy(uid);
        r.setUpdateBy(uid);
        r.setCompanyId(cid);
        r = repo.save(r);

        return toDTOWithObj(cid, uid, r);
    }

    /* update by id object */
    public ReasonDTO update(Long cid, String uid, Long id, ReasonDTO request) throws BusinessException {
        valid(request);
        Reason curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(Reason.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);

        try{
            repo.findByCompanyIdAndTypeAndCodeAndStatus(cid, request.getType(), request.getCode(), Constants.ENTITY_ACTIVE).orElse(new Reason());
        }catch (IncorrectResultSizeDataAccessException ex){
            throw new BusinessException("duplicate-data-with-this-type-and-code");
        }

        return toDTOWithObj(cid, uid, curr);
    }

    /* convert list model object to DTO before response */
    public List<ReasonDTO> toDTOs(Long cid, String uid, List<Reason> objs){
        List<ReasonDTO> dtos = new ArrayList<>();
        objs.forEach(obj -> {
            dtos.add(toDTO(obj));
        });
        return dtos;
    }

    /* convert model object to DTO with data before response */
    public ReasonDTO toDTOWithObj(Long cid, String uid, Reason obj){
        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    /* convert model object to DTO before response */
    public ReasonDTO toDTO(Reason obj){
        ReasonDTO dto = MapperUtils.map(obj, ReasonDTO.class);
        return dto;
    }
}
