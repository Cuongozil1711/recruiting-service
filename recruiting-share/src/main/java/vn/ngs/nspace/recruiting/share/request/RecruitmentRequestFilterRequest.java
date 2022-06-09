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

    private Long orgId; // search by org children
    private List<Long> positionIds; // các vị trí
    private String search = StringUtils.EMPTY; // tìm kiếm
    private Integer getAll;
    private List<String> createByUIds; // danh sách id người tạo
    private List<String> statuses; // trạng thái
    private String type; // loại
    private Date fromDate = DateUtils.minDate(); // từ ngày
    private Date toDate = DateUtils.maxDate(); // đến ngày

    public RecruitmentRequestFilterRequest() throws Exception {
    }
}
