package vn.ngs.nspace.recruiting.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
//Danh sách ứng viên, được nhập hoặc import từ hệ thống
public class Candicate extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String fullName;
    private String avatar;
    private Date birthDate;
    private String gender;
    private String phone;
    private String email;

    private String address;
    private String wardCode;
    private String districtCode;
    private String provinceCode;
    private String countryCode;

    private Long educationLevel; // danh muc dung chung
    private String educateLocation;
    private String industry;

    private String language;
    private String languageLevel;

    private Double experience;
    private String experienceUnit; // months, years

    private String lastCompanyName;
    private String lastPosition;
    private Date lastCompanyDateFrom;
    private Date lastCompanyDateTo;

    private Long applyPositionId;
    private Date applyDate;
    private Long cvSourceId;

    private String tags;
}
