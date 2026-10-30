package perpustakaan;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.border.*;

public class LoginForm extends JFrame {

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnKeluar = new JButton("Keluar");

    public LoginForm() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Sistem Perpustakaan");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 330);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== Header (gaya web jadul: biru tua + teks putih) =====
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBackground(new Color(0, 51, 102));
        header.setBorder(new EmptyBorder(12, 15, 12, 15));
        JLabel lblJudul = new JLabel("SISTEM PERPUSTAKAAN");
        lblJudul.setFont(new Font("Verdana", Font.BOLD, 18));
        lblJudul.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("Silakan login untuk melanjutkan");
        lblSub.setFont(new Font("Verdana", Font.PLAIN, 11));
        lblSub.setForeground(new Color(204, 221, 238));
        header.add(lblJudul);
        header.add(lblSub);
        add(header, BorderLayout.NORTH);

        // ===== Form =====
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(238, 238, 238));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.anchor = GridBagConstraints.WEST;

        Font fontLabel = new Font("Tahoma", Font.PLAIN, 12);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(fontLabel);
        g.gridx = 0; g.gridy = 0;
        form.add(lblUser, g);
        g.gridx = 1;
        form.add(txtUsername, g);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(fontLabel);
        g.gridx = 0; g.gridy = 1;
        form.add(lblPass, g);
        g.gridx = 1;
        form.add(txtPassword, g);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        tombol.setOpaque(false);
        tombol.add(btnLogin);
        tombol.add(btnKeluar);
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        g.anchor = GridBagConstraints.CENTER;
        form.add(tombol, g);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new CompoundBorder(
            new EmptyBorder(15, 15, 10, 15),
            new LineBorder(new Color(153, 153, 153))));
        wrapper.add(form, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // ===== Footer =====
        JLabel footer = new JLabel("(c) Perpustakaan - Versi 1.0", SwingConstants.CENTER);
        footer.setFont(new Font("Tahoma", Font.PLAIN, 10));
        footer.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(footer, BorderLayout.SOUTH);

        // ===== Aksi =====
        btnLogin.addActionListener(e -> prosesLogin());
        btnKeluar.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnLogin); // tekan Enter = Login
    }

    private void prosesLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Username dan password harus diisi.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT id_user, username, nama_lengkap, level "
                   + "FROM users WHERE username = ? AND password = SHA2(?, 256)";

        try (Connection c = Koneksi.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSession.set(
                        rs.getInt("id_user"),
                        rs.getString("username"),
                        rs.getString("nama_lengkap"),
                        rs.getString("level"));

                    new DashboardForm().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Username atau password salah.",
                        "Login Gagal", JOptionPane.ERROR_MESSAGE);
                    txtPassword.setText("");
                    txtPassword.requestFocus();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal terhubung ke database:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
