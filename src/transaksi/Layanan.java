/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transaksi;
import koneksi.koneksi;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.io.File;
import login.MenuUtama;
/**
 *
 * @author Hype
 */
public class Layanan extends javax.swing.JFrame {
      public static String userLogin;
      private int harga = 0;
      private String namaPelanggan = "";
    /**
     * Creates new form Layanan
     */
    public Layanan() {
    initComponents();
    setTanggalOtomatis();
  

    // posisi tengah
        setLocationRelativeTo(null);

    // tidak bisa maximize / resize
        setResizable(false);
    
    lbl_nama.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lbl_alamat.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    lbl_hp.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    loadLayanan();
    txtidtrx.setEditable(false);
    
        if (cb_lyn.getItemCount() > 0) {
        ambilHarga();
    }
        
    autoID(); 
    initTabel();
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    txttgl.setText(sdf.format(new java.util.Date()));
    }
    
    private void setTanggalOtomatis() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    java.util.Date date = new java.util.Date();

    txttgl.setText(format.format(date));
}
    
    private void simpanTransaksi() {

    try {

        // VALIDASI
        if (txtplg.getText().equals("")) {

            JOptionPane.showMessageDialog(
                    null,
                    "ID Pelanggan harus diisi!"
            );

            return;
        }

        if (model.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    null,
                    "Belum ada layanan!"
            );

            return;
        }

        Connection conn = koneksi.getKoneksi();

        // =========================
        // SIMPAN TRANSAKSI
        // =========================
        String sqlTransaksi =
        "INSERT INTO transaksi "
        + "(id_transaksi, tanggal, id_pelanggan, grand_total) "
        + "VALUES (?, ?, ?, ?)";

        PreparedStatement pstTransaksi =
                conn.prepareStatement(sqlTransaksi);

        pstTransaksi.setString(
                1,
                txtidtrx.getText()
        );

        pstTransaksi.setString(
                2,
                txttgl.getText()
        );

        pstTransaksi.setString(
                3,
                txtplg.getText()
        );

       pstTransaksi.setInt(
        4,
        Integer.parseInt(
                txtGrandTotal.getText()
        )
);

        pstTransaksi.executeUpdate();

        // =========================
        // DETAIL TRANSAKSI
        // =========================
        String sqlDetail =
        "INSERT INTO detail_transaksi "
        + "(id_transaksi, layanan, berat, harga, total) "
        + "VALUES (?, ?, ?, ?, ?)";

        PreparedStatement pstDetail =
                conn.prepareStatement(sqlDetail);

        for (int i = 0; i < model.getRowCount(); i++) {

            pstDetail.setString(
                    1,
                    txtidtrx.getText()
            );

            pstDetail.setString(
                    2,
                    model.getValueAt(i, 0).toString()
            );

            pstDetail.setDouble(
                    3,
                    Double.parseDouble(
                            model.getValueAt(i, 1).toString()
                    )
            );

            pstDetail.setDouble(
                    4,
                    Double.parseDouble(
                            model.getValueAt(i, 2).toString()
                    )
            );

            pstDetail.setDouble(
                    5,
                    Double.parseDouble(
                            model.getValueAt(i, 3).toString()
                    )
            );

            pstDetail.executeUpdate();
        }

        JOptionPane.showMessageDialog(
                null,
                "Transaksi berhasil disimpan!"
        );

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                null,
                "Error : " + e.getMessage()
        );
    }
}
    
    private void resetForm() {
    txtidtrx.setText("");
    txttgl.setText("");
    txtplg.setText("");
    txtGrandTotal.setText("");

    DefaultTableModel model = (DefaultTableModel) jtabel.getModel();
    model.setRowCount(0);
}
    private String autoIDBayar() {
    String id = "BYR001";

    try {
        Connection conn = koneksi.getKoneksi();
        String sql = "SELECT MAX(id_pembayaran) FROM pembayaran";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        if (rs.next()) {
            String max = rs.getString(1);

            if (max != null) {
                int num = Integer.parseInt(max.substring(3)) + 1;
                id = "BYR" + String.format("%03d", num);
            }
        }

    } catch (Exception e) {
        System.out.println(e);
    }

    return id;
}
    
    private void cetakStrukPDF() {
    try {
        Document document = new Document();
        String id = txtidtrx.getText();
         String path = "C:\\Users\\Hype\\Documents\\Kuliah\\struk\\struk_" + id + ".pdf";
        PdfWriter.getInstance(document, new FileOutputStream("C:\\Users\\Hype\\Documents\\Kuliah\\struk\\struk_" + id + ".pdf"));
        document.open();

        // Judul
        document.add(new Paragraph("=== STRUK LAUNDRY ===\n\n"));

        // Info transaksi
        document.add(new Paragraph("ID Transaksi : " + txtidtrx.getText()));
        document.add(new Paragraph("Tanggal      : " + txttgl.getText()));
        document.add(new Paragraph("Pelanggan    : " + txtplg.getText()));
        document.add(new Paragraph("\n"));
        
        document.add(new Paragraph("Kasir: " + userLogin));
        document.add(new Paragraph("Terima kasih telah menggunakan jasa kami"));

        // Tabel
        PdfPTable table = new PdfPTable(4);

        table.addCell("Layanan");
        table.addCell("Berat");
        table.addCell("Harga");
        table.addCell("Total");

        for (int i = 0; i < jtabel.getRowCount(); i++) {
            table.addCell(jtabel.getValueAt(i, 0).toString());
            table.addCell(jtabel.getValueAt(i, 1).toString());
            table.addCell(jtabel.getValueAt(i, 2).toString());
            table.addCell(jtabel.getValueAt(i, 3).toString());
        }

        document.add(table);

        document.add(new Paragraph("\nTotal Bayar : " + txtGrandTotal.getText()));

        document.close();

   
        Desktop.getDesktop().open(new File("C:\\Users\\Hype\\Documents\\Kuliah\\struk\\struk_" + id + ".pdf"));

        javax.swing.JOptionPane.showMessageDialog(null, "Struk berhasil dibuat!");

    } catch (Exception e) {
        e.printStackTrace();
    }
}
     private void autoID() {
        try {
            Connection conn = koneksi.getKoneksi();
            String sql = "SELECT MAX(id_transaksi) FROM transaksi";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                String max = rs.getString(1);
                if (max == null) {
                    txtidtrx.setText("TRX001");
                } else {
                    int num = Integer.parseInt(max.substring(3)) + 1;
                    txtidtrx.setText("TRX" + String.format("%03d", num));
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
     
     private void hitungGrandTotal() {
    int total = 0;

    for (int i = 0; i < model.getRowCount(); i++) {

        total += Integer.parseInt(
                model.getValueAt(i, 3).toString()
        );
    }

    txtGrandTotal.setText(String.valueOf(total));
}
     
      private void loadLayanan() {
        try {
            Connection conn = koneksi.getKoneksi();
            String sql = "SELECT * FROM layanan";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            cb_lyn.removeAllItems();

            while (rs.next()) {
                cb_lyn.addItem(rs.getString("nama_layanan"));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
      }
        
      
      private void ambilHarga() {
        try {
        if (cb_lyn.getSelectedItem() == null) return;

        Connection conn = koneksi.getKoneksi();
        String sql = "SELECT harga_per_kg FROM layanan WHERE nama_layanan=?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, cb_lyn.getSelectedItem().toString());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            harga = rs.getInt("harga_per_kg");
        }

    } catch (Exception e) {
        System.out.println(e);
    }
      }
      
      private void hitungTotal() {

    try {

        if (txtbrt.getText().equals("")) {

            txttotalHarga.setText("");
            return;
        }

        int berat =
                Integer.parseInt(txtbrt.getText());

        int total =
                berat * harga;

        // tampil ke field total
        txttotalHarga.setText(
                String.valueOf(total)
        );

    } catch (Exception e) {

        txttotalHarga.setText("");
    }
}
        
private DefaultTableModel model;
private void initTabel() {
    model = new DefaultTableModel();
    model.addColumn("Layanan");
    model.addColumn("Berat (Kg)");
    model.addColumn("Harga/Kg");
    model.addColumn("Total");

    jtabel.setModel(model);
}
         



     

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        cb_lyn = new javax.swing.JComboBox<>();
        txtbrt = new javax.swing.JTextField();
        txttgl = new javax.swing.JTextField();
        txtplg = new javax.swing.JTextField();
        txtidtrx = new javax.swing.JTextField();
        btncari = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lbl_nama = new javax.swing.JLabel();
        lbl_alamat = new javax.swing.JLabel();
        lbl_hp = new javax.swing.JLabel();
        btnTambah = new javax.swing.JButton();
        txtGrandTotal = new javax.swing.JTextField();
        btnHapus = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtabel = new javax.swing.JTable();
        btncek = new javax.swing.JButton();
        btnKembali2 = new javax.swing.JButton();
        btnlanjut1 = new javax.swing.JButton();
        txttotalHarga = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("ID Transaksi");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, -1));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("ID Pelanggan");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, -1, -1));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel3.setText("Tanggal");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 240, -1, 20));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel4.setText("Jenis Layanan");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 270, -1, -1));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel5.setText("Berat (Kg)");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 300, -1, -1));

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel6.setText("Total Harga");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 590, -1, 20));

        cb_lyn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cb_lyn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cb_lyn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cb_lynActionPerformed(evt);
            }
        });
        getContentPane().add(cb_lyn, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 270, 122, 20));

        txtbrt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtbrtKeyReleased(evt);
            }
        });
        getContentPane().add(txtbrt, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 300, 122, -1));

        txttgl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttglActionPerformed(evt);
            }
        });
        getContentPane().add(txttgl, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 240, 122, -1));

        txtplg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtplgActionPerformed(evt);
            }
        });
        getContentPane().add(txtplg, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 40, 105, -1));
        getContentPane().add(txtidtrx, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 10, 105, -1));

        btncari.setText("CARI");
        btncari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncariActionPerformed(evt);
            }
        });
        getContentPane().add(btncari, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 140, -1, -1));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 51, 51));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("DATA LAYANAN");
        jLabel7.setToolTipText("");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 60, 560, 50));

        lbl_nama.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_nama.setText("Nama");
        getContentPane().add(lbl_nama, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 140, -1, -1));

        lbl_alamat.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_alamat.setText("Alamat");
        getContentPane().add(lbl_alamat, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 170, -1, -1));

        lbl_hp.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_hp.setText("Nomor HP");
        getContentPane().add(lbl_hp, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 200, -1, -1));

        btnTambah.setText("TAMBAH");
        btnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTambahActionPerformed(evt);
            }
        });
        getContentPane().add(btnTambah, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 380, 90, 30));

        txtGrandTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGrandTotalActionPerformed(evt);
            }
        });
        getContentPane().add(txtGrandTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 590, 122, -1));

        btnHapus.setText("HAPUS");
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        getContentPane().add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 380, 80, 30));

        btnReset.setText("RESET");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        getContentPane().add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 380, 90, 30));

        jtabel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtabelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtabel);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 460, 515, 120));

        btncek.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btncek.setText("Cek Status Pengambilan Barang");
        btncek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncekActionPerformed(evt);
            }
        });
        getContentPane().add(btncek, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 430, 240, -1));

        btnKembali2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnKembali2.setText("Kembali");
        btnKembali2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKembali2ActionPerformed(evt);
            }
        });
        getContentPane().add(btnKembali2, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, -1));

        btnlanjut1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        btnlanjut1.setText("Lanjut");
        btnlanjut1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnlanjut1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnlanjut1, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 50, 100, -1));

        txttotalHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txttotalHargaActionPerformed(evt);
            }
        });
        getContentPane().add(txttotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 330, 122, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel9.setText("Harga");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 330, -1, -1));

        jLabel8.setIcon(new javax.swing.ImageIcon("C:\\Users\\ASUS\\Documents\\NetBeansProjects\\Aplikasi-Laundry\\src\\Images\\bg 1.jpg")); // NOI18N
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 860, 620));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cb_lynActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cb_lynActionPerformed
        // TODO add your handling code here:       
        ambilHarga();
        hitungTotal();
    }//GEN-LAST:event_cb_lynActionPerformed

    private void txtbrtKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtbrtKeyReleased
        // TODO add your handling code here:
        hitungTotal();
    }//GEN-LAST:event_txtbrtKeyReleased

    private void btncariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncariActionPerformed
        // TODO add your handling code here:
     try {
        Connection conn = koneksi.getKoneksi();
        String sql = "SELECT * FROM pelanggan WHERE id_pelanggan = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, txtplg.getText());

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

    namaPelanggan = rs.getString("nama");

    lbl_nama.setText("Nama : " + namaPelanggan);
    lbl_alamat.setText("Alamat : " + rs.getString("alamat"));
    lbl_hp.setText("No HP : " + rs.getString("no_hp"));

} else {
            javax.swing.JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
            
            // kosongkan label
        lbl_nama.setText("Nama : -");
        lbl_alamat.setText("Alamat : -");
        lbl_hp.setText("No HP : -");
        }

    } catch (Exception e) {
        System.out.println(e);
    }
    }//GEN-LAST:event_btncariActionPerformed

    private void txtplgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtplgActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtplgActionPerformed

    private void txttglActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttglActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttglActionPerformed

    private void btnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTambahActionPerformed
        // TODO add your handling code here:
         if (txtbrt.getText().equals("")) {

        JOptionPane.showMessageDialog(
                null,
                "Berat harus diisi!"
        );

        return;
    }

    try {

        String layanan =
                cb_lyn.getSelectedItem().toString();

        int berat =
                Integer.parseInt(txtbrt.getText());

        int total =
                Integer.parseInt(
                        txttotalHarga.getText()
                );

        model.addRow(new Object[]{

            layanan,
            berat,
            harga,
            total
        });

        hitungGrandTotal();

        txtbrt.setText("");
        txttotalHarga.setText("");

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
                null,
                "Input tidak valid"
        );
    }
        
    }//GEN-LAST:event_btnTambahActionPerformed

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
        int row = jtabel.getSelectedRow();

    if (row != -1) {
        model.removeRow(row);
        hitungGrandTotal();
    } else {
        javax.swing.JOptionPane.showMessageDialog(null, "Pilih data dulu!");
    }
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
            model.setRowCount(0);
            txtGrandTotal.setText("0");
    }//GEN-LAST:event_btnResetActionPerformed

    private void jtabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtabelMouseClicked
        // TODO add your handling code here:
         int row = jtabel.getSelectedRow();

    // DOUBLE CLICK = hapus data
    if (evt.getClickCount() == 2 && row != -1) {
    model.removeRow(row);
    hitungGrandTotal();
    }
    }//GEN-LAST:event_jtabelMouseClicked

    private void txtGrandTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGrandTotalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGrandTotalActionPerformed

    private void btncekActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncekActionPerformed
        transaksi.pengambilan p =
        new transaksi.pengambilan();

    // tampilkan form
    p.setVisible(true);

    // tutup form layanan
    this.dispose();
    }//GEN-LAST:event_btncekActionPerformed

    private void btnKembali2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKembali2ActionPerformed
        // TODO add your handling code here:
        new MenuUtama().setVisible(true);
    this.dispose();

    }//GEN-LAST:event_btnKembali2ActionPerformed

    private void btnlanjut1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnlanjut1ActionPerformed
        // TODO add your handling code here:
        // simpan transaksi dulu
    simpanTransaksi();

    // buka pembayaran
    pembayaran p = new pembayaran();

    // kirim data
    String grandTotal = txtGrandTotal.getText();

    p.setData(
            txtidtrx.getText(),
            grandTotal
    );

    p.setVisible(true);

    this.dispose();
    }//GEN-LAST:event_btnlanjut1ActionPerformed

    private void txttotalHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txttotalHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txttotalHargaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Layanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Layanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Layanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Layanan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            new Layanan().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnKembali2;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnTambah;
    private javax.swing.JButton btncari;
    private javax.swing.JButton btncek;
    private javax.swing.JButton btnlanjut1;
    private javax.swing.JComboBox<String> cb_lyn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jtabel;
    private javax.swing.JLabel lbl_alamat;
    private javax.swing.JLabel lbl_hp;
    private javax.swing.JLabel lbl_nama;
    private javax.swing.JTextField txtGrandTotal;
    private javax.swing.JTextField txtbrt;
    private javax.swing.JTextField txtidtrx;
    private javax.swing.JTextField txtplg;
    private javax.swing.JTextField txttgl;
    private javax.swing.JTextField txttotalHarga;
    // End of variables declaration//GEN-END:variables
}
