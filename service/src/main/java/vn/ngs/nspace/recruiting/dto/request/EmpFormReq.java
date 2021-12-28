package vn.ngs.nspace.recruiting.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.ngs.nspace.task.core.dto.request.TaskRequest;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmpFormReq extends TaskRequest<EmpForm> {
}
