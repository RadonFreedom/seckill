package fre.shown.seckill.config;

import fre.shown.seckill.common.domain.ErrorEnum;
import fre.shown.seckill.common.domain.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author Shaman
 * @date 2020/5/2 14:21
 */

@Component
public class ModeProperties {
    @Getter
    private ModeEnum mode;

    public ModeProperties() {
        mode = ModeEnum.TEST;
    }

    public Result<Integer> setMode(Integer id) {
        if (id == null) {
            return Result.error(ErrorEnum.PARAM_ERROR);
        }
        for (ModeEnum mode : ModeEnum.values()) {
            if (id.equals(mode.id)) {
                this.mode = mode;
            }
        }
        return Result.success(mode.id);
    }

    @AllArgsConstructor
    public enum ModeEnum {
        PROD(0, "生产模式"),
        TEST(1, "测试模式"),
        ;

        private final int id;
        private final String name;
    }
}
