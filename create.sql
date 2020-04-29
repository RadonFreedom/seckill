show variables like 'character%';
# set character_set_database = utf8mb4;
# set character_set_server = utf8mb4;
show variables like '%time_zone%';

create database if not exists seckill;
use seckill;
DROP TABLE IF EXISTS `seckillOrder`;
DROP TABLE IF EXISTS `seckillGood`;
DROP TABLE IF EXISTS `good`;
drop table if exists user;

create table user
(
    id          bigint unsigned  NOT NULL AUTO_INCREMENT,
    username    varchar(200)     NOT NULL unique,
    password    varchar(200)     NOT NULL,
    roleId      integer unsigned not null,
    gmtCreate   datetime default CURRENT_TIMESTAMP COMMENT '创建时间',
    gmtModified datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB;

insert into user(username, password, roleId)
values ('admin', '$2a$10$gE2Grb/zZdKhx87V2st99eTCEFVmejmVe2v52BYBC5koaITCpUxh6', 0);


CREATE TABLE `good`
(
    `id`         bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `goodName`   varchar(16)     not null COMMENT '商品名称',
    `goodTitle`  varchar(64)     not null COMMENT '商品标题',
    `goodImg`    varchar(64)    DEFAULT NULL COMMENT '商品的图片',
    `goodDetail` longtext COMMENT '商品的详情介绍',
    `goodPrice`  decimal(10, 2) DEFAULT '0.00' COMMENT '商品单价',
    `goodStock`  int(11)        DEFAULT '0' COMMENT '商品库存，-1表示没有限制',
    gmtCreate    datetime       default CURRENT_TIMESTAMP COMMENT '创建时间',
    gmtModified  datetime       default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;

INSERT INTO `good` (goodName, goodTitle, goodImg, goodDetail, goodPrice, goodStock)
VALUES ('iphoneX', 'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphonex.png',
        'Apple iPhone X (A1865) 64GB 银色 移动联通电信4G手机', '8765.00', '10000'),
       ('华为Meta9', '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/meta10.png',
        '华为 Mate 9 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '3212.00', '10000'),
       ('iphone8', 'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '/img/iphone8.png',
        'Apple iPhone 8 (A1865) 64GB 银色 移动联通电信4G手机', '5589.00', '10000'),
       ('小米6', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '/img/mi6.png', '小米6 4GB+32GB版 月光银 移动联通电信4G手机 双卡双待', '3212.00',
        '10000');


CREATE TABLE `seckillGood`
(
    `id`           bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '秒杀的商品表',
    `goodId`       bigint unsigned DEFAULT NULL COMMENT '商品Id',
    `seckillPrice` decimal(10, 2)  DEFAULT '0.00' COMMENT '秒杀价',
    `stockCount`   int(11)         DEFAULT NULL COMMENT '秒杀库存',
    `startDate`    datetime        default CURRENT_TIMESTAMP COMMENT '秒杀开始时间',
    `endDate`      datetime        default null COMMENT '秒杀结束时间',
    gmtCreate      datetime        default CURRENT_TIMESTAMP COMMENT '创建时间',
    gmtModified    datetime        default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    constraint `fk_seckillGood_goodId` foreign KEY (goodId) references good (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `seckillGood`
    (goodId, seckillPrice, stockCount, endDate)
VALUES ('1', '0.01', '9', '2020-12-31 21:51:27'),
       ('2', '0.01', '9', '2020-12-31 14:00:24'),
       ('3', '0.01', '9', '2020-12-31 14:00:24'),
       ('4', '0.01', '9', '2020-12-31 14:00:24');


CREATE TABLE `seckillOrder`
(
    `id`             bigint unsigned NOT NULL AUTO_INCREMENT,
    userId           bigint unsigned NOT NULL,
    seckillGoodId    bigint unsigned DEFAULT NULL COMMENT '商品ID',
    `deliveryInfoId` bigint unsigned DEFAULT NULL COMMENT '收获地址ID',
    `goodName`       varchar(16)     DEFAULT NULL COMMENT '冗余过来的商品名称',
    `goodCount`      int(11)         DEFAULT '0' COMMENT '商品数量',
    `goodPrice`      decimal(10, 2)  DEFAULT '0.00' COMMENT '商品单价',
    seckillPrice     decimal(10, 2)  DEFAULT '0.00' COMMENT '秒杀价',
    `orderChannel`   tinyint(4)      DEFAULT '0' COMMENT '1 pc，2 android，3 ios',
    `status`         tinyint(4)      DEFAULT '0' COMMENT '订单状态，0新建未支付，1已支付，2已发货，3已收货，4已退款，5已完成',
    `payDate`        datetime        DEFAULT NULL COMMENT '支付时间',
    gmtCreate        datetime COMMENT '创建时间',
    gmtModified      datetime COMMENT '更新时间',
    PRIMARY KEY pk_id (`id`),
    constraint `uk_seckillOrder_goodId_userId` UNIQUE KEY (seckillGoodId, `userId`),
    constraint `fk_seckillOrder_userId` foreign KEY (userId) references user (id),
    constraint `fk_seckillOrder_seckillGoodId` foreign KEY (seckillGoodId) references seckillGood (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;







