package fre.shown.seckill.module.base;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author Shaman
 * @date 2019/12/13 15:27
 */

@NoRepositoryBean
public interface BaseDAO<T> extends PagingAndSortingRepository<T, Long> {

    void deleteAllByIdIn(List<Long> ids);
}
