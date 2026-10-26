package app;

import admin.*;
import core.*;
import database.DatabaseHelper;
import model.*;
import ogrenci.*;
import ogretmen.*;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // SQLite veritabanını ve tablolarını hazırla, varsa txt'den verileri aktar
        DatabaseHelper.initDatabase();

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame girisEkrani = new JFrame("Sisteme Giriş");
        girisEkrani.setSize(400, 350);
        girisEkrani.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        girisEkrani.setLocationRelativeTo(null);
        girisEkrani.setLayout(new BorderLayout());

        JPanel pnlOrta = new JPanel(new GridLayout(4, 2, 10, 10));
        pnlOrta.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        pnlOrta.add(new JLabel("Kullanıcı Tipi:"));
        String[] tipler = {"Öğrenci", "Öğretim Elemanı", "Admin"};
        JComboBox<String> cmbTip = new JComboBox<>(tipler);
        pnlOrta.add(cmbTip);

        pnlOrta.add(new JLabel("ID / TC Kimlik No:"));
        JTextField txtKullanici = new JTextField();
        pnlOrta.add(txtKullanici);

        pnlOrta.add(new JLabel("Şifre:"));
        JPasswordField txtSifre = new JPasswordField();
        pnlOrta.add(txtSifre);

        JButton btnGiris = new JButton("Giriş Yap");
        btnGiris.setBackground(new Color(70, 130, 180));
        btnGiris.setForeground(Color.WHITE);
        btnGiris.setFont(new Font("Arial", Font.BOLD, 14));

        pnlOrta.add(new JLabel("")); 
        pnlOrta.add(btnGiris);

        girisEkrani.add(new JLabel("Öğrenci Bilgi Sistemine Hoşgeldiniz", SwingConstants.CENTER), BorderLayout.NORTH);
        girisEkrani.add(pnlOrta, BorderLayout.CENTER);

        btnGiris.addActionListener(e -> {
            String tip = (String) cmbTip.getSelectedItem();
            String kadi = txtKullanici.getText().trim();
            String sifre = new String(txtSifre.getPassword()).trim();

            if (kadi.isEmpty() || sifre.isEmpty()) {
                JOptionPane.showMessageDialog(girisEkrani, "Lütfen alanları boş bırakmayınız!");
                return;
            }

            if ("Admin".equals(tip)) {
                if (kadi.equals("admin") && sifre.equals("admin123")) {
                    girisEkrani.dispose();
                    SwingUtilities.invokeLater(() -> new AnaEkran().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(girisEkrani, "Hatalı Admin Girişi!");
                }
            } 
            else if ("Öğretim Elemanı".equals(tip)) {
                Ogretmen girisYapanOgretmen = DatabaseHelper.ogretmenGirisKontrol(kadi, sifre);

                if (girisYapanOgretmen != null) {
                    girisEkrani.dispose();
                    Ogretmen finalOgr = girisYapanOgretmen;
                    SwingUtilities.invokeLater(() -> new OgretmenEkrani(finalOgr).setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(girisEkrani, "Öğretmen TC veya Şifre Hatalı!");
                }
            }
            else {
                try {
                    int id = Integer.parseInt(kadi);
                    Ogrenci ogrGonder = DatabaseHelper.ogrenciGirisKontrol(id, sifre);

                    if (ogrGonder != null) {
                        girisEkrani.dispose();
                        Ogrenci finalOgr = ogrGonder; 
                        SwingUtilities.invokeLater(() -> new OgrenciEkrani(finalOgr).setVisible(true));
                    } else {
                        JOptionPane.showMessageDialog(girisEkrani, "Öğrenci ID veya Şifre Hatalı!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(girisEkrani, "Öğrenci ID sayı olmalıdır!");
                }
            }
        });

        girisEkrani.setVisible(true);
    }
}
