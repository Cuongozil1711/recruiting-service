package vn.ngs.nspace.recruiting.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardEmployeeFilterRequest {
    private String name = "#";
    private String code = "#";
    private Long gender = -1L;
    private Long orgRecruitingId = -1L;
    private String state = "#";
}
