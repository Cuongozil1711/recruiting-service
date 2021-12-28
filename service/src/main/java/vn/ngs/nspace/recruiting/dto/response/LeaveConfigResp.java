package vn.ngs.nspace.recruiting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.recruiting.dto.LeaveConfigDTO;
import vn.ngs.nspace.recruiting.dto.LeavePolicyDTO;
import vn.ngs.nspace.recruiting.dto.LeaveScopeDTO;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LeaveConfigResp {
    private LeaveConfigDTO config;
    private Set<LeaveScopeDTO> scopes;
    private Map<String, LeavePolicyDTO> policies;
}
