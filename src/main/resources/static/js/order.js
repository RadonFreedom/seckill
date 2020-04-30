function getOrderDetail() {
    var seckillGoodId = g_getQueryString("orderId");

    $.ajax({
        url: "/order/" + seckillGoodId,
        type: "GET",
        success: function (result) {
            if (result.success === true) {
                render(result.value);
            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}

function render(data) {
    $("#goodName").text(data.goodName);
    $("#goodImg").attr("src", data.goodImg);
    $("#goodPrice").text(data.goodPrice);
    $("#orderPrice").text(data.seckillPrice);
    $("#createDate").text(new Date(data.gmtCreate).format("yyyy-MM-dd hh:mm:ss"));
    var status = "";
    if (data.status === 0) {
        status = "未支付"
    } else if (data.status === 1) {
        status = "待发货";
    }
    $("#orderStatus").text(status);
}