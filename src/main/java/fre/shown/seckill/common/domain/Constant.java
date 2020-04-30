package fre.shown.seckill.common.domain;

import java.util.concurrent.TimeUnit;

/**
 * @author Shaman
 * @date 2020/4/29 15:19
 */

public class Constant {
    public static final long TIMEOUT_30 = 30;
    public static final long TIMEOUT_300 = 300;
    public static final TimeUnit SEC = TimeUnit.SECONDS;
    public static final TimeUnit MILLIS = TimeUnit.MILLISECONDS;

    public static final String SECKILL_ORDER_QUEUE = "seckill_order";

    public static final String SECKILL_PATH_PREFIX = "seckill_path";
    public static final String NO_STOCK_PREFIX = "no_stock";
    public static final String SECKILL_RESULT_PREFIX = "seckill_result";
}
