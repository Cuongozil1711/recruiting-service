package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import vn.ngs.nspace.recruiting.share.dto.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RecruitmentRequestFilterRequest {

    private Long orgId ; // search by org children
    private List<Long> positionIds ;
    private String search = StringUtils.EMPTY;
    private Integer getAll ;
    private List<String> createByUIds ;
    private List<Integer> statuses;
    private String type;
    private Date fromDate = DateUtils.minDate();
    private Date toDate = DateUtils.maxDate();
    public RecruitmentRequestFilterRequest() throws Exception{
    }
}
