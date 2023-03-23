create table category (
                          id bigint not null auto_increment,
                          created_at datetime(6) not null,
                          deleted bit not null,
                          updated_at datetime(6) not null,
                          code varchar(255),
                          name varchar(255),
                          primary key (id)
) engine=InnoDB;

create table coupon (
                        id bigint not null auto_increment,
                        created_at datetime(6) not null,
                        deleted bit not null,
                        updated_at datetime(6) not null,
                        amount bigint,
                        expired_at date,
                        quantity bigint,
                        type varchar(255),
                        user_id bigint,
                        primary key (id)
) engine=InnoDB;

create table delivery (
                          id bigint not null auto_increment,
                          address varchar(255),
                          zip_code varchar(255),
                          primary key (id)
) engine=InnoDB;

create table order_product (
                               id bigint not null auto_increment,
                               created_at datetime(6) not null,
                               deleted bit not null,
                               updated_at datetime(6) not null,
                               order_quantity bigint not null,
                               status varchar(255) not null,
                               order_id bigint,
                               product_id bigint,
                               user_coupon_id bigint,
                               primary key (id)
) engine=InnoDB;

create table orders (
                        id bigint not null auto_increment,
                        created_at datetime(6) not null,
                        deleted bit not null,
                        updated_at datetime(6) not null,
                        user_id bigint not null,
                        delivery_id bigint not null,
                        primary key (id)
) engine=InnoDB;

create table product (
                         id bigint not null auto_increment,
                         created_at datetime(6) not null,
                         deleted bit not null,
                         updated_at datetime(6) not null,
                         is_display bit not null,
                         name varchar(255),
                         price bigint,
                         quantity bigint,
                         category_id bigint,
                         owner_id bigint,
                         product_thumbnail_id bigint,
                         primary key (id)
) engine=InnoDB;

create table product_image (
                               id bigint not null auto_increment,
                               extension varchar(255),
                               file_name varchar(255),
                               origin_name varchar(255),
                               path varchar(255),
                               size bigint,
                               arrangement bigint,
                               product_id bigint,
                               primary key (id)
) engine=InnoDB;

create table product_thunmnail_image (
                                         id bigint not null auto_increment,
                                         extension varchar(255),
                                         file_name varchar(255),
                                         origin_name varchar(255),
                                         path varchar(255),
                                         size bigint,
                                         primary key (id)
) engine=InnoDB;

create table review (
                        id bigint not null auto_increment,
                        created_at datetime(6) not null,
                        deleted bit not null,
                        updated_at datetime(6) not null,
                        content longtext,
                        score integer not null,
                        user_id bigint,
                        order_product_id bigint,
                        primary key (id)
) engine=InnoDB;

create table review_image (
                              id bigint not null auto_increment,
                              extension varchar(255),
                              file_name varchar(255),
                              origin_name varchar(255),
                              path varchar(255),
                              size bigint,
                              review_id bigint,
                              primary key (id)
) engine=InnoDB;

create table user_coupon (
                             id bigint not null auto_increment,
                             is_usable bit not null,
                             issued_at date,
                             coupon_id bigint,
                             user_id bigint,
                             primary key (id)
) engine=InnoDB;

create table user_product (
                              id bigint not null auto_increment,
                              created_at date,
                              product_id bigint,
                              wisher_id bigint,
                              primary key (id)
) engine=InnoDB;

create table users (
                       id bigint not null auto_increment,
                       created_at datetime(6) not null,
                       deleted bit not null,
                       updated_at datetime(6) not null,
                       email varchar(255),
                       nick_name varchar(20),
                       password varchar(255) not null,
                       role varchar(255),
                       username varchar(24),
                       primary key (id)
) engine=InnoDB;