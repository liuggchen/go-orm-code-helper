package org.shonminh.helper.sql;

import org.junit.Test;

import static org.junit.Assert.*;

public class SqlParserTest {

    @Test
    public void TestExecute() {
        String sql = "CREATE DATABASE IF NOT EXISTS demo_db;\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS `demo_db`.`test_tab` (\n" +
                "  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  user_id int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  parent_user_id varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "  no_id bigint(21) DEFAULT 0 NOT NULL,\n" +
                "  `tiny_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "\n" +
                "  `score` decimal(10,3)  DEFAULT '0.00' NOT NULL,\n" +
                "  create_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  update_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  UNIQUE (user_id, pareent_user_id),\n" +
                "  PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        SqlParser sqlParser = new SqlParser();
        String expect = "package model\n" +
                "\n" +
                "type TestTab struct {\n" +
                "\tId           uint64  `gorm:\"column:id;type:BIGINT(21) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL\"`\n" +
                "\tUserId       uint32  `gorm:\"column:user_id;type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "\tParentUserId string  `gorm:\"column:parent_user_id;type:VARCHAR(64);NOT NULL\"`\n" +
                "\tNoId         int64   `gorm:\"column:no_id;type:BIGINT(21);NOT NULL\"`\n" +
                "\tTinyName     string  `gorm:\"column:tiny_name;type:VARCHAR(64);NOT NULL\"`\n" +
                "\tScore        float64 `gorm:\"column:score;type:DECIMAL(10, 3);NOT NULL\"`\n" +
                "\tCreateTime   uint32  `gorm:\"column:create_time;type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "\tUpdateTime   uint32  `gorm:\"column:update_time;type:INT(11) UNSIGNED;NOT NULL\"`\n" +
                "}\n";
        sqlParser.Execute(sql);
        assertEquals(expect, sqlParser.getModelFileContent());
    }

    @Test
    public void TestExecute2() {
        String sql = "CREATE DATABASE IF NOT EXISTS demo_db;\n" +
                "\n" +
                "CREATE TABLE IF NOT EXISTS `demo_db`.`test_tab` (\n" +
                "  `id` bigint(21) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "  user_id int(11) unsigned DEFAULT 0 NOT NULL COMMENT '用户ID',\n" +
                "  parent_user_id varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL,\n" +
                "  no_id bigint(21) DEFAULT 0 NOT NULL comment '迷彩ID，1-u 0-n',\n" +
                "  `tiny_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' NOT NULL comment 123,\n" +
                "\n" +
                "  `score` decimal(10,3)  DEFAULT '0.00' NOT NULL,\n" +
                "  create_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  update_time int(11) unsigned DEFAULT 0 NOT NULL,\n" +
                "  UNIQUE (user_id, pareent_user_id),\n" +
                "  PRIMARY KEY (id)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci comment '操作记录个';";
        SqlParser sqlParser = new SqlParser();

        sqlParser.Execute(sql);

        System.out.printf("Model: %s\n", sqlParser.getModelFileContent());
        System.out.printf("Repo: %s\n", sqlParser.getRepoFileContent());
    }

