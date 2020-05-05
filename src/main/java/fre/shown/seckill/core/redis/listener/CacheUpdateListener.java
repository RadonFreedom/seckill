package fre.shown.seckill.core.redis.listener;

import fre.shown.seckill.core.good.domain.SeckillGoodDTO;
import fre.shown.seckill.core.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fre.shown.seckill.common.domain.Constant.SECKILL_GOOD_QUEUE;

/**
 * @author Shaman
 * @date 2020/5/5 17:10
 */


@Component
public class CacheUpdateListener {

    private static final Logger logger = LoggerFactory.getLogger(CacheUpdateListener.class);

    @Autowired
    private RedisService redisService;

    @RabbitListener(queues = SECKILL_GOOD_QUEUE)
    public void onSeckillGoodUpdated(SeckillGoodDTO seckillGoodDTO) {
        try {
            redisService.setById(seckillGoodDTO.getId(), seckillGoodDTO);
        } catch (Throwable t) {
            logger.error("消息处理异常！", t);
        }
    }
}
