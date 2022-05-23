package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardWithStateDTO {
    private OnboardOrderCheckListDTO checkListDTO;
    private Integer pending = 0;
    private Integer processing = 0;
    private Integer complete = 0;
    private Integer cancel = 0;
}
