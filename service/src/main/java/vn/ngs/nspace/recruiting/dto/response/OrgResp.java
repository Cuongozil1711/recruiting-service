package vn.ngs.nspace.recruiting.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrgResp {
    private Long id;
    private String code;
    private String type;
    private String name;
    private Long parentId;
    private Long leaderId;
    private String description;
    private String path;
    private String pathName;
    private Integer status;
}

