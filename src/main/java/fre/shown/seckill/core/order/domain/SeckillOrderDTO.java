package fre.shown.seckill.core.order.domain;

import lombok.Data;

/**
 * @author Shaman
 * @date 2020/4/29 19:09
 */

@Data
public class SeckillOrderDTO {

    private String seckillPath;
    private Long seckillGoodId;
    private Integer goodCnt;
    private Long userId;
    private Integer orderChannel;
    private Long deliveryInfoId;
}
