package vn.ngs.nspace.recruiting.share.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardTrainingTemplateDTO {
    private Long id;
    private String name;
    private Long positionId;
    private Long titleId;
    private Long orgId;
    private Long levelId;
    private float completion;
    private Integer status;

    private Map<String, Object> contractTypeObj;
    private Map<String, Object> positionObj;
    private Map<String, Object> titleObj;
    private Map<String, Object> orgObj;
    private Map<String, Object> levelObj;

    private List<OnboardTrainingTemplateItemDTO> children;
}
