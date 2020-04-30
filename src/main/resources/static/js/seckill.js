function getSeckillPath() {
    const seckillGoodId = $("#seckillGoodId").val();
    $.ajax({
        url: "/seckill/path",
        type: "GET",
        data: {
            seckillGoodId: seckillGoodId
        },
        success: function (result) {
            if (result.success === true) {
                const path = result.value;
                doSeckill(path);
            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}

function doSeckill(path) {

    const seckillGoodId = $("#seckillGoodId").val();
    $.ajax({
        url: "/seckill/" + path,
        type: "POST",
        data: {
            seckillGoodId: seckillGoodId,
            goodCnt: 1,
            deliveryInfoId: 1,
            orderChannel: 1
        },
        success: function (result) {
            if (result.success === true) {
                getSeckillResult(path);
            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}

function getSeckillResult(path) {

    $.ajax({
        url: "/seckill/result",
        type: "POST",
        data: {
            path: path
        },
        success: function (result) {
            if (result.success === true) {
                if (result.value === -1) {
                    layer.msg("正在等待下单结果，请稍等...");
                    setTimeout(function () {
                        getSeckillResult(path);
                    }, 200);
                } else {
                    window.location.href = "/order_detail.html?orderId=" + result.value;
                }

            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}