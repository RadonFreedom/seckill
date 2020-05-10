package fre.shown.seckill.web.domain;

import lombok.Data;

import java.util.List;

/**
 * @author Shaman
 * @date 2020/5/10 10:40
 */

@Data
public class PageDetail<T> {

    Long totalPage;
    List<T> list;

    public PageDetail(Long cnt, Integer size, List<T> list) {
        this.totalPage = cnt / size;
        if (cnt % size > 0) {
            totalPage++;
        }
        this.list = list;
    }
}
