package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import vn.ngs.nspace.recruiting.share.dto.utils.DateUtils;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PlantRequestFilter {
    private Long positionId;
    private Long levelId;
    private Long orgId;
    private Long roomId;
    private String type = StringUtils.EMPTY; // phương án tuyển dụng
    private String typeRequest = StringUtils.EMPTY; // loại yêu cầu
    private List<String> state = List.of(StringUtils.EMPTY);
    private Long picId;
    private Date deadlineFrom = DateUtils.minDate();
    private Date deadlineTo = DateUtils.maxDate();
    private String reason = StringUtils.EMPTY;
    private String search = StringUtils.EMPTY;
    private Integer size;
    private Integer page;

    public PlantRequestFilter() throws Exception {
    }
}
