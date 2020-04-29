package fre.shown.seckill.module.base;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * @author Shaman
 * @date 2019/12/8 15:45
 */

public class Manager {

    public static final Sort SORT = Sort.by(Sort.Order.desc("id"));
    private static final Logger logger = LoggerFactory.getLogger(Manager.class);

    public static <E, D extends BaseDAO<E>> Result<List<E>> pageQuery(Integer page, Integer size, D dao) {
        if (page == null || size == null || page < 0 || size <= 0 || dao == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        try {
            return Result.success(dao.findAll(PageRequest.of(page, size, SORT)).getContent());
        } catch (Exception e) {
            logger.error(ErrorEnum.RUNTIME_ERROR.getMsg(), e);
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
    }

    public static <E, D extends BaseDAO<E>> Result<E> findById(Long id, D dao) {
        if (id == null || id < 0 || dao == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        try {
            Optional<E> entity = dao.findById(id);
            return entity.map(Result::success).orElseGet(() -> Result.error(ErrorEnum.RESULT_EMPTY));
        } catch (Exception e) {
            logger.error(ErrorEnum.RUNTIME_ERROR.getMsg(), e);
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
    }

    public static <E, D extends BaseDAO<E>> Result<Boolean> deleteAllById(List<Long> ids, D dao) {
        if (dao == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        if (ids == null || ids.isEmpty()) {
            return Result.success(true);
        }
        try {
            dao.deleteAllByIdIn(ids);
        } catch (Exception e) {
            logger.error(ErrorEnum.RUNTIME_ERROR.getMsg(), e);
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
        return Result.success(true);
    }

    public static <E, D extends BaseDAO<E>> Result<E> save(E entity, D dao) {
        if (dao == null || entity == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        try {
            return Result.success(dao.save(entity));
        } catch (Exception e) {
            logger.error(ErrorEnum.RUNTIME_ERROR.getMsg(), e);
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
    }
}
