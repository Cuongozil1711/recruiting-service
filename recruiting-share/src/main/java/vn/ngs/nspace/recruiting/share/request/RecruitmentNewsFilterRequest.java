package vn.ngs.nspace.recruiting.share.request;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import vn.ngs.nspace.recruiting.share.dto.utils.DateUtils;

import java.util.*;

@Getter
@Setter
public class RecruitmentNewsFilterRequest {
    private Long positionId;
    private Long titleId;
    private Long levelId;
    private String search = StringUtils.EMPTY;
    private Integer getAll;
    private List<String> states;
    private Long fromQuantity;
    private Long toQuantity;
    private Date fromDate = DateUtils.minDate();
    private Date toDate = DateUtils.maxDate();
    private Long newsId;
    public RecruitmentNewsFilterRequest() throws Exception {
    }
}
