package fre.shown.seckill.core.order.listener;

import fre.shown.seckill.core.order.OrderService;
import fre.shown.seckill.core.order.domain.SeckillOrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fre.shown.seckill.common.domain.Constant.SECKILL_ORDER_QUEUE;

/**
 * @author Shaman
 * @date 2020/4/30 14:25
 */

@Component
public class SeckillOrderListener {

    @Autowired
    private OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderListener.class);


    @RabbitListener(queues = SECKILL_ORDER_QUEUE)
    public void onReceived(SeckillOrderDTO seckillOrderDTO) {
        try {
            orderService.createSeckillOrder(seckillOrderDTO);
        } catch (Throwable t) {
            logger.error("消息处理异常！", t);
        }
    }
}
