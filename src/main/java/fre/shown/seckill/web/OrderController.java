package fre.shown.seckill.web;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.config.ModeProperties;
import fre.shown.seckill.core.order.OrderService;
import fre.shown.seckill.core.order.TestModeOrderService;
import fre.shown.seckill.core.order.domain.SeckillOrderDTO;
import fre.shown.seckill.module.order.domain.SeckillOrderDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shaman
 * @date 2020/4/29 14:48
 */
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private TestModeOrderService testModeOrderService;
    @Autowired
    private ModeProperties modeProperties;

    @GetMapping("/seckill/path")
    public Result<String> getSeckillPath(Long seckillGoodId) {
        return orderService.getSeckillPath(seckillGoodId);
    }

    @PostMapping("/seckill/{path}")
    public Result<Boolean> seckill(@PathVariable("path") String path, SeckillOrderDTO seckillOrderDTO) {
        return ModeProperties.ModeEnum.PROD.equals(modeProperties.getMode()) ?
                orderService.trySeckill(path, seckillOrderDTO) :
                testModeOrderService.trySeckill(path, seckillOrderDTO);
    }

    @PostMapping("/seckill/result")
    public Result<Long> seckillResult(String path) {
        return orderService.getSeckillResult(path);
    }

    @GetMapping("/order/{id}")
    public Result<SeckillOrderDO> getSeckillOrder(@PathVariable("id") Long id) {
        return orderService.getSeckillOrder(id);
    }

    @GetMapping("/seckill/cmp")
    public Result<Long> seckill(SeckillOrderDTO seckillOrderDTO) {
        if (!ModeProperties.ModeEnum.TEST.equals(modeProperties.getMode())) {
            return Result.error(ErrorEnum.PERMISSION_DENIED);
        }
        try {
            return testModeOrderService.createSeckillOrder_CMP(seckillOrderDTO);
        } catch (DataIntegrityViolationException e) {
            return Result.error(ErrorEnum.ORDER_EXISTS);
        } catch (Throwable t) {
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
    }
}
