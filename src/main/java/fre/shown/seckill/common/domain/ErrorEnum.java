package fre.shown.seckill.common.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Shaman
 * @date 2019/12/8 14:28
 */

@Getter
@AllArgsConstructor
public enum ErrorEnum {
    SUCCESS(-1, "请求成功！"),
    RESULT_EMPTY(0, "请求结果为空！"),
    PARAM_ERROR(1, "参数有误！"),
    RUNTIME_ERROR(2, "服务器异常！"),
    DATA_CONFLICT(3, "数据冲突！"),
    NO_STOCK(1001, "库存不足！"),
    ORDER_EXISTS(1002, "活动已参与！"),
    ;

    private int code;
    private String msg;
}
