package vn.ngs.nspace.recruiting.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenericGenerator;
import vn.ngs.nspace.lib.models.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecruitmentNewsChannel extends PersistableEntity<Long> {

    @Id
    @GenericGenerator(name = "id",strategy = "vn.ngs.nspace.lib.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private  Long id;

    private Long newsId; //tin tuyển dụng
    private Long channelId; // kênh tuyển dụng
    private Integer state; // trạng thái kênh tuyển dụng với tin

}
