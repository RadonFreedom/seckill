show variables like 'character%';
# set character_set_database = utf8mb4;
# set character_set_server = utf8mb4;
show variables like '%time_zone%';

create database if not exists seckill;
use seckill;

drop table if exists user;

create table user
(
    id          bigint unsigned  NOT NULL AUTO_INCREMENT,
    username    varchar(200)     NOT NULL unique,
    password    varchar(200)     NOT NULL,
    roleId      integer unsigned not null,
    gmtCreate   datetime COMMENT '创建时间',
    gmtModified datetime COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE = InnoDB;







