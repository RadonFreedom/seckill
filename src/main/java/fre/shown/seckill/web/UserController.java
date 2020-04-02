package fre.shown.seckill.web;

import fre.shown.seckill.common.domain.Result;
import fre.shown.seckill.core.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shaman
 * @date 2020/4/2 16:28
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public Result<Boolean> register(String username, String password) {
        return userService.registerUser(username, password);
    }
}
