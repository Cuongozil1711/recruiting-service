package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LeavePolicyDTO {
    private Long id;
    private Integer maxPerYear;
    private Integer maxPerMonth;
    private Integer maxPerForm;
    private Boolean notAllowBackForm;
    private Integer backFormBefore;
    private Map<String, Object> policy;
}
