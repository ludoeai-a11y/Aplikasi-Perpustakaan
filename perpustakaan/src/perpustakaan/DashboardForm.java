package perpustakaan;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;

public class DashboardForm extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private final JLabel lblJumlahBuku = new JLabel("0");
    private final JLabel lblJumlahAnggota = new JLabel("0");
    private final JLabel lblJumlahPinjam = new JLabel("0");
    private final JLabel lblWaktu = new JLabel();

    private JButton btnLaporan;

    public DashboardForm() {
        initComponents();
        aturHakAkses();
        muatStatistik();
    }

    private void initComponents() {
        setTitle("Sistem Perpustakaan - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(920, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(buatHeader(), BorderLayout.NORTH);
        add(buatSidebar(), BorderLayout.WEST);
        add(buatKonten(), BorderLayout.CENTER);
        add(buatStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(buatMenuBar());

        // jam berjalan
        Timer timer = new Timer(1000, e -> lblWaktu.setText(
            new SimpleDateFormat("EEEE, dd MMMM yyyy  HH:mm:ss").format(new Date())));
        timer.setInitialDelay(0);
        timer.start();
    }

    // ================= HEADER =================
    private JPanel buatHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0, 51, 102));
        p.setBorder(new EmptyBorder(12, 15, 12, 15));

        JLabel judul = new JLabel("SISTEM MANAJEMEN PERPUSTAKAAN");
        judul.setFont(new Font("Verdana", Font.BOLD, 20));
        judul.setForeground(Color.WHITE);

        JLabel user = new JLabel("Login sebagai: " + UserSession.getNamaLengkap()
            + " (" + UserSession.getLevel() + ")");
        user.setFont(new Font("Tahoma", Font.PLAIN, 12));
        user.setForeground(new Color(204, 221, 238));

        p.add(judul, BorderLayout.WEST);
        p.add(user, BorderLayout.EAST);
        return p;
    }

    // ================= MENU BAR =================
    private JMenuBar buatMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu mFile = new JMenu("File");
        JMenuItem miBeranda = new JMenuItem("Beranda");
        miBeranda.addActionListener(e -> cardLayout.show(contentPanel, "beranda"));
        JMenuItem miLogout = new JMenuItem("Logout");
        miLogout.addActionListener(e -> logout());
        JMenuItem miKeluar = new JMenuItem("Keluar");
        miKeluar.addActionListener(e -> System.exit(0));
        mFile.add(miBeranda);
        mFile.addSeparator();
        mFile.add(miLogout);
        mFile.add(miKeluar);

        JMenu mData = new JMenu("Data");
        JMenuItem miBuku = new JMenuItem("Data Buku");
        miBuku.addActionListener(e -> cardLayout.show(contentPanel, "buku"));
        mData.add(miBuku);

        JMenu mTrans = new JMenu("Transaksi");
        JMenuItem miPinjam = new JMenuItem("Peminjaman");
        miPinjam.addActionListener(e -> cardLayout.show(contentPanel, "peminjaman"));
        JMenuItem miKembali = new JMenuItem("Pengembalian");
        miKembali.addActionListener(e -> cardLayout.show(contentPanel, "pengembalian"));
        mTrans.add(miPinjam);
        mTrans.add(miKembali);

        bar.add(mFile);
        bar.add(mData);
        bar.add(mTrans);
        return bar;
    }

    // ================= SIDEBAR =================
    private JPanel buatSidebar() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(221, 221, 221));
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(153, 153, 153)),
            new EmptyBorder(10, 10, 10, 10)));
        p.setPreferredSize(new Dimension(190, 0));

        JLabel lblMenu = new JLabel("MENU UTAMA");
        lblMenu.setFont(new Font("Verdana", Font.BOLD, 12));
        lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lblMenu);
        p.add(Box.createVerticalStrut(10));

        p.add(buatTombolMenu("Beranda", "beranda"));
        p.add(buatTombolMenu("Data Buku", "buku"));
        p.add(buatTombolMenu("Peminjaman", "peminjaman"));
        p.add(buatTombolMenu("Pengembalian", "pengembalian"));
        btnLaporan = buatTombolMenu("Laporan", "laporan");
        p.add(btnLaporan);

        p.add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("Logout");
        btnLogout.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnLogout.addActionListener(e -> logout());
        p.add(btnLogout);
        return p;
    }

    private JButton buatTombolMenu(String teks, String kartu) {
        JButton b = new JButton(teks);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setFont(new Font("Tahoma", Font.PLAIN, 12));
        b.addActionListener(e -> cardLayout.show(contentPanel, kartu));
        // jarak antar tombol
        b.setBorder(new CompoundBorder(
            new LineBorder(new Color(153, 153, 153)),
            new EmptyBorder(4, 10, 4, 10)));
        return b;
    }

    // ================= KONTEN =================
    private JPanel buatKonten() {
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buatBeranda(), "beranda");
        contentPanel.add(buatPlaceholder("Data Buku"), "buku");
        contentPanel.add(buatPlaceholder("Transaksi Peminjaman"), "peminjaman");
        contentPanel.add(buatPlaceholder("Transaksi Pengembalian"), "pengembalian");
        contentPanel.add(buatPlaceholder("Laporan"), "laporan");
        return contentPanel;
    }

    private JPanel buatBeranda() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel sambutan = new JLabel("Selamat datang, " + UserSession.getNamaLengkap() + "!");
        sambutan.setFont(new Font("Verdana", Font.BOLD, 18));
        sambutan.setBorder(new EmptyBorder(0, 0, 15, 0));
        p.add(sambutan, BorderLayout.NORTH);

        JPanel kartu = new JPanel(new GridLayout(1, 3, 15, 0));
        kartu.setOpaque(false);
        kartu.add(buatKartuStatistik("Total Judul Buku", lblJumlahBuku));
        kartu.add(buatKartuStatistik("Total Anggota", lblJumlahAnggota));
        kartu.add(buatKartuStatistik("Sedang Dipinjam", lblJumlahPinjam));

        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(kartu, BorderLayout.NORTH);
        p.add(atas, BorderLayout.CENTER);
        return p;
    }

    private JPanel buatKartuStatistik(String judul, JLabel nilai) {
        JPanel k = new JPanel(new BorderLayout());
        k.setBackground(new Color(238, 244, 250));
        k.setBorder(new CompoundBorder(
            new LineBorder(new Color(0, 51, 102)),
            new EmptyBorder(15, 15, 15, 15)));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Tahoma", Font.BOLD, 12));
        nilai.setFont(new Font("Verdana", Font.BOLD, 32));
        nilai.setForeground(new Color(0, 51, 102));

        k.add(lblJudul, BorderLayout.NORTH);
        k.add(nilai, BorderLayout.CENTER);
        return k;
    }

    private JPanel buatPlaceholder(String judul) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lbl = new JLabel(judul);
        lbl.setFont(new Font("Verdana", Font.BOLD, 18));
        JLabel info = new JLabel("Modul ini akan dibuat pada tahap berikutnya.");
        info.setFont(new Font("Tahoma", Font.ITALIC, 12));
        info.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel atas = new JPanel(new GridLayout(2, 1));
        atas.setOpaque(false);
        atas.add(lbl);
        atas.add(info);
        p.add(atas, BorderLayout.NORTH);
        return p;
    }

    // ================= STATUS BAR =================
    private JPanel buatStatusBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(221, 221, 221));
        p.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(153, 153, 153)),
            new EmptyBorder(4, 10, 4, 10)));

        JLabel kiri = new JLabel("User: " + UserSession.getUsername()
            + "  |  Level: " + UserSession.getLevel());
        kiri.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblWaktu.setFont(new Font("Tahoma", Font.PLAIN, 11));

        p.add(kiri, BorderLayout.WEST);
        p.add(lblWaktu, BorderLayout.EAST);
        return p;
    }

    // ================= HAK AKSES =================
    private void aturHakAkses() {
        // Admin: semua menu. Petugas: tanpa menu Laporan
        // (Data Buku untuk Petugas nanti dibuat read-only di modul Data Buku).
        if (!UserSession.isAdmin()) {
            btnLaporan.setEnabled(false);
            btnLaporan.setToolTipText("Hanya untuk Admin");
        }
    }

    // ================= DATA STATISTIK =================
    private void muatStatistik() {
        try (Connection c = Koneksi.getConnection();
             Statement st = c.createStatement()) {

            lblJumlahBuku.setText(hitung(st, "SELECT COUNT(*) FROM buku"));
            lblJumlahAnggota.setText(hitung(st, "SELECT COUNT(*) FROM anggota"));
            lblJumlahPinjam.setText(hitung(st,
                "SELECT COUNT(*) FROM transaksi WHERE status = 'Dipinjam'"));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal memuat statistik:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String hitung(Statement st, String sql) throws Exception {
        try (ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? String.valueOf(rs.getInt(1)) : "0";
        }
    }

    // ================= LOGOUT =================
    private void logout() {
        int pilih = JOptionPane.showConfirmDialog(this,
            "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilih == JOptionPane.YES_OPTION) {
            UserSession.clear();
            new LoginForm().setVisible(true);
            dispose();
        }
    }
}
