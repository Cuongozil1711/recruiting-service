package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationOnboardDTO {
    private JobApplicationDTO jobApplicationDTO;
    private List<OnboardOrderDTO> onboardOrderDTOS;
}
