package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasonDTO {
    private Long id;
    private String code;
    private String type;
    private String title;
    private String description;
    private Long parentId;
    private Integer status;
}
