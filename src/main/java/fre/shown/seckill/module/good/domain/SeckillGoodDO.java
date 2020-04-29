package fre.shown.seckill.module.good.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Shaman
 * @date 2020/4/28 15:10
 */

@Data
@Entity
@Table(name = "seckillGood")
public class SeckillGoodDO {
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "goodId")
    private GoodDO goodDO;
    private Double seckillPrice;
    private Integer stockCount;

    private Timestamp startDate;
    private Timestamp endDate;

    @CreationTimestamp
    private Timestamp gmtCreate;
    @UpdateTimestamp
    private Timestamp gmtModified;
}
