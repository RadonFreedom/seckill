package fre.shown.seckill.module.order.domain;

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
 * @date 2020/4/28 15:21
 */

@Data
@Entity
@Table(name = "seckillOrder")
public class SeckillOrderDO {
    @Id
    @GeneratedValue
    private Long id;
    private Long userId;
    private Long seckillGoodId;
    private Long deliveryInfoId;
    private String goodName;
    private Integer goodCount;
    private Double goodPrice;
    private Double seckillPrice;
    private Integer orderChannel;
    private Integer status;
    private Timestamp payDate;

    @CreationTimestamp
    private Timestamp gmtCreate;
    @UpdateTimestamp
    private Timestamp gmtModified;
}
