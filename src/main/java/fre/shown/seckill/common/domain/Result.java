package fre.shown.seckill.common.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Shaman
 * @date 2019/12/8 14:11
 */

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Result<R> {

    /**
     * 接口执行成功失败标识
     */
    private boolean success;

    /**
     * 结果数据
     */
    @Getter
    private R value;

    /**
     * 错误或者提示信息
     */
    @Getter
    private String msg;

    /**
     * 响应代码
     */
    @Getter
    private int code;

    private Result(boolean success, R value, String msg, int code) {
        this.success = success;
        this.value = value;
        this.msg = msg;
        this.code = code;
    }

    public static <E> Result<E> success(E val) {
        return val == null ? error(ErrorEnum.RESULT_EMPTY)
                : new Result<>(true, val, "", ErrorEnum.SUCCESS.getCode());
    }

    public static <E> Result<E> error(ErrorEnum errorEnum) {
        return error(errorEnum.getMsg(), errorEnum.getCode());
    }

    public static <E, T> Result<E> error(Result<T> result) {
        return error(result.msg, result.code);
    }

    public static <E> boolean isSuccess(Result<E> result) {
        return result != null && result.success && result.value != null;
    }

    private static <E> Result<E> error(String msg, int code) {
        return new Result<>(false, null, msg, code);
    }
}
