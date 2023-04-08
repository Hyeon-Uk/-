-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema chatting
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema chatting
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `chatting` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `chatting` ;

-- -----------------------------------------------------
-- Table `chatting`.`member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chatting`.`member` (
                                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                   `created_at` DATETIME(6) NULL DEFAULT NULL,
    `updated_at` DATETIME(6) NULL DEFAULT NULL,
    `email` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `UK_mbmcqelty0fbrvxp1q58dn57t` (`email` ASC) VISIBLE,
    UNIQUE INDEX `UK_hh9kg6jti4n1eoiertn2k6qsc` (`nickname` ASC) VISIBLE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `chatting`.`friendship`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chatting`.`friendship` (
                                                       `member_id` BIGINT NOT NULL,
                                                       `friend_id` BIGINT NOT NULL,
                                                       UNIQUE INDEX `UKcvoks0qunlumv8ypt87kv7bjl` (`member_id` ASC, `friend_id` ASC) VISIBLE,
    INDEX `FKoiw3l97nclqk0k9xd0jsacsju` (`friend_id` ASC) VISIBLE,
    CONSTRAINT `FKoiw3l97nclqk0k9xd0jsacsju`
    FOREIGN KEY (`friend_id`)
    REFERENCES `chatting`.`member` (`id`),
    CONSTRAINT `FKsd6jhgw98wj7eas8f28ybf4hd`
    FOREIGN KEY (`member_id`)
    REFERENCES `chatting`.`member` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `chatting`.`membersecurity`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `chatting`.`membersecurity` (
                                                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                                                           `created_at` DATETIME(6) NULL DEFAULT NULL,
    `updated_at` DATETIME(6) NULL DEFAULT NULL,
    `blockedTime` DATETIME(6) NULL DEFAULT NULL,
    `salt` VARCHAR(255) NOT NULL,
    `tryCount` INT NULL DEFAULT '0',
    `member_id` BIGINT NULL DEFAULT NULL,
    PRIMARY KEY (`id`),
    INDEX `FKh3lolr39ro4rg0qyl910coevf` (`member_id` ASC) VISIBLE,
    CONSTRAINT `FKh3lolr39ro4rg0qyl910coevf`
    FOREIGN KEY (`member_id`)
    REFERENCES `chatting`.`member` (`id`))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8mb4
    COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
