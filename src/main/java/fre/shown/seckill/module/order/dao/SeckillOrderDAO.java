package fre.shown.seckill.module.order.dao;

import fre.shown.seckill.module.base.BaseDAO;
import fre.shown.seckill.module.order.domain.SeckillOrderDO;

/**
 * @author Shaman
 * @date 2020/4/28 15:38
 */

public interface SeckillOrderDAO extends BaseDAO<SeckillOrderDO> {
    Boolean existsByUserIdAndSeckillGoodId(Long userId, Long seckillGoodId);
    Boolean existsByIdAndUserId(Long id, Long userId);
}
