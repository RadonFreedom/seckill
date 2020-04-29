function getGoodList() {

    $.ajax({
        type: "GET",
        url: "/seckillGood?page=0&size=10",
        success: function (result) {
            //局部刷新页面数据
            var userDataHtml = "";
            if (result.success === true) {
                $.each(result.value, function (i, seckillGood) {
                    userDataHtml += '<tr>';
                    userDataHtml += '  <td>' + seckillGood.goodName + '</td>';
                    userDataHtml += '  <td><img src="' + seckillGood.goodImg + '" width="100" height="100" alt="' + seckillGood.goodName + '"/></td>';
                    userDataHtml += '  <td>' + seckillGood.goodPrice + '</td>';
                    userDataHtml += '  <td>' + seckillGood.seckillPrice + '</td>';
                    userDataHtml += '  <td>' + seckillGood.stockCount + '</td>';
                    userDataHtml += '  <td><a href="/good_detail.html?id=' + seckillGood.id + '">详情</a></td>';
                    userDataHtml += '</tr>';
                });

                $("#goods-list").html(userDataHtml);
            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}

function getGoodDetail() {
    var goodId = g_getQueryString("id");
    $.ajax({
        url: "/seckillGood/" + goodId,
        type: "GET",
        success: function (result) {
            if (result.success === true) {
                renderGoodDetail(result.value);
            } else {
                layer.msg("服务器请求有误: " + result.msg + " code: " + result.code);
            }
        },
        error: function () {
            layer.msg("客户端请求有误");
        }
    });
}

function renderGoodDetail(seckillGood) {
    $("#goodName").text(seckillGood.goodName);
    $("#goodImg").attr("src", seckillGood.goodImg);
    $("#startTime").text(new Date(seckillGood.startDate).format("yyyy-MM-dd hh:mm:ss"));
    $("#remainSeconds").val(seckillGood.remainSeconds);
    $("#seckillGoodId").val(seckillGood.id);
    $("#goodPrice").text(seckillGood.goodPrice);
    $("#seckillPrice").text(seckillGood.seckillPrice);
    $("#stockCount").text(seckillGood.stockCount);
    countDown();
}

function countDown() {
    var remainSeconds = $("#remainSeconds").val();
    var timeout;
    if (remainSeconds > 0) {
        //秒杀还没开始，倒计时
        $("#buyButton").attr("disabled", true);
        $("#seckillTip").html("秒杀倒计时：" + remainSeconds + "秒");
        timeout = setTimeout(function () {
            $("#countDown").text(remainSeconds - 1);
            $("#remainSeconds").val(remainSeconds - 1);
            countDown();
        }, 1000);
    } else if (remainSeconds == 0) {
        //秒杀进行中
        $("#buyButton").attr("disabled", false);
        //不再进行下次调用
        if (timeout) {
            clearTimeout(timeout);
        }
        $("#seckillTip").html("秒杀进行中");
    } else {
        //秒杀已经结束
        $("#buyButton").attr("disabled", true);
        $("#seckillTip").html("秒杀已经结束");
    }
}