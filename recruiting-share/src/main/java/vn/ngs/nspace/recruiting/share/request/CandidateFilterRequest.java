package vn.ngs.nspace.recruiting.share.request;

import lombok.Getter;
import lombok.Setter;
import vn.ngs.nspace.lib.utils.DateUtil;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CandidateFilterRequest {
    private String search = "#";
    private List<String> states = List.of("#");
    private List<Long> educationLevel = List.of(-1L);
    private List<Long> language = List.of(-1L);
    private Date applyDateFrom ;
    private Date applyDateTo ;
    private Integer graduationFrom = 0;
    private Integer isBlacklist = -1;
    private Integer graduationTo = 99999;
    private Long gender = -1L;
    private Long applyPosition = -1L;
    private Long resource = -1L;
    private String experience = "#";
    private Integer ageLess = 1000;

}
