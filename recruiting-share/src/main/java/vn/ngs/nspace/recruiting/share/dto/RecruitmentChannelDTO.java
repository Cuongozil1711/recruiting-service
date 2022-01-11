package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class RecruitmentChannelDTO {
    private Long id;
    private String type;
    private String avatar;
    private String code;
    private String name;
    private String description;
    private Map<String, Object> configs;
}
