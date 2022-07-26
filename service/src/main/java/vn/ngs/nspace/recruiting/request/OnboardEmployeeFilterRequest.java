package vn.ngs.nspace.recruiting.request;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class OnboardEmployeeFilterRequest {
    private String search = "";
    private Long orgId = -1L;
    private Long titleId = -1L;
    private Long buddy = -1L;
    private Long jobApplicationId = -1L;
    private Long positionId = -1L;
    private Long employeeId = -1L;
    private Long levelId = -1L;
    List<String> states = new ArrayList<>();
    String startDateFrom = "1999-01-01T00:00:00+0700";
    String startDateTo = "3000-01-01T00:00:00+0700";
}
