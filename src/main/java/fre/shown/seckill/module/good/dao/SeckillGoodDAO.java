package fre.shown.seckill.module.good.dao;

import fre.shown.seckill.module.base.BaseDAO;
import fre.shown.seckill.module.good.domain.SeckillGoodDO;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author Shaman
 * @date 2020/4/28 15:41
 */

public interface SeckillGoodDAO extends BaseDAO<SeckillGoodDO> {

    @Modifying
    @Query(value = "update seckillGood set stockCount = stockCount - ?2 where id = ?1 and stockCount >= ?2", nativeQuery = true)
    Integer reduceStock(Long seckillGoodId, Integer goodCnt);
}
