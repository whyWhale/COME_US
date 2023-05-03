alter table users
    modify password varchar(255) null;

alter table users
    add provider varchar(55) null;

alter table users
    add provider_id varchar(255) null;