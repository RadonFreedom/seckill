package fre.shown.seckill.core.good;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.common.util.DataUtils;
import fre.shown.seckill.core.good.domain.SeckillGoodDTO;
import fre.shown.seckill.core.good.domain.SeckillGoodVO;
import fre.shown.seckill.core.redis.RedisService;
import fre.shown.seckill.module.base.Manager;
import fre.shown.seckill.module.good.dao.SeckillGoodDAO;
import fre.shown.seckill.module.good.domain.SeckillGoodDO;
import fre.shown.seckill.web.domain.PageDetail;
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
    @Autowired
    RedisService redisService;

    public Result<PageDetail<SeckillGoodDTO>> getSeckillGoodList(Integer page, Integer size) {
        if (page == null || size == null || page < 0 || size <= 0) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }

        Result<List<SeckillGoodDO>> seckillGoodDOList = Manager.pageQuery(page, size, seckillGoodDAO);
        if (!Result.isSuccess(seckillGoodDOList)) {
            return Result.error(seckillGoodDOList);
        }

        List<SeckillGoodDTO> seckillGoodDTOList = new LinkedList<>();
        for (SeckillGoodDO seckillGoodDO : seckillGoodDOList.getValue()) {
            // 先复制goodDO中的属性，再复制seckillGoodDO中的属性，因为最后id需要从seckillGoodDO中copy
            SeckillGoodDTO seckillGoodDTO = new SeckillGoodDTO();
            DataUtils.copyFields(seckillGoodDO.getGoodDO(), seckillGoodDTO);
            DataUtils.copyFields(seckillGoodDO, seckillGoodDTO);
            seckillGoodDTOList.add(seckillGoodDTO);
        }

        return Result.success(new PageDetail<>(seckillGoodDAO.count(), size, seckillGoodDTOList));
    }

    public Result<SeckillGoodVO> getSeckillGoodDetail(Long id) {

        Result<SeckillGoodDTO> seckillGoodDTOResult = getSeckillGoodDTO(id);
        if (!Result.isSuccess(seckillGoodDTOResult)) {
            return Result.error(seckillGoodDTOResult);
        }
        SeckillGoodDTO seckillGoodDTO = seckillGoodDTOResult.getValue();

        SeckillGoodVO result = new SeckillGoodVO();
        result.setSeckillGood(seckillGoodDTO);
        long seckillStartAt = seckillGoodDTO.getStartDate().getTime();
        long seckillEndAt = seckillGoodDTO.getEndDate().getTime();
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

    public Result<SeckillGoodDTO> getSeckillGoodDTO(Long id) {
        if (id == null || id < 0) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        SeckillGoodDTO result;
        if (redisService.hasKey(id, SeckillGoodDTO.class)) {
            result = redisService.getById(id, SeckillGoodDTO.class);
        } else {
            Result<SeckillGoodDO> seckillGoodDOResult = Manager.findById(id, seckillGoodDAO);
            if (!Result.isSuccess(seckillGoodDOResult)) {
                return Result.error(seckillGoodDOResult);
            }
            SeckillGoodDO seckillGoodDO = seckillGoodDOResult.getValue();
            // 先复制goodDO中的属性，再复制seckillGoodDO中的属性，因为最后id需要从seckillGoodDO中copy
            result = new SeckillGoodDTO();
            DataUtils.copyFields(seckillGoodDO.getGoodDO(), result);
            DataUtils.copyFields(seckillGoodDO, result);
            redisService.setById(id, result);
        }

        return Result.success(result);
    }
}
