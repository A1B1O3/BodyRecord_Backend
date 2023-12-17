DROP database body_record;
CREATE database body_record;
USE body_record;

CREATE TABLE `exercise` (
                            `exercise_code`   int   AUTO_INCREMENT PRIMARY KEY NOT NULL,
                            `exercise_name`   varchar(45)   NULL,
                            `exercise_weight`   float   NULL,
                            `exercise_count`   int   NULL,
                            `exercise_time`   time   NULL,
                            `exercise_image_path`  varchar(1000)   NULL   ,
                            `exercise_share`   boolean   NOT NULL   ,
                            `exercise_date`   date   NOT NULL,
                            `member_code`   bigint   NOT NULL   ,
                            `created_at`   datetime(6)   NULL,
                            `modified_at`   datetime(6)   NULL,
                            `state`   enum('DELETED','USEABLE')   NOT NULL
);

CREATE TABLE `member` (
                          `member_code` bigint PRIMARY KEY AUTO_INCREMENT,
                          `member_socialid` VARCHAR(30) NOT NULL,
                          `member_name` VARCHAR(20) NOT NULL,
                          `member_image`  varchar(1000)   NULL   ,
                          `member_nickname` VARCHAR(255) ,
                          `role` VARCHAR(20),
                          `state` VARCHAR(20),
                          `created_at` DATETIME,
                          `modified_at` DATETIME,
                          CONSTRAINT unique_member_name UNIQUE (member_name)

);

CREATE TABLE `challenge` (
                             `challenge_code`   int   AUTO_INCREMENT PRIMARY KEY NOT NULL,
                             `challenge_title`   varchar(45)   NOT NULL,
                             `challenge_content`   varchar(500)   NULL,
                             `challenge_startdate`   date   NOT NULL,
                             `challenge_enddate`   date   NOT NULL,
                             `member_code`   bigint   NOT NULL   ,
                             `challengecategory_code`   int   NOT NULL,
                             `created_at`   datetime(6)   NULL,
                             `modified_at`   datetime(6)   NULL,
                             `state`   enum('USEABLE','DELETED','ENDED')   NOT NULL
);

CREATE TABLE `challenge_category` (
                                      `challengecategory_code`   int   AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                      `challengecategory_name`   varchar(45)   NOT NULL,
                                      `created_at`   datetime(6)   NULL,
                                      `modified_at`   datetime(6)   NULL,
                                      `state`   enum('DELETED','USEABLE')   NOT NULL
);

CREATE TABLE `body` (
                        `body_code` int AUTO_INCREMENT PRIMARY KEY NOT NULL,
                        `weight` float NOT NULL,
                        `fat` float NOT NULL,
                        `muscle` float NOT NULL,
                        `member_code` bigint NOT NULL,
                        `created_at` datetime(6) NULL,
                        `modified_at` datetime(6) NULL,
                        `state` enum('DELETED', 'USEABLE') NOT NULL,
                        CONSTRAINT `FK_body_member` FOREIGN KEY (`member_code`) REFERENCES `member` (`member_code`)
);

CREATE TABLE `report` (
                          `report_code`   iNT   AUTO_INCREMENT PRIMARY KEY NOT NULL,
                          `report_date`   date   NULL,
                          `report_content`   varchar(500)   NULL,
                          `challenge_code`   int   NULL,
                          `member_code`   bigint   NOT NULL,
                          `reportcategory_code`   int   NOT NULL,
                          `created_at`   datetime(6)   NULL,
                          `modified_at`   datetime(6)   NULL,
                          `state`   enum('UNPROCESSED','PROCESSED','RETURNED')   NOT NULL,
                          `challengecer_code`   int   NULL
);

CREATE TABLE `report_category` (
                                   `reportcategory_code`   int AUTO_INCREMENT PRIMARY KEY   NOT NULL,
                                   `reportcategory_name`   varchar(45)   NULL,
                                   `created_at`   datetime(6)   NULL,
                                   `modified_at`   datetime(6)   NULL,
                                   `state`   enum('USEABLE','DELETED')   NOT NULL
);

