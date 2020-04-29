package fre.shown.seckill.module.good.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Shaman
 * @date 2020/4/28 15:04
 */

@Data
@Entity
@Table(name = "good")
public class GoodDO {
    @Id
    @GeneratedValue
    private Long id;
    private String goodName;
    private String goodTitle;
    private String goodImg;
    private String goodDetail;
    private Double goodPrice;
    private Integer goodStock;

    @CreationTimestamp
    private Timestamp gmtCreate;
    @UpdateTimestamp
    private Timestamp gmtModified;
}
