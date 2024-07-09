create table if not EXISTS t_user
(
    ID BIGINT UNSIGNED AUTO_INCREMENT,
    NAME VARCHAR(20) not null,
    EMAIL VARCHAR(30) not null,
    CREATED_AT DATE null,
    UPDATED_AT DATE null,
    constraint T_USER_pk
        primary key (ID)
);

drop table t_user