package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDTO {
    protected Long id;
    private Long accountId;
    private String userId;
    private String code;
    private String type;
    private String name;
    private String path;
    private String pathId;
    private String description;
    private Long parentId;
    private Long leaderId;
    private int status;
    private GroupDTO parent;
    private List<GroupDTO> children;
}
