-- # order page 전용 sql file
insert into users (id, created_at, deleted, updated_at, email, nick_name, password, role, username)
values (1, '2023-02-16 16:35:15.338158', false, '2023-02-16 16:35:15.338158', 'whyWhale@ooo.co.kr', 'whyWhale1',
        '$2a$10$5.q.T.1X4UIHgrE2CGcNquu4K7lfX5ztWW8U6G59LJyGjd5BSgED2', 'USER', 'whyWhale1');

INSERT INTO delivery (id, address, zip_code)
VALUES (1, '123-1', '123-1');

insert into product_thunmnail_image (id, extension, file_name, origin_name, path, size)
values (1, 'png', '12391jni4', 'djnasd', 'testPath', 1203128),
       (2, 'png', '12391jni1', 'djnasd', 'testPath', 1203128),
       (3, 'png', '12391jni2', 'djnasd', 'testPath', 1203128),
       (4, 'png', '12391jni3', 'djnasd', 'testPath', 1203128);

insert into product (id, created_at, deleted, updated_at, is_display, name, price, quantity, category_id, owner_id,
                     product_thumbnail_id)
values (1, '2023-03-05 21:41:43', false, '2023-03-05 21:41:43', true, '테스트 상품1', 10000, 10000, null, null, 1),
       (2, '2023-03-05 21:41:43', false, '2023-03-05 21:41:43', true, '테스트 상품2', 10000, 10000, null, null, 2),
       (3, '2023-03-05 21:41:43', false, '2023-03-05 21:41:43', true, '테스트 상품3', 10000, 10000, null, null, 3),
       (4, '2023-03-05 21:41:43', false, '2023-03-05 21:41:43', true, '테스트 상품4', 10000, 10000, null, null, 4);

insert into orders (id, created_at, deleted, updated_at, user_id, delivery_id)
values (1, '2023-03-01 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (2, '2023-03-02 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (3, '2023-03-03 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (4, '2023-03-04 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (5, '2023-03-05 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (6, '2023-03-06 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (7, '2023-03-02 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (8, '2023-03-03 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (9, '2023-03-04 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (10, '2023-03-05 21:24:50', false, '2023-03-05 21:24:50', 1, 1),
       (11, '2023-03-01 21:24:50', false, '2023-03-05 21:24:50', 1, 1);

insert into order_product (id, created_at, deleted, updated_at, order_quantity, status, order_id, product_id,
                           user_coupon_id)
values (1, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 1, 1, null),
       (2, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 1, 2, null),
       (3, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 1, 3, null),
       (4, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 2, 1, null),
       (5, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 2, 2, null),
       (6, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 2, 3, null),
       (7, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 3, 1, null),
       (8, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 3, 2, null),
       (9, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 3, 3, null),
       (10, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 3, 4, null),
       (11, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 1, null),
       (12, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 5, 1, null),
       (13, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 2, null),
       (14, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 3, null),
       (15, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 4, null),
       (16, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 5, 1, null),
       (17, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 6, 2, null),
       (18, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 3, null),
       (19, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 4, null),
       (20, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 4, 2, null),
       (21, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 5, 1, null),
       (22, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 6, 1, null),
       (23, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 7, 2, null),
       (24, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 8, 2, null),
       (25, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 9, 1, null),
       (26, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 9, 2, null),
       (27, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 9, 3, null),
       (28, '2023-03-05 21:29:51', false, '2023-03-05 21:29:56', 2, 'ACCEPT', 9, 4, null);