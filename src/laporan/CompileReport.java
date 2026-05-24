package laporan;

import net.sf.jasperreports.engine.JasperCompileManager;

public class CompileReport {

    public static void main(String[] args) {

        try {

            // LAPORAN PELANGGAN
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanPelanggan.jrxml"
            );

            // LAPORAN TRANSAKSI
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanTransaksi.jrxml"
            );

            // LAPORAN PEMBAYARAN
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanPembayaran.jrxml"
            );

            // LAPORAN PENDAPATAN
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanPendapatan.jrxml"
            );

            // LAPORAN PENGAMBILAN
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanPengambilan.jrxml"
            );

            // LAPORAN NOTA
            JasperCompileManager.compileReportToFile(
                    "src/laporan/laporanNota.jrxml"
            );

            System.out.println(
                    "Semua laporan berhasil di compile!"
            );

        } catch (Exception e) {

            System.out.println(
                    "Error : " + e
            );
        }
    }
}