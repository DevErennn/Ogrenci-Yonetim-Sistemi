package app;

import admin.*;
import core.*;
import model.*;
import ogrenci.*;
import ogretmen.*;
import ortak.*;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        OgrenciYonetici yonetici = new OgrenciYonetici();

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

            if (tip.equals("Admin")) {
                if (kadi.equals("admin") && sifre.equals("admin123")) {
                    girisEkrani.dispose();
                    SwingUtilities.invokeLater(() -> new AnaEkran().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(girisEkrani, "Hatalı Admin Girişi!");
                }
            } 
            else if (tip.equals("Öğretim Elemanı")) {
                boolean girisBasarili = false;
                Ogretmen girisYapanOgretmen = null;

                try (BufferedReader br = new BufferedReader(new FileReader("ogretmenler.txt"))) {
                    String satir;
                    while ((satir = br.readLine()) != null) {
                        String[] veri = satir.split(";");
                        if (veri.length >= 4) {
                            String dTc = veri[0];
                            String dSifreBase64 = veri[1];
                            String dSifreCozulmus = OgrenciYonetici.sifreCoz(dSifreBase64); 

                            if (dTc.equals(kadi) && dSifreCozulmus.equals(sifre)) {
                                girisBasarili = true;
                                girisYapanOgretmen = new Ogretmen(veri[0], dSifreCozulmus, veri[2], veri[3]);
                                break;
                            }
                        }
                    }
                } catch (Exception ex) {}

                if (girisBasarili && girisYapanOgretmen != null) {
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
                    boolean girisBasarili = false;
                    Ogrenci ogrGonder = null;

                    for (Ogrenci o : yonetici.getOgrenciListesi()) {
                        if (o.getId() == id && o.getSifre().equals(sifre)) {
                            girisBasarili = true;
                            ogrGonder = o;
                            break;
                        }
                    }

                    if (girisBasarili && ogrGonder != null) {
                        girisEkrani.dispose();
                        Ogrenci finalOgr = ogrGonder; 
                        SwingUtilities.invokeLater(() -> new OgrenciEkrani(finalOgr).setVisible(true));
                    } else {
                        JOptionPane.showMessageDialog(girisEkrani, "Öğrenci ID veya Şifre Hatalı!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(girisEkrani, "Öğrenci ID sayı olmalıdır!");
                }
            }
        });

        girisEkrani.setVisible(true);
    }
}
