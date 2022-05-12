package vn.ngs.nspace.recruiting.share.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ScheduleTypeDTO {
    private Long id;
    private String code;
    private String name;
    private String description;

}
