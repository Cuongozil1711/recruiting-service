package vn.ngs.nspace.recruiting.service.v2;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.CostDetail;
import vn.ngs.nspace.recruiting.model.RecruitmentNews;
import vn.ngs.nspace.recruiting.repo.CostDetailRepo;
import vn.ngs.nspace.recruiting.repo.CostRepo;
import vn.ngs.nspace.recruiting.repo.RecruitmentNewsRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.recruiting.share.request.RecruitmentNewsFilterRequest;
import vn.ngs.nspace.task.core.data.UserData;

import javax.transaction.Transactional;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CostV2Service {
    private final CostRepo repo;
    private final CostDetailRepo detailRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;
    private final RecruitmentNewsRepo recruitmentNewsRepo;

    public void valid(CostDTO dto) {
        if (StringUtils.isEmpty(dto.getName())) {
            throw new BusinessException("cost-name-data-empty");
        }
        if (dto.getName().length() > 255) {
            throw new BusinessException("cost-name-data-length-255");
        }
        if (dto.getCost() == null || dto.getCost() <= 0) {
            throw new BusinessException("cost-actual-cost-data-empty");
        }
        if (dto.getCost() >= 1e16) {
            throw new BusinessException("cost-actual-cost-data-length");
        }
        if (dto.getExpectedCost() == null || dto.getExpectedCost() <= 0) {
            throw new BusinessException("cost-expect-cost-data-empty");
        }
        if (dto.getExpectedCost() >= 1e16) {
            throw new BusinessException("cost-expect-cost-data-length");
        }
        if (StringUtils.isEmpty(dto.getUnit())) {
            throw new BusinessException("data-empty");
        }

    }

    @Transactional
    public CostDTO create(long cid, String uid, CostDTO dto) {
        valid(dto);
        // create template
        Cost cost = Cost.of(cid, uid, dto);
        cost.setCompanyId(cid);
        cost.setCreateBy(uid);
        cost.setUpdateBy(uid);
        cost.setStatus(Constants.ENTITY_ACTIVE);

        if (dto.getNewsId() != null) {
            recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, dto.getNewsId())
                    .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, dto.getNewsId()));
        }
        cost = repo.save(cost);

        return toDTO(cost);
    }

    public List<CostDTO> creates(long cid, String uid, List<CostDTO> dtos) {
        List<CostDTO> costDTOS = new ArrayList<>();
        dtos.forEach(
                e-> {
                    costDTOS.add(create(cid,uid,e));
                }
        );

        return costDTOS;
    }

    @Transactional
    public CostDTO update(long cid, String uid, CostDTO dto) {
        valid(dto);
        Cost curr = repo.findByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, dto.getId()).orElseThrow(() -> new EntityNotFoundException(Cost.class, dto.getId()));
        if (dto.getNewsId() != null) {
            recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, dto.getNewsId())
                    .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, dto.getNewsId()));
        }
        MapperUtils.copyWithoutAudit(dto, curr);

        curr.setUpdateBy(uid);

        curr = repo.save(curr);

        return toDTO(curr);
    }

    public List<CostDTO> updates(long cid, String uid,List<CostDTO> dtos) {
        List<CostDTO> costDTOS = new ArrayList<>();

        List<Long> ids = dtos.stream().map(CostDTO::getId).collect(Collectors.toList());
        repo.deleteAllByCIdAndIdIn(cid, ids);

        dtos.forEach(
                e-> {
                    if (e.getId() == null) {
                        costDTOS.add(create(cid,uid,e));
                    }
                    else {
                        costDTOS.add(update(cid,uid,e));
                    }
                }
        );

        return costDTOS;
    }


    public CostDTO delete(Long cis, String uid, Long costId) {
        Cost curr = repo.findById( costId).orElseThrow(() -> new EntityNotFoundException(Cost.class, costId));

        curr.setUpdateBy(uid);
        curr.setStatus(Constants.ENTITY_INACTIVE);

        curr = repo.save(curr);

        return toDTO(curr);
    }

//    public Map<String, Object> splitAmountTo12Months(Long cid, String uid, CostDTO cost) throws Exception {
//        Date currentYear = DateUtil.SHORT_DATE_FORMAT.parse(cost.getYear() + "/01/01");
//        Date startMonth = DateUtil.startOfCycle(cost.getStartDate());
//        Date endMonth = DateUtil.startOfCycle(cost.getEndDate());
//        long monthsBetween = ChronoUnit.MONTHS.between(
//                new Timestamp(startMonth.getTime()).toLocalDateTime().withDayOfMonth(1),
//                new Timestamp(endMonth.getTime()).toLocalDateTime().withDayOfMonth(1));
//        Double amountBetween = cost.getTotalAmount() / monthsBetween;
//        Map<String, Object> returnData = new ConcurrentHashMap<>();
//        for (int i = 0; i <= 11; i++) {
//            Map<String, Object> splitData = new HashMap<>();
//            Date checkDate = DateUtil.addDate(currentYear, "month", i);
//            splitData.put("month", checkDate);
//            splitData.put("request_amount", 0D);
//            splitData.put("usage_amount", 0D);
//            splitData.put("remain_amount", 0D);
//            if (checkDate.compareTo(startMonth) >= 0 && checkDate.compareTo(DateUtil.endOfCycle(cost.getEndDate())) <= 0) {
//                splitData.put("request_amount", amountBetween);
//                Double usageAmount = 0d;
//                if (cost.getCostDetails() != null && !cost.getCostDetails().isEmpty()) {
//                    List<CostDetailDTO> detailInMonths = cost.getCostDetails()
//                            .stream()
//                            .filter(
//                                    d -> (d.getPaymentDate().compareTo(checkDate) >= 0
//                                            && d.getPaymentDate().compareTo(DateUtil.endOfCycle(checkDate)) <= 0
//                                    )
//                            )
//                            .collect(Collectors.toList());
//
//                    for (CostDetailDTO dto : detailInMonths) {
//                        usageAmount += dto.getTotalAmount();
//                    }
//                }
//                splitData.put("usage_amount", usageAmount);
//                splitData.put("remain_amount", (amountBetween - usageAmount));
//            }
//            returnData.put("T." + i, splitData);
//        }
//
//        return returnData;
//    }


    public List<CostDTO> toDTOs(long cid, String uid, List<Cost> costs) {
        return costs.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public CostDTO toDTO(Cost obj) {
        return MapperUtils.map(obj, CostDTO.class);
    }

    @Transactional
    public List<Cost> createList(long cid, String uid, List<CostDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return new ArrayList<>();
        }
        dtos.forEach(this::valid);
        List<Long> newIds = dtos.stream().map(CostDTO::getNewsId).distinct().collect(Collectors.toList());
        if (newIds.size() > 1) {
            throw new BusinessException("has 2 newsId");
        }
        if (newIds.size() > 0 && newIds.get(0) != null) {
            recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.STATE_ACTIVE, newIds.get(0))
                    .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, newIds.get(0)));
        }
        List<Cost> costs = dtos.stream().map(dto -> {
            Cost cost = Cost.of(cid, uid, dto);
            cost.setCompanyId(cid);
            cost.setCreateBy(uid);
            cost.setUpdateBy(uid);
            cost.setStatus(Constants.ENTITY_ACTIVE);
            return cost;
        }).collect(Collectors.toList());


        repo.saveAll(costs);

        return costs;
    }

    @Transactional
    public List<Cost> updateList(long cid, String uid, List<CostDTO> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return new ArrayList<>();
        }
        dtos.forEach(this::valid);
        List<Long> newIds = dtos.stream().map(CostDTO::getNewsId).distinct().collect(Collectors.toList());
        if (newIds.size() > 1) {
            throw new BusinessException("has 2 newsId");
        }
        List<Cost> costs = new ArrayList<>();
        if (newIds.size() > 0 && newIds.get(0) != null) {
            Long newId = newIds.get(0);
            recruitmentNewsRepo.findAllByCompanyIdAndStatusAndId(cid, Constants.ENTITY_ACTIVE, newId)
                    .orElseThrow(() -> new EntityNotFoundException(RecruitmentNews.class, newIds.get(0)));
            List<Cost> costOlds = repo.findAllByCompanyIdAndStatusAndNewsId(cid, Constants.ENTITY_ACTIVE, newId);
            Map<Long, Cost> costOldMap = costOlds.stream().collect(Collectors.toMap(Cost::getId, Function.identity()));

            for (CostDTO dto : dtos) {
                if (dto.getId() == null || !costOldMap.containsKey(dto.getId())) {
                    dto.setId(null);
                    Cost cost = Cost.of(cid, uid, dto);
                    cost.setId(null);
                    cost.setCompanyId(cid);
                    cost.setCreateBy(uid);
                    cost.setUpdateBy(uid);
                    cost.setStatus(Constants.ENTITY_ACTIVE);
                    costs.add(cost);
                } else {
                    Cost cost = costOldMap.get(dto.getId());
                    cost.setUnit(dto.getUnit());
                    cost.setName(dto.getName());
                    cost.setExpectedCost(dto.getExpectedCost());
                    cost.setCost(dto.getCost());
                    cost.setNewsId(dto.getNewsId());
                    cost.setUpdateBy(uid);
                }
            }
            Set<Long> IdsUpdate = dtos.stream().map(CostDTO::getId).collect(Collectors.toSet());
            costOlds.forEach(cost -> {
                if (!IdsUpdate.contains(cost.getId())) {
                    cost.setStatus(Constants.STATE_INACTIVE);
                    cost.setUpdateBy(uid);
                }
            });
            costs.addAll(costOlds);
        } else {

            costs = dtos.stream().map(dto -> {
                Cost cost = Cost.of(cid, uid, dto);
                cost.setCompanyId(cid);
                cost.setCreateBy(uid);
                cost.setUpdateBy(uid);
                cost.setStatus(Constants.ENTITY_ACTIVE);
                return cost;
            }).collect(Collectors.toList());
        }

        repo.saveAll(costs);

        return costs;
    }

    public Object filter(long cid, String uid, RecruitmentNewsFilterRequest request, Pageable page) {
        if (Constants.GET_ALL.equals(request.getGetAll())) {
            page = PageRequest.of(0, Integer.MAX_VALUE, page.getSort());
        }

        Long newId = request.getNewsId();
        ;
        String search;

        if (StringUtils.isEmpty(request.getSearch())) {
            search = "%%";
        } else {
            search = "%" + request.getSearch() + "%";
        }
        return repo.filterByNewId(cid, newId, search, page);

    }

    @Transactional
    public List<Cost> deleteListCost(long cid, String uid, List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) throw new BusinessException("ids-null");
        List<Cost> costs = repo.findAllByCompanyIdAndStatusAndIdIn(cid, Constants.ENTITY_ACTIVE, ids);
        if (CollectionUtils.isEmpty(costs)) {
            throw new EntityNotFoundException(Cost.class);
        }
        costs.forEach(cost -> cost.setStatus(Constants.ENTITY_INACTIVE));
        repo.saveAll(costs);
        return costs;
    }
}
