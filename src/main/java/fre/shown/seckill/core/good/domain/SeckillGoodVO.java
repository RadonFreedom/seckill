package fre.shown.seckill.core.good.domain;

import lombok.Data;

/**
 * @author Shaman
 * @date 2020/4/28 17:55
 */

@Data
public class SeckillGoodVO {

    /**
     * 秒杀倒计时<br/>
     * remainSeconds > 0: 秒杀未开始<br/>
     * remainSeconds = 0: 秒杀正在进行<br/>
     * remainSeconds < 0: 秒杀已结束<br/>
     */
    private Long remainSeconds;

    private SeckillGoodDTO seckillGood;
}
