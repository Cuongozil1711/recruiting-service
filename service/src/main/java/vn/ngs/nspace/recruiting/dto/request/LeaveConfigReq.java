package vn.ngs.nspace.recruiting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LeaveConfigReq {
    private LeaveConfigDTO config;
    private Set<LeaveScopeDTO> scopes;
    private Map<String, LeavePolicyDTO> policies;
}
