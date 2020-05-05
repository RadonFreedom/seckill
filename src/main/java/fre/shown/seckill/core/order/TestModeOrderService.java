package fre.shown.seckill.core.order;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.common.util.DataUtils;
import fre.shown.seckill.common.util.UserUtils;
import fre.shown.seckill.core.good.GoodService;
import fre.shown.seckill.core.good.domain.SeckillGoodDTO;
import fre.shown.seckill.core.order.domain.SeckillOrderDTO;
import fre.shown.seckill.core.redis.RedisService;
import fre.shown.seckill.module.good.dao.SeckillGoodDAO;
import fre.shown.seckill.module.order.dao.SeckillOrderDAO;
import fre.shown.seckill.module.order.domain.SeckillOrderDO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static fre.shown.seckill.common.domain.Constant.*;

/**
 * @author Shaman
 * @date 2020/5/2 14:39
 */

@Service
public class TestModeOrderService {

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

    public Result<Boolean> trySeckill(String path, SeckillOrderDTO seckillOrderDTO) {
        if (seckillOrderDTO == null || seckillOrderDTO.getSeckillGoodId() == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }

        if (hasStock(seckillOrderDTO.getSeckillGoodId())) {
            seckillOrderDTO.setSeckillPath(path);
            seckillOrderDTO.setUserId(UserUtils.getUserId());
            amqpTemplate.convertAndSend(NEW_SECKILL_ORDER_QUEUE, seckillOrderDTO);
            return Result.success(true);
        } else {
            return Result.error(ErrorEnum.NO_STOCK);
        }
    }

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

    private boolean hasStock(Long seckillGoodId) {
        return !redisService.hasKey(NO_STOCK_PREFIX + seckillGoodId);
    }

    private void setSeckillResult(String seckillPath, Result<Long> result) {
        redisService.set(SECKILL_RESULT_PREFIX + seckillPath, result, TIMEOUT_30, SEC);
    }

    @Transactional(rollbackFor = Throwable.class)
    public Result<Long> createSeckillOrder_CMP(SeckillOrderDTO seckillOrderDTO) {

        Long seckillGoodId = seckillOrderDTO.getSeckillGoodId();

        //查询商品信息
        Result<SeckillGoodDTO> seckillGoodDTOResult = goodService.getSeckillGoodDTO(seckillGoodId);
        if (!Result.isSuccess(seckillGoodDTOResult)) {
            return Result.error(seckillGoodDTOResult);
        }
        SeckillGoodDTO seckillGoodDTO = seckillGoodDTOResult.getValue();
        //减库存
        if (seckillGoodDAO.reduceStock(seckillGoodId, seckillOrderDTO.getGoodCnt()) == 0) {
            return Result.error(ErrorEnum.NO_STOCK);
        }
        //生成订单
        SeckillOrderDO seckillOrderDO = DataUtils.copyFields(seckillOrderDTO, new SeckillOrderDO());
        DataUtils.copyFields(seckillGoodDTO, seckillOrderDO);
        seckillOrderDO.setId(null);
        seckillOrderDO.setUserId(UserUtils.getUserId());
        seckillOrderDO.setStatus(0);
        seckillOrderDO = seckillOrderDAO.save(seckillOrderDO);
        return Result.success(seckillOrderDO.getId());
    }
}
