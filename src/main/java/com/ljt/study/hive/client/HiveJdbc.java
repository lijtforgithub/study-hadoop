package com.ljt.study.hive.client;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author LiJingTang
 * @date 2024-12-24 18:37
 */
@Slf4j
public class HiveJdbc {

    public static void main(String[] args) throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");

        try (Connection conn = DriverManager.getConnection("jdbc:hive2://node01:10000/test_hive", "root", "");
             Statement stmt = conn.createStatement()) {
            String sql = "select * from p limit 5";
            ResultSet res = stmt.executeQuery(sql);
            while (res.next()) {
                System.out.println(res.getString(1) + "-" + res.getString("name"));
            }
        }
    }

}
