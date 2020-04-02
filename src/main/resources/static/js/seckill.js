function refreshVerifyCode() {
    $("#verifyCodeImg").attr("src", "/seckill/verifyCode?seckillGoodId=" + $("#seckillGoodId").val());
}

function getSeckillPath() {

    const token = getOauthTokenFromStorage();
    const seckillGoodId = $("#seckillGoodId").val();
    const verifyCode = $("#verifyCode").val();
    $.ajax({
        url: "/seckill/path",
        type: "POST",
        headers: {'Authorization': 'Bearer ' + token},
        data: {
            seckillGoodId: seckillGoodId,
            verifyCode: verifyCode
        },
        success: function (result) {
            if (result.success == true) {
                const path = result.data;
                doSeckill(path, token);
            } else {
                layer.msg(result.msg);
                refreshVerifyCode();
            }
        },
        error: function (xhr) {
            checkError(xhr);
        }
    });
}

function doSeckill(path, token) {

    const seckillGoodId = $("#seckillGoodId").val();
    $.ajax({
        url: "/seckill/" + path,
        type: "POST",
        headers: {'Authorization': 'Bearer ' + token},
        data: {
            seckillGoodId: seckillGoodId,
            goodCnt: 1,
            deliveryInfoId: 1,
            orderChannel: 1
        },
        success: function (result) {
            if (result.success == true) {
                getSeckillResult(token, seckillGoodId);
            } else {
                layer.msg(result.msg);
                refreshVerifyCode();
            }
        },
        error: function (xhr) {
            checkError(xhr);
        }
    });
}

function getSeckillResult(token, seckillGoodId) {

    $.ajax({
        url: "/seckill/result",
        type: "POST",
        headers: {'Authorization': 'Bearer ' + token},
        data: {
            seckillGoodId: seckillGoodId
        },
        success: function (result) {
            if (result.success == true) {
                if (result.data != null) {
                    if (result.msg != null) {
                        layer.confirm(result.msg + " 查看订单?", {btn: ["确定", "取消"]},
                            function () {
                                window.location.href = "/order_detail.html?orderId=" + result.data;
                            },
                            function () {
                                layer.closeAll();
                            });
                    } else {
                        window.location.href = "/order_detail.html?orderId=" + result.data;
                    }
                } else {
                    layer.msg("正在等待下单结果，请稍等...");
                    setTimeout(function () {
                        getSeckillResult(token, seckillGoodId);
                    }, 200);
                }

            } else {
                layer.msg(result.msg);
            }
        },
        error: function (xhr) {
            checkError(xhr);
        }
    });
}