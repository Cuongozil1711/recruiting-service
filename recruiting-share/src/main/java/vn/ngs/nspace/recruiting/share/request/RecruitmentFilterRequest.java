package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import vn.ngs.nspace.recruiting.share.dto.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class RecruitmentFilterRequest {

    private String createdBy = StringUtils.EMPTY;
    private String search = StringUtils.EMPTY;
    private Date startFrom = DateUtils.minDate();
    private Date startTo = DateUtils.maxDate();
    private Date endTo = DateUtils.maxDate();
    private Date endFrom = DateUtils.minDate();
    private List<String> state = List.of(StringUtils.EMPTY);

    public RecruitmentFilterRequest() throws Exception {
    }
}
