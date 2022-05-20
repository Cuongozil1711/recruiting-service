package vn.ngs.nspace.recruiting.share.dto;


import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ScheduleTypeDTO {
    private Long id;
    @NotNull(message = "has-not-template-valid-with-this-candidate")
    private String code;
    @NotNull(message = "invalid-profile-template")
    private String name;
    private String description;

}
