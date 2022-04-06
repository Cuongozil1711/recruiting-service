package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderDTO {
    private String id;
    private String name;
    private Long companyId;
    private Long groupId;
    private String userId;
    private String parent;
    private String path;
    private String application;
    private Integer mode;
    private String publish;
    //    private List<InvolveDTO> involves;
    private List<FolderDTO> children;
    private FolderDTO parentFolder;
}

