package fre.shown.seckill.module.user.manager;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.module.base.Manager;
import fre.shown.seckill.module.user.dao.UserDAO;
import fre.shown.seckill.module.user.domain.UserDO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Shaman
 * @date 2019/12/12 10:31
 */

@Component
public class UserManager {

    Logger logger = LoggerFactory.getLogger(UserManager.class);

    @Autowired
    UserDAO userDAO;

    public Result<UserDO> findByUsername(String username) {
        if (!existsByUsername(username)) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        try {
            return Result.success(userDAO.findByUsername(username));
        } catch (Exception e) {
            logger.error(ErrorEnum.RUNTIME_ERROR.getMsg(), e);
            return Result.error(ErrorEnum.RUNTIME_ERROR);
        }
    }

    public boolean existsByUsername(String username) {
        return !StringUtils.isBlank(username) && userDAO.existsByUsername(username);
    }

    public Result<UserDO> save(UserDO userDO) {
        return Manager.save(userDO, userDAO);
    }

    public Result<Boolean> deleteUserByIds(List<Long> ids) {
        return Manager.deleteAllById(ids, userDAO);
    }
}
