CREATE TABLE shedlock(
    name       varchar(64) NOT NULL COMMENT '스케줄잠금이름',
    lock_until timestamp(3) NULL DEFAULT NULL COMMENT '잠금기간',
    locked_at  timestamp(3) NULL DEFAULT NULL COMMENT '잠금일시',
    locked_by  varchar(255) DEFAULT NULL COMMENT '잠금신청자',
    PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;