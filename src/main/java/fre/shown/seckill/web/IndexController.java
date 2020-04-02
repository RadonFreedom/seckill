package fre.shown.seckill.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Shaman
 * @date 2020/4/2 18:50
 */

@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {
        return "goods.html";
    }
}
