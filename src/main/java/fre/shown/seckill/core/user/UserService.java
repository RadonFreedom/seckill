package fre.shown.seckill.core.user;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.core.security.domain.RoleEnum;
import fre.shown.seckill.module.user.domain.UserDO;
import fre.shown.seckill.module.user.manager.UserManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Shaman
 * @date 2020/4/2 16:27
 */

@Service
public class UserService {

    @Autowired
    private UserManager userManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Result<Boolean> registerUser(String username, String password) {

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)
                || userManager.existsByUsername(username)) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        UserDO userDO = new UserDO();
        userDO.setUsername(username);
        userDO.setPassword(passwordEncoder.encode(password));
        userDO.setRoleId(RoleEnum.USER.getId());

        Result<UserDO> saveResult = userManager.save(userDO);
        return Result.isSuccess(saveResult) ? Result.success(true) : Result.error(saveResult);
    }
}
