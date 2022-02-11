package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewCheckListTemplateItemDTO {
    private Long id;
    private Long templateId;
    private Long checkListId;
    private String optionType; // number , select
    private Double minRating; //enable when optionType = number
    private Double maxRating; //enable when optionType = number
    private String optionValues; //enable when optionType = select
    private Integer status;
    private String description;
    private String priority;

    private Map<String, Object> checkListObj;
}
