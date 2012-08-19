package org.torpidity.tank.data;

import java.sql.*;

public class MySQL {
    private static MySQL mysql;
    private static Statement stmt;
    private static Connection con;

    private MySQL() {
        try {
            String db = "jdbc:mysql://127.0.0.1:3306/tankgame";
            String user = "dev";
            String pass = "password";

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(db, user, pass);
            stmt = con.createStatement();
            System.out.println("Connected to database successfully.");
        } catch (Exception e) {
            System.out.println("Connection to database failed.");
            System.exit(1);
        }
    }

    public static int connect() {
        if (mysql == null) {
            mysql = new MySQL();
            return 1;
        } else
            return 0;
    }

    public static int query(String s) {
        try {
            stmt.executeQuery(s);
        } catch (SQLException e) {
            return -1;
        }
        return 1;
    }

    public static int update(String s) {
        try {
            stmt.executeUpdate(s);
        } catch (SQLException e) {
            return -1;
        }
        return 1;
    }

    public static ResultSet result() {
        try {
            ResultSet r = stmt.getResultSet();
            boolean hasRows = r.next();
            if (hasRows)
                return stmt.getResultSet();
            else
                return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public static int close() {
        try {
            con.close();
        } catch (Exception e) {
            return -1;
        }
        return 1;
    }
}
