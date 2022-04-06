package vn.ngs.nspace.recruiting.dto;

import lombok.*;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)

public class UploadTemplateDTO {

    private Long id;
    private Long unit;
    private String unitName;
    private String plan;
    private String script;
    private String fileName;
    private String department;
    private Long planType;
    private String planTypeName;
    private String year;
    private String yearName;
    private String monthQuarterType;
    private Long monthQuarter;
    private Long workflowId;
    private String nextStep;
    private Map<String,String> excelField;

    private Long requestOrder;

    private String folderId;
    private String fileStorageId;
    private String workflowVersion;
    private String classify;
    private String fileStructureId;
    private String requestStatus;
    private String type;
    private String typePlan;
    private Long fileConfigId;

    private Integer workflowStatus;
    private Integer status;
    private String createBy;
    private String updateBy;
    private Date createDate;
    private Date modifiedDate;

}
