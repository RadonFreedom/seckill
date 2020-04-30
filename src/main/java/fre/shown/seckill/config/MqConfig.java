package fre.shown.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static fre.shown.seckill.common.domain.Constant.SECKILL_ORDER_QUEUE;

/**
 * @author Shaman
 * @date 2020/4/29 17:30
 */

@Configuration
public class MqConfig {


    @Bean
    public Queue seckillOrderQueue() {
        return new Queue(SECKILL_ORDER_QUEUE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
