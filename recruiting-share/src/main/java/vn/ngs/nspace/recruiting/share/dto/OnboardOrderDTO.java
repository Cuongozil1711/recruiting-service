package vn.ngs.nspace.recruiting.share.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OnboardOrderDTO {
    private Long id;
    private Long jobApplicationId; // id cua ho so xin viec
    private Long onboardId; // id cua thu tuc onboard
    private Long responsibleId; // id nhan vien chiu trach nhiem
    private Date deadline; // hạn hoàn thành
    private Date endDate; // ngày đóng việc
    private String state; // trạng thái

    private JobApplicationDTO jobApplicationDTO;
    private OnboardOrderDTO onboardOrderDTO;

}
