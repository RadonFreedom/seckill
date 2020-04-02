function getOrderDetail() {
    var token = getOauthTokenFromStorage();
    var seckillGoodId = g_getQueryString("orderId");

    $.ajax({
        url: "/order/" + seckillGoodId,
        type: "GET",
        headers: {'Authorization': 'Bearer ' + token},
        success: function (result) {
            if (result.success == true) {
                render(result.data);
            } else {
                layer.msg(result.msg);
            }
        },
        error: function (xhr) {
            checkError(xhr);
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
    if (data.status == 0) {
        status = "未支付"
    } else if (data.status == 1) {
        status = "待发货";
    }
    $("#orderStatus").text(status);
}