package vn.ngs.nspace.recruiting.service.v2;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import vn.ngs.nspace.hcm.share.dto.response.OrgResp;
import vn.ngs.nspace.lib.exceptions.BusinessException;
import vn.ngs.nspace.lib.exceptions.EntityNotFoundException;
import vn.ngs.nspace.lib.utils.CompareUtil;
import vn.ngs.nspace.lib.utils.DateUtil;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.lib.utils.StaticContextAccessor;
import vn.ngs.nspace.recruiting.model.Cost;
import vn.ngs.nspace.recruiting.model.CostDetail;
import vn.ngs.nspace.recruiting.repo.CostDetailRepo;
import vn.ngs.nspace.recruiting.repo.CostRepo;
import vn.ngs.nspace.recruiting.service.ExecuteConfigService;
import vn.ngs.nspace.recruiting.service.ExecuteHcmService;
import vn.ngs.nspace.recruiting.share.dto.CostDTO;
import vn.ngs.nspace.recruiting.share.dto.CostDetailDTO;
import vn.ngs.nspace.recruiting.share.dto.utils.Constants;
import vn.ngs.nspace.task.core.data.UserData;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class CostV2Service {
    private final CostRepo repo;
    private final CostDetailRepo detailRepo;
    private final ExecuteHcmService _hcmService;
    private final ExecuteConfigService _configService;

    public CostV2Service(CostRepo repo, CostDetailRepo detailRepo, ExecuteHcmService _hcmService, ExecuteConfigService _configService) {
        this.repo = repo;
        this.detailRepo = detailRepo;
        this._hcmService = _hcmService;
        this._configService = _configService;
    }

    public void valid(CostDTO dto) {

    }

    public CostDTO create(long cid, String uid, CostDTO dto) {
        valid(dto);
        // create template
        Cost cost = Cost.of(cid, uid, dto);
        cost.setCompanyId(cid);
        cost.setCreateBy(uid);
        cost.setUpdateBy(uid);
        cost.setStatus(Constants.ENTITY_ACTIVE);

        cost = repo.save(cost);

        return toDTO(cost);
    }

    public CostDTO update(long cid, String uid, Long costId, CostDTO dto) {
        valid(dto);
        Cost curr = repo.findById( costId).orElseThrow(() -> new EntityNotFoundException(Cost.class, costId));
        MapperUtils.copyWithoutAudit(dto, curr);
        curr.setUpdateBy(uid);

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


    public List<CostDTO> toDTOs(long cid, String uid, List<Cost> objs) {
        List<CostDTO> dtos = new ArrayList<>();
        Set<Long> costIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        Set<Long> orgIds = new HashSet<>();
        Set<String> userIds = new HashSet<>();

        List<CostDetail> details = detailRepo.findByCompanyIdAndCostIdInAndStatus(cid, costIds, Constants.ENTITY_ACTIVE);

        Map<Long, OrgResp> mapOrg = _hcmService.getMapOrgs(uid, cid, orgIds);
        Map<Long, Map<String, Object>> mapCategory = _configService.getCategoryByIds(uid, cid, categoryIds);
        Map<String, Object> mapperUser = StaticContextAccessor.getBean(UserData.class).getUsers(userIds);

        return dtos;
    }

    public CostDTO toDTO(Cost obj) {
        return MapperUtils.map(obj, CostDTO.class);
    }
}
