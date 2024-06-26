create table t_user
(
    ID INT UNSIGNED null,
    NAME VARCHAR(20) not null,
    EMAIL VARCHAR(30) not null,
    CREATED_AT DATE null,
    UPDATED_AT DATE null,
    constraint T_USER_pk
        primary key (ID)
);

