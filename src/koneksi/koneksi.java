/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author Hype
 */
public class koneksi {
    public static Connection getKoneksi() {
        try {
            String url = "jdbc:mysql://localhost:3306/laundry";
            String user = "root";
            String pass = "";

            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            return DriverManager.getConnection(url, user, pass);

        } catch (Exception e) {
            System.out.println("Koneksi gagal: " + e);
            return null;
        }
    }
}