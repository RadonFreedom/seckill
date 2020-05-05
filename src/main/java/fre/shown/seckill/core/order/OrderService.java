package fre.shown.seckill.core.order;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.common.util.DataUtils;
import fre.shown.seckill.common.util.UserUtils;
import fre.shown.seckill.core.good.GoodService;
import fre.shown.seckill.core.good.domain.SeckillGoodDTO;
import fre.shown.seckill.core.order.domain.SeckillOrderDTO;
import fre.shown.seckill.core.redis.RedisService;
import fre.shown.seckill.module.base.Manager;
import fre.shown.seckill.module.good.dao.SeckillGoodDAO;
import fre.shown.seckill.module.order.dao.SeckillOrderDAO;
import fre.shown.seckill.module.order.domain.SeckillOrderDO;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fre.shown.seckill.common.domain.Constant.*;

/**
 * @author Shaman
 * @date 2020/4/28 15:21
 */

@Service
public class OrderService {
    @Autowired
    private SeckillOrderDAO seckillOrderDAO;
    @Autowired
    private SeckillGoodDAO seckillGoodDAO;
    @Autowired
    private RedisService redisService;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private GoodService goodService;

    public Result<String> getSeckillPath(Long seckillGoodId) {
        if (seckillGoodId == null || seckillGoodId < 0) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }

        String path = RandomStringUtils.randomAlphanumeric(20);
        redisService.set(getSeckillPathKey(seckillGoodId), path, TIMEOUT_300, MILLIS);
        return Result.success(path);
    }

    /**
     * 1. 秒杀请求先访问 Redis 判断是否有库存, 如果是, 下一步; 否则返回库存不足. <br/>
     * 2. 秒杀请求进入消息队列, 通知客户端轮询秒杀结果.
     *
     * @param path            秒杀路径
     * @param seckillOrderDTO 秒杀商品id
     * @return 通知前端轮询秒杀结果，或者秒杀失败
     */
    public Result<Boolean> trySeckill(String path, SeckillOrderDTO seckillOrderDTO) {
        if (seckillOrderDTO == null || seckillOrderDTO.getSeckillGoodId() == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }

        String pathKey = getSeckillPathKey(seckillOrderDTO.getSeckillGoodId());
        if (path == null || !redisService.hasKey(pathKey)
                || !path.equals(redisService.get(pathKey, String.class))) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }

        redisService.delete(pathKey);
        if (hasStock(seckillOrderDTO.getSeckillGoodId())) {
            seckillOrderDTO.setSeckillPath(path);
            seckillOrderDTO.setUserId(UserUtils.getUserId());
            amqpTemplate.convertAndSend(NEW_SECKILL_ORDER_QUEUE, seckillOrderDTO);
            return Result.success(true);
        } else {
            return Result.error(ErrorEnum.NO_STOCK);
        }
    }

    /**
     * 1. 访问 Redis 判断是否有库存，若否，在Redis保存秒杀结果并返回 <br/>
     * 2. 判断同一用户同一商品的订单是否存在，若是，在Redis保存秒杀结果并返回 <br/>
     * 3. 尝试减库存，如果失败，在Redis中保存结果并返回 <br/>
     * 4. 尝试生成订单信息，如果失败，保存结果并抛出异常（为了让减库存操作回滚）
     *
     * @param seckillOrderDTO 消息队列 {@link fre.shown.seckill.common.domain.Constant#NEW_SECKILL_ORDER_QUEUE SECKILL_ORDER_QUEUE}
     *                        中的下一个待生成订单
     */
    @Transactional(rollbackFor = Throwable.class)
    public void createSeckillOrder(SeckillOrderDTO seckillOrderDTO) {

        Long seckillGoodId = seckillOrderDTO.getSeckillGoodId();

        // 判断redis库存标记
        String seckillPath = seckillOrderDTO.getSeckillPath();
        if (!hasStock(seckillGoodId)) {
            setSeckillResult(seckillPath, Result.error(ErrorEnum.NO_STOCK));
            return;
        }

        try {
            // 判断订单是否存在
            if (seckillOrderDAO.existsByUserIdAndSeckillGoodId(seckillOrderDTO.getUserId(), seckillGoodId)) {
                setSeckillResult(seckillPath, Result.error(ErrorEnum.ORDER_EXISTS));
                return;
            }
            //查询商品信息
            Result<SeckillGoodDTO> seckillGoodDTOResult = goodService.getSeckillGoodDTO(seckillGoodId);
            if (!Result.isSuccess(seckillGoodDTOResult)) {
                setSeckillResult(seckillPath, Result.error(seckillGoodDTOResult));
                return;
            }
            SeckillGoodDTO seckillGoodDTO = seckillGoodDTOResult.getValue();

            //减库存
            if (seckillGoodDAO.reduceStock(seckillGoodId, seckillOrderDTO.getGoodCnt()) == 0) {
                //将无库存标志存入Redis
                redisService.set(NO_STOCK_PREFIX + seckillGoodId, "", TIMEOUT_30, SEC);
                setSeckillResult(seckillPath, Result.error(ErrorEnum.NO_STOCK));
                return;
            }
            //发送消息，异步同步seckillGoodDTO更改缓存
            seckillGoodDTO.setStockCount(seckillGoodDTO.getStockCount() - seckillOrderDTO.getGoodCnt());
            amqpTemplate.convertAndSend(SECKILL_GOOD_QUEUE, seckillGoodDTO);

            //生成订单
            SeckillOrderDO seckillOrderDO = DataUtils.copyFields(seckillOrderDTO, new SeckillOrderDO());
            DataUtils.copyFields(seckillGoodDTO, seckillOrderDO);
            seckillOrderDO.setId(null);
            seckillOrderDO.setStatus(0);
            seckillOrderDO = seckillOrderDAO.save(seckillOrderDO);
            setSeckillResult(seckillPath, Result.success(seckillOrderDO.getId()));
        } catch (DataIntegrityViolationException e) {
            setSeckillResult(seckillPath, Result.error(ErrorEnum.ORDER_EXISTS));
            throw e;
        } catch (Throwable t) {
            setSeckillResult(seckillPath, Result.error(ErrorEnum.RUNTIME_ERROR));
            throw t;
        }
    }

    private String getSeckillPathKey(Long seckillGoodId) {
        return SECKILL_PATH_PREFIX + UserUtils.getUserId() + "_" + seckillGoodId;
    }

    private boolean hasStock(Long seckillGoodId) {
        return !redisService.hasKey(NO_STOCK_PREFIX + seckillGoodId);
    }

    private void setSeckillResult(String seckillPath, Result<Long> result) {
        redisService.set(SECKILL_RESULT_PREFIX + seckillPath, result, TIMEOUT_30, SEC);
    }

    @SuppressWarnings("unchecked")
    public Result<Long> getSeckillResult(String seckillPath) {
        String key = SECKILL_RESULT_PREFIX + seckillPath;
        return redisService.hasKey(key) ? redisService.get(key, Result.class) : Result.success(-1L);
    }

    public Result<SeckillOrderDO> getSeckillOrder(Long id) {
        return seckillOrderDAO.existsByIdAndUserId(id, UserUtils.getUserId()) ?
                Manager.findById(id, seckillOrderDAO) : Result.error(ErrorEnum.PERMISSION_DENIED);
    }
}
