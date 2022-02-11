package vn.ngs.nspace.recruiting.service;

import org.springframework.stereotype.Service;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.model.OnboardContract;
import vn.ngs.nspace.recruiting.model.OnboardOrder;
import vn.ngs.nspace.recruiting.repo.OnboardContractRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderCheckListRepo;
import vn.ngs.nspace.recruiting.repo.OnboardOrderRepo;
import vn.ngs.nspace.recruiting.share.dto.OnboardContractDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;

import javax.transaction.Transactional;

@Service
@Transactional
public class OnboardContractService {
    private final OnboardContractRepo repo;
    private final OnboardOrderRepo orderRepo;
    private final ExecuteHcmService _hcmService;

    public OnboardContractService(OnboardContractRepo repo, OnboardOrderCheckListRepo checkListRepo, ExecuteHcmService hcmService, ExecuteConfigService configService, OnboardOrderRepo orderRepo) {
        this.repo = repo;
        _hcmService = hcmService;
        this.orderRepo = orderRepo;
    }

    /* logic validate data before insert model */
    public void valid(OnboardContractDTO dto) throws BusinessException {
        if(dto.getContractId() == null){
            throw new BusinessException("invalid-contract-id");
        }
        if(dto.getOnboardOrderId() == null){
            throw new BusinessException("invalid-contract-order-id");
        }
    }

    /* create object */
    public OnboardContractDTO create(Long cid, String uid, OnboardContractDTO request) throws BusinessException {
        valid(request);
        orderRepo.findByCompanyIdAndId(cid, request.getOnboardOrderId()).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, request.getOnboardOrderId()));
        _hcmService.getContract(uid, cid, request.getContractId());

        OnboardContract order = OnboardContract.of(cid, uid, request);
        order.setStatus(Constants.ENTITY_ACTIVE);
        order.setCreateBy(uid);
        order.setUpdateBy(uid);
        order.setCompanyId(cid);
        order = repo.save(order);
        return toDTO(order);
    }

    /* update by id object */
    public OnboardContractDTO update(Long cid, String uid, Long id, OnboardContractDTO request) throws BusinessException {
        valid(request);
        repo.findByCompanyIdAndOnboardOrderId(cid, request.getOnboardOrderId()).orElseThrow(() -> new EntityNotFoundException(OnboardOrder.class, request.getOnboardOrderId()));
        _hcmService.getContract(uid, cid, request.getContractId());

        OnboardContract curr = repo.findByCompanyIdAndId(cid, id).orElseThrow(() -> new EntityNotFoundException(OnboardContract.class, id));
        MapperUtils.copyWithoutAudit(request, curr);
        curr.setUpdateBy(uid);
        curr = repo.save(curr);
        return toDTO(curr);
    }


    /* convert model object to DTO before response */
    public OnboardContractDTO toDTO(OnboardContract obj){
        OnboardContractDTO dto = MapperUtils.map(obj, OnboardContractDTO.class);

        return dto;
    }
}