CREATE TABLE `challenge_participate` (
                                         `challengeparticipate_code`   int AUTO_INCREMENT PRIMARY KEY   NOT NULL,
                                         `member_code`   bigint   NOT NULL   ,
                                         `challenge_code`   int   NOT NULL,
                                         `created_at`   datetime(6)   NULL,
                                         `modified_at`   datetime(6)   NULL,
                                         `state`   enum('JOIN','LEAVE')   NOT NULL
);

CREATE TABLE `challenge_certification` (
                                           `challengecer_code`   int   AUTO_INCREMENT PRIMARY KEY NOT NULL,
                                           `challenge_image`   varchar(200)   NULL,
                                           `challenge_code`  int   NOT NULL,
                                           `created_at`   datetime(6)   NULL,
                                           `modified_at`   datetime(6)   NULL,
                                           `state`   enum('USEABLE','DELETED')   NOT NULL,

                                           `member_code`   bigint   NOT NULL
);

CREATE TABLE `token` (
                         `member_code` bigint NOT NULL,
                         `refresh_token` varchar(255) NOT NULL,
                         PRIMARY KEY (`member_code`),
                         CONSTRAINT `FK_member_TO_token_1` FOREIGN KEY (`member_code`) REFERENCES `member` (`member_code`)
);

ALTER TABLE `exercise` ADD CONSTRAINT `FK_member_TO_exercise_1` FOREIGN KEY (
                                                                             `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );

ALTER TABLE `challenge` ADD CONSTRAINT `FK_member_TO_challenge_1` FOREIGN KEY (
                                                                               `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );

ALTER TABLE `challenge` ADD CONSTRAINT `FK_challenge_category_TO_challenge_1` FOREIGN KEY (
                                                                                           `challengecategory_code`
    )
    REFERENCES `challenge_category` (
                                     `challengecategory_code`
        );

ALTER TABLE `body` ADD CONSTRAINT `FK_member_TO_body_1` FOREIGN KEY (
                                                                     `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );

ALTER TABLE `report` ADD CONSTRAINT `FK_challenge_TO_report_1` FOREIGN KEY (
                                                                            `challenge_code`
    )
    REFERENCES `challenge` (
                            `challenge_code`
        );

ALTER TABLE `report` ADD CONSTRAINT `FK_member_TO_report_1` FOREIGN KEY (
                                                                         `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );

ALTER TABLE `report` ADD CONSTRAINT `FK_report_category_TO_report_1` FOREIGN KEY (
                                                                                  `reportcategory_code`
    )
    REFERENCES `report_category` (
                                  `reportcategory_code`
        );

ALTER TABLE `report` ADD CONSTRAINT `FK_challenge_certification_TO_report_1` FOREIGN KEY (
                                                                                          `challengecer_code`
    )
    REFERENCES `challenge_certification` (
                                          `challengecer_code`
        );

ALTER TABLE `challenge_participate` ADD CONSTRAINT `FK_member_TO_challenge_participate_1` FOREIGN KEY (
                                                                                                       `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );

ALTER TABLE `challenge_participate` ADD CONSTRAINT `FK_challenge_TO_challenge_participate_1` FOREIGN KEY (
                                                                                                          `challenge_code`
    )
    REFERENCES `challenge` (
                            `challenge_code`
        );

ALTER TABLE `challenge_certification` ADD CONSTRAINT `FK_challenge_TO_challenge_certification_1` FOREIGN KEY (
                                                                                                              `challenge_code`
    )
    REFERENCES `challenge` (
                            `challenge_code`
        );

ALTER TABLE `challenge_certification` ADD CONSTRAINT `FK_member_TO_challenge_certification_1` FOREIGN KEY (
                                                                                                           `member_code`
    )
    REFERENCES `member` (
                         `member_code`
        );