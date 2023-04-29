alter table category
    add parent_id bigint null;

alter table category
    modify parent_id bigint null after id;

alter table category
    modify code varchar (255) not null;

create unique index category_code_uindex
    on category (code);