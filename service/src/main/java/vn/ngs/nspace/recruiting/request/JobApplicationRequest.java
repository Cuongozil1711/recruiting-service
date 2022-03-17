package vn.ngs.nspace.recruiting.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import vn.ngs.nspace.recruiting.model.JobApplication;
import vn.ngs.nspace.task.core.dto.request.TaskRequest;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
public class JobApplicationRequest extends TaskRequest<JobApplication> {
    Set<Long> approveEmpId;
}
