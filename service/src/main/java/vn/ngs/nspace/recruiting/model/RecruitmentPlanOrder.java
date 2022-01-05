package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;
import vn.ngs.nspace.lib.utils.MapperUtils;
import vn.ngs.nspace.recruiting.share.dto.RecruitmentPlanOrderDTO;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Kế hoạch tuyển dụng
public class RecruitmentPlanOrder extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    @Size(max = 15)
    private String code;//
    private String type; //in-plan , out-plan
    private String solutionSuggestType; //in-company, out
    private Long orgId;//don vi
    private Long titleId;//
    private Long positionId;//vi tri
    private Long levelId;//cap bac
    private Long pic; // employeeId
    private Long supporterId; // employeeId
    private Long quantity;//so luong
    private String businessAddition;//chuyen mon/phan he
    private Date startDate;
    private Date deadline;
    private Date escalateDate;//ngay gia han
    private Long reasonId;//ly do
    private String description;//mo ta
    private String state;//trang thai


        public static RecruitmentPlanOrder of(Long cid, String uid, RecruitmentPlanOrderDTO dto) throws Exception{
        RecruitmentPlanOrder recruitmentPlanOrder = RecruitmentPlanOrder.builder().build();
            MapperUtils.map(dto, recruitmentPlanOrder);
            recruitmentPlanOrder.setCompanyId(cid);
            recruitmentPlanOrder.setUpdateBy(uid);
            recruitmentPlanOrder.setCreateBy(uid);
            return recruitmentPlanOrder;
        }
}
