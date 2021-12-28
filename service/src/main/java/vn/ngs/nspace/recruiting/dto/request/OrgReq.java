package vn.ngs.nspace.recruiting.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrgReq {
    private String code;
    private String type;
    private String name;
    private Long parentId;
    private Long leaderId;
    private String description;
    private Integer status;
}

