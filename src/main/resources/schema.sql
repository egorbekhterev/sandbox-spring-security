create table t_user (
    id int primary key,
    c_username varchar(255) NOT NULL unique
);

create table t_user_password (
    id serial primary key,
    id_user int not null unique references t_user(id),
    c_password varchar(255)
);

create table t_user_authority (
    id serial primary key,
    id_user int not null references t_user(id),
    c_authority varchar(255)
);