/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JComboBox;
/**
 *
 * @author Hype
 */
public class loadLayanan {

    public void load(javax.swing.JComboBox cb_lyn) {
        try {
            Connection conn = koneksi.getKoneksi();
            String sql = "SELECT * FROM layanan";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                cb_lyn.addItem(rs.getString("nama_layanan"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}