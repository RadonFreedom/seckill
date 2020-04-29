package fre.shown.seckill.core.good.domain;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author Shaman
 * @date 2020/4/28 15:12
 */

@Data
public class SeckillGoodVO {

    //** GoodDO的属性 **//
    private String goodName;
    private String goodTitle;
    private String goodImg;
    private String goodDetail;
    private Double goodPrice;


    //** SeckillGoodDO的属性 **//
    /**
     * 秒杀商品的id
     */
    private Long id;
    private Double seckillPrice;
    private Integer stockCount;
    private Timestamp startDate;
    private Timestamp endDate;
}
