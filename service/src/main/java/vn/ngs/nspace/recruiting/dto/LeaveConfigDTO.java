package vn.ngs.nspace.recruiting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LeaveConfigDTO {
    private Long id;
    private String code;
    private String name;
    private String scopeType; //company, org, employee
    private Date startDate;
    private Date endDate;
    private String description;
    private Integer status;
}
