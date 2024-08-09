package com.learningapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHelper {
//    private static final String DB_URL = "jdbc:mysql://localhost:3306/KidsApp";
//    private static final String DB_USER = "root";
//    private static final String DB_PASSWORD = "";
//    private static final String TABLE_NAME = "results";

//    public static Connection getConnection() {
//        try {
//            Class.forName("com.mysql.jdbc.Driver");
//            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
//        } catch (ClassNotFoundException | SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

//    public static void insertResult(int totalCorrectAnswers) {
//        Connection connection = getConnection();
//        if (connection != null) {
//            try {
//                String sql = "INSERT INTO " + TABLE_NAME + " (total_correct_answers) VALUES (?)";
//                PreparedStatement statement = connection.prepareStatement(sql);
//                statement.setInt(1, totalCorrectAnswers);
//                int rowsAffected = statement.executeUpdate();
//                if (rowsAffected > 0) {
//                    Log.d("DatabaseHelper", "Total correct answers saved to database.");
//                } else {
//                    Log.e("DatabaseHelper", "Failed to save total correct answers to database.");
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            Log.e("DatabaseHelper", "Failed to establish database connection.");
//        }
//    }
}