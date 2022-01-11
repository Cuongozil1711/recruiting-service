package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProfileCheckListTemplateItemDTO {
    private Long id;
    private Long checklistId; //dm dung chung
    private Long templateId; // ngay nhan
    private String description; // ngay nhan
    private Boolean required = false;
    private Integer status;

    private Map<String, Object> checkListObj;
}
