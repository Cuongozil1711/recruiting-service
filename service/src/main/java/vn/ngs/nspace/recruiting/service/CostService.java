package vn.ngs.nspace.recruiting.service;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Months;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.*;
import vn.ngs.nspace.recruiting.repo.CostDetailRepo;
import vn.ngs.nspace.recruiting.repo.CostRepo;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.CostDetailDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateDTO;
import vn.ngs.nspace.recruiting.share.dto.InterviewCheckListTemplateItemDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.data.UserData;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class CostService {
    private final CostRepo repo;
    private final CostDetailRepo detailRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;


    public CostService(CostRepo repo, CostDetailRepo detailRepo, ExecuteHcmService hcmService, ExecuteConfigService configService) {
        this.repo = repo;
        this.detailRepo = detailRepo;
        _hcmService = hcmService;
        _configService = configService;
    }

    public void valid(CostDTO dto){

        if (dto.getOrgId() == null || dto.getOrgId() == 0l){
            throw new BusinessException("invalid-org");
        }
        if (dto.getStartDate() == null){
            throw new BusinessException("invalid-start-date");
        }
        if (dto.getEndDate() == null){
            throw new BusinessException("invalid-end-date");
        }

        if (dto.getTotalAmount() == null || dto.getTotalAmount() <= 0d){
            throw new BusinessException("invalid-total-amount");
        }
    }

    public void validDetail(CostDetailDTO dto){
        if (dto.getPaymentDate() == null){
            throw new BusinessException("invalid-payment-date");
        }
        if (dto.getTotalAmount() == null || dto.getTotalAmount() <= 0d){
            throw new BusinessException("invalid-payment-amount");
        }
    }
    public CostDTO create(long cid, String uid, CostDTO dto) {
        valid(dto);
        // create template
        Cost obj = Cost.of(cid, uid, dto);
        obj.setCompanyId(cid);
        obj.setCreateBy(uid);
        obj.setUpdateBy(uid);
        obj.setStatus(Constants.ENTITY_ACTIVE);

        obj = repo.save(obj);
        //create detail
        if(dto.getCostDetails() != null && !dto.getCostDetails().isEmpty()){
            for(CostDetailDTO detailDTO : dto.getCostDetails()){
                detailDTO.setCostId(obj.getId());
                createItem(cid, uid, detailDTO);
            }
        }

        return toDTOs(cid, uid, Collections.singletonList(obj)).get(0);
    }

    private void createItem(long cid, String uid, CostDetailDTO itemDTO) {
        validDetail(itemDTO);
        // create template
        CostDetail detail = CostDetail.of(cid, uid, itemDTO);
        detail.setCompanyId(cid);
        detail.setCreateBy(uid);
        detail.setUpdateBy(uid);
        detail.setStatus(Constants.ENTITY_ACTIVE);

        detail = detailRepo.save(detail);
    }

    public CostDTO update(long cid, String uid, Long costId, CostDTO dto) {
        valid(dto);
        Cost curr = repo.findByCompanyIdAndId(cid, costId).orElseThrow(() -> new EntityNotFoundException(Cost.class, costId));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);

        if(dto.getCostDetails() != null && !dto.getCostDetails().isEmpty()){
            for(CostDetailDTO itemDTO : dto.getCostDetails()){
                if(CompareUtil.compare(dto.getStatus(), Constants.ENTITY_INACTIVE)){
                    itemDTO.setStatus(Constants.ENTITY_INACTIVE);
                }
                itemDTO.setCostId(dto.getId());
                updateItem(cid, uid, itemDTO.getId(), itemDTO);
            }
        }

        curr = repo.save(curr);
        return toDTOs(cid, uid, Collections.singletonList(curr)).get(0);
    }

    private void updateItem(long cid, String uid, Long detailId, CostDetailDTO detailDTO) {
        validDetail(detailDTO);
        if(detailDTO.getId() != null && detailDTO.getId() != 0l){
            CostDetail curr = detailRepo.findByCompanyIdAndId(cid, detailId).orElseThrow(() -> new EntityNotFoundException(CostDetail.class, detailId));
            MapperUtils.copyWithoutAudit(detailDTO, curr);
            curr.setUpdateBy(uid);
            curr = detailRepo.save(curr);
        }else{
            createItem(cid, uid, detailDTO);
        }
    }

    public Map<String, Object> splitAmountTo12Months(Long cid, String uid, CostDTO cost) throws Exception {
        Date currentYear = DateUtil.SHORT_DATE_FORMAT.parse( cost.getYear() + "/01/01");
        Date startMonth = DateUtil.startOfCycle(cost.getStartDate());
        Date endMonth = DateUtil.startOfCycle(cost.getEndDate());
        long monthBetweens = ChronoUnit.MONTHS.between(
                new Timestamp(startMonth.getTime()).toLocalDateTime().withDayOfMonth(1),
                new Timestamp(endMonth.getTime()).toLocalDateTime().withDayOfMonth(1));
        Double amountBetween = cost.getTotalAmount() / monthBetweens;
        Map<String, Object> returnData = new ConcurrentHashMap<>();
        for(int i = 0; i <=11; i++){
            Map<String, Object> splitData = new HashMap<>();
            Date checkDate = DateUtil.addDate(currentYear, "month", i);
            splitData.put("month", checkDate);
            splitData.put("request_amount", 0d);
            splitData.put("usage_amount", 0d);
            splitData.put("remain_amount", 0d);
            if(checkDate.compareTo(startMonth) >= 0 && checkDate.compareTo(DateUtil.endOfCycle(cost.getEndDate())) <= 0){
                splitData.put("request_amount", amountBetween);
                Double usageAmount = 0d;
                if(cost.getCostDetails() != null && !cost.getCostDetails().isEmpty()){
                    List<CostDetailDTO> detailInMonths = cost.getCostDetails().stream().filter(d -> {
                        return (d.getPaymentDate().compareTo(checkDate) >= 0
                                && d.getPaymentDate().compareTo(DateUtil.endOfCycle(checkDate)) <= 0);
                    }).collect(Collectors.toList());
                    for(CostDetailDTO dto : detailInMonths){
                        usageAmount += dto.getTotalAmount();
                    }
                }
                splitData.put("usage_amount", usageAmount);
                splitData.put("remain_amount", (amountBetween - usageAmount));
            }
            returnData.put("T." + i, splitData);
        }
        return returnData;
    }


    public List<CostDTO> toDTOs(long cid, String uid, List<Cost> objs) {
        List<CostDTO> dtos = new ArrayList<>();
        Set<Long> costIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();

        objs.forEach(o -> {
            if(o.getCostTypeId() != null){
                categoryIds.add(o.getCostTypeId());
            }
            if(o.getOrgId() != null && o.getOrgId() != 0){
                orgIds.add(o.getOrgId());
            }
            if(!StringUtils.isEmpty(o.getCreateBy())){
                userIds.add(o.getCreateBy());
            }

            costIds.add(o.getId());
        });


        List<CostDetail> details = detailRepo.findByCompanyIdAndCostIdInAndStatus(cid, costIds, Constants.ENTITY_ACTIVE);

        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);

        for(Cost obj : objs){
            CostDTO o = toDTO(obj);

            if(o.getCostTypeId() != null && o.getCostTypeId() != 0l){
                o.setCostTypeObj(mapCategory.get(o.getCostTypeId()));
            }
            if(o.getOrgId() != null){
                o.setOrg( mapOrg.get(o.getOrgId()));
            }
            if(!StringUtils.isEmpty(o.getCreateBy())){
                o.setCreateByObj((Map<String, Object>) mapperUser.get(o.getCreateBy()));
            }
            List<CostDetailDTO> detailDTOS = new ArrayList<>();
            details.stream().filter(i -> CompareUtil.compare(i.getCostId(), obj.getId()))
                    .collect(Collectors.toList()).stream().forEach(i -> {
                        detailDTOS.add(MapperUtils.map(i, CostDetailDTO.class));
                    });

            o.setCostDetails(detailDTOS);
            dtos.add(o);
        }
        return dtos;
    }
    public CostDTO toDTO (Cost obj){
        return MapperUtils.map(obj, CostDTO.class);
    }
}
