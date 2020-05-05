package fre.shown.seckill.core.order.listener;

import fre.shown.seckill.config.ModeProperties;
import fre.shown.seckill.core.order.OrderService;
import fre.shown.seckill.core.order.TestModeOrderService;
import fre.shown.seckill.core.order.domain.SeckillOrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fre.shown.seckill.common.domain.Constant.NEW_SECKILL_ORDER_QUEUE;

/**
 * @author Shaman
 * @date 2020/4/30 14:25
 */

@Component
public class SeckillOrderListener {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderListener.class);
    @Autowired
    private OrderService orderService;
    @Autowired
    private TestModeOrderService testModeOrderService;
    @Autowired
    private ModeProperties modeProperties;

    @RabbitListener(queues = NEW_SECKILL_ORDER_QUEUE)
    public void onReceived(SeckillOrderDTO seckillOrderDTO) {
        try {
            if (ModeProperties.ModeEnum.PROD.equals(modeProperties.getMode())) {
                orderService.createSeckillOrder(seckillOrderDTO);
            } else {
                testModeOrderService.createSeckillOrder(seckillOrderDTO);
            }
        } catch (Throwable t) {
            logger.error("消息处理异常！", t);
        }
    }
}
