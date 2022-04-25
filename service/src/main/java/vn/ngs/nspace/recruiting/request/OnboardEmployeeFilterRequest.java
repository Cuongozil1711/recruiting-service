package vn.ngs.nspace.recruiting.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnboardEmployeeFilterRequest {

    private String name;
    private String code;
    private Long gender;
    private Long orgRecruitingId;
    private String state;
    private Integer status;
}
