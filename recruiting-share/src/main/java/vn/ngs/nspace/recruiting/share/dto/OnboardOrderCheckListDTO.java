package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.ngs.nspace.hcm.share.dto.EmployeeDTO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardOrderCheckListDTO {
    private Long id;
    private String name;
    private String code; // ma thu tuc
    private String description;
    private Integer status;
}
