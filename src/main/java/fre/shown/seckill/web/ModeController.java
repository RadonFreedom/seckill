package fre.shown.seckill.web;

import fre.shown.seckill.config.ModeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shaman
 * @date 2020/5/2 14:27
 */

@RestController
public class ModeController {

    @Autowired
    private ModeProperties modeProperties;

    @GetMapping("/mode")
    public String setMode(Integer id) {
        return modeProperties.setMode(id);
    }
}
