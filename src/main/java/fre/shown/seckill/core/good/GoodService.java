package fre.shown.seckill.core.good;

import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.common.util.DataUtils;
import fre.shown.seckill.core.good.domain.SeckillGoodDetailVO;
import fre.shown.seckill.core.good.domain.SeckillGoodVO;
import fre.shown.seckill.module.base.Manager;
import fre.shown.seckill.module.good.dao.SeckillGoodDAO;
import fre.shown.seckill.module.good.domain.SeckillGoodDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Shaman
 * @date 2020/4/28 14:52
 */

@Service
public class GoodService {
    @Autowired
    SeckillGoodDAO seckillGoodDAO;

    public Result<List<SeckillGoodVO>> getSeckillGoodList(Integer page, Integer size) {
        Result<List<SeckillGoodDO>> seckillGoodDOList = Manager.pageQuery(page, size, seckillGoodDAO);
        if (!Result.isSuccess(seckillGoodDOList)) {
            return Result.error(seckillGoodDOList);
        }

        List<SeckillGoodVO> result = new LinkedList<>();
        for (SeckillGoodDO seckillGoodDO : seckillGoodDOList.getValue()) {
            // 先复制goodDO中的属性，再复制seckillGoodDO中的属性，因为最后id需要从seckillGoodDO中copy
            SeckillGoodVO seckillGoodVO =new SeckillGoodVO();
            DataUtils.copyFields(seckillGoodDO.getGoodDO(), seckillGoodVO);
            DataUtils.copyFields(seckillGoodDO, seckillGoodVO);
            result.add(seckillGoodVO);
        }

        return Result.success(result);
    }

    public Result<SeckillGoodDetailVO> getSeckillGoodDetail(Long id) {
        Result<SeckillGoodDO> seckillGoodDOResult = Manager.findById(id, seckillGoodDAO);
        if (!Result.isSuccess(seckillGoodDOResult)) {
            return Result.error(seckillGoodDOResult);
        }
        SeckillGoodDO seckillGoodDO = seckillGoodDOResult.getValue();
        SeckillGoodDetailVO result =new SeckillGoodDetailVO();
        DataUtils.copyFields(seckillGoodDO.getGoodDO(), result);
        DataUtils.copyFields(seckillGoodDO, result);

        long seckillStartAt = result.getStartDate().getTime();
        long seckillEndAt = result.getEndDate().getTime();
        long now = System.currentTimeMillis();
        long remainSeconds;

        if (now < seckillStartAt) {
            //秒杀还没开始，倒计时
            remainSeconds = seckillStartAt - now;
        } else if (now > seckillEndAt) {
            //秒杀已经结束
            remainSeconds = -1L;
        } else {
            //秒杀进行中
            remainSeconds = 0L;
        }
        result.setRemainSeconds(remainSeconds);
        return Result.success(result);
    }
}