    @Test
    // https://github.com/Shonminh/go-orm-code-helper/issues/16
    public void TestIssue16_can_not_work_with_goland_windows_version() {
        String sql = "CREATE TABLE group_job (\n" +
                "g_id int(10) unsigned NOT NULL AUTO_INCREMENT,\n" +
                "g_type tinyint(4) NOT NULL DEFAULT '1' COMMENT '1:TYPEA; 2:TYPEB',\n" +
                "g_valid_start timestamp NOT NULL DEFAULT '2001-01-01 00:00:00',\n" +
                "g_message text NOT NULL COMMENT 'Template, json format',\n" +
                "g_job_status tinyint(4) NOT NULL DEFAULT '1' COMMENT '0:invalid,1:valid',\n" +
                "g_a_id varchar(20) NOT NULL DEFAULT '' COMMENT 'account_id',\n" +
                "g_operator_id int(10) unsigned NOT NULL DEFAULT '0',\n" +
                "g_version tinyint(255) unsigned NOT NULL DEFAULT '1',\n" +
                "g_created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "g_updated_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "PRIMARY KEY (g_id) USING BTREE,\n" +
                "KEY g_type (g_type) USING BTREE\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        SqlParser sqlParser = new SqlParser();
        sqlParser.Execute(sql);
        String actual = sqlParser.getModelFileContent();
        String expect = "package model\n" +
                "\n" +
                "import (\n" +
                "\t\"time\"\n" +
                ")\n" +
                "\n" +
                "type GroupJob struct {\n" +
                "\tGId         uint32    `gorm:\"column:g_id;type:INT(10) UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;NOT NULL\"`\n" +
                "\tGType       int8      `gorm:\"column:g_type;type:TINYINT(4);NOT NULL\"`\n" +
                "\tGValidStart time.Time `gorm:\"column:g_valid_start;type:TIMESTAMP;NOT NULL\"`\n" +
                "\tGMessage    string    `gorm:\"column:g_message;type:TEXT;NOT NULL\"`\n" +
                "\tGJobStatus  int8      `gorm:\"column:g_job_status;type:TINYINT(4);NOT NULL\"`\n" +
                "\tGAId        string    `gorm:\"column:g_a_id;type:VARCHAR(20);NOT NULL\"`\n" +
                "\tGOperatorId uint32    `gorm:\"column:g_operator_id;type:INT(10) UNSIGNED;NOT NULL\"`\n" +
                "\tGVersion    uint8     `gorm:\"column:g_version;type:TINYINT(255) UNSIGNED;NOT NULL\"`\n" +
                "\tGCreatedAt  time.Time `gorm:\"column:g_created_at;type:TIMESTAMP;NOT NULL\"`\n" +
                "\tGUpdatedAt  time.Time `gorm:\"column:g_updated_at;type:TIMESTAMP;NOT NULL\"`\n" +
                "}\n";
        assertEquals(expect, actual);
    }

    @Test
    // // https://github.com/Shonminh/go-orm-code-helper/issues/24
    public void TestIssue24_support_primary_key_format() {
        String sql = "CREATE TABLE `user`\n" +
                "(\n" +
                "    `id`                     int unsigned primary key auto_increment,\n" +
                "    `name`                   varchar(24) not null unique,\n" +
                "    `password`               char(60)    not null,\n" +
                "    `randomSalt`             char(24)    not null,\n" +
                "    `create_time`            timestamp NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "    `update_time`            timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "    `api_permission_bitmask` int unsigned not null\n" +
                ");";
        SqlParser sqlParser = new SqlParser();
        sqlParser.Execute(sql);
        String actual = sqlParser.getModelFileContent();
        String expect = "package model\n" +
                "\n" +
                "import (\n" +
                "\t\"time\"\n" +
                ")\n" +
                "\n" +
                "type User struct {\n" +
                "\tId                   uint32    `gorm:\"column:id;type:INT UNSIGNED;PRIMARY_KEY;AUTO_INCREMENT;\"`\n" +
                "\tName                 string    `gorm:\"column:name;type:VARCHAR(24);NOT NULL\"`\n" +
                "\tPassword             string    `gorm:\"column:password;type:CHAR(60);NOT NULL\"`\n" +
                "\tRandomSalt           string    `gorm:\"column:randomSalt;type:CHAR(24);NOT NULL\"`\n" +
                "\tCreateTime           time.Time `gorm:\"column:create_time;type:TIMESTAMP;\"`\n" +
                "\tUpdateTime           time.Time `gorm:\"column:update_time;type:TIMESTAMP;\"`\n" +
                "\tApiPermissionBitmask uint32    `gorm:\"column:api_permission_bitmask;type:INT UNSIGNED;NOT NULL\"`\n" +
                "}\n";
        assertEquals(expect, actual);
    }
}