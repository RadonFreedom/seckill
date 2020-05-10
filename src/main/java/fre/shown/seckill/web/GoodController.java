package fre.shown.seckill.web;

import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.core.good.GoodService;
import fre.shown.seckill.core.good.domain.SeckillGoodVO;
import fre.shown.seckill.core.good.domain.SeckillGoodDTO;
import fre.shown.seckill.web.domain.PageDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Shaman
 * @date 2020/4/28 14:51
 */

@RestController
public class GoodController {
    @Autowired
    private GoodService goodService;

    @GetMapping("/seckillGood")
    public Result<PageDetail<SeckillGoodDTO>> getSeckillGoodList(Integer page, Integer size) {
        return goodService.getSeckillGoodList(page, size);
    }

    @GetMapping("/seckillGood/{id}")
    public Result<SeckillGoodVO> getSeckillGoodDetail(@PathVariable Long id) {
        return goodService.getSeckillGoodDetail(id);
    }
}
