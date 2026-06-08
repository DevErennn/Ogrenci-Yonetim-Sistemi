package ogrenci;

import model.Ogrenci;
import core.OgrenciYonetici;
import javax.swing.*;
import java.awt.*;

public class OgrenciProfilPaneli extends JPanel {
    public OgrenciProfilPaneli(Ogrenci ogrenci) {
        setLayout(new GridLayout(11, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        add(new JLabel("Okul Numarası:"));
        add(new JLabel(String.valueOf(ogrenci.getId())));

        add(new JLabel("İsim Soyisim:"));
        add(new JLabel(ogrenci.getAd()));

        add(new JLabel("Bölüm:"));
        add(new JLabel(ogrenci.getBolum()));

        add(new JLabel("Sınıf:"));
        add(new JLabel(ogrenci.getSinif() + ". Sınıf"));

        add(new JLabel("Ortalama:"));
        add(new JLabel(String.valueOf(ogrenci.getOrtalama())));

        add(new JLabel("TC Kimlik No:"));
        JTextField txtTc = new JTextField(ogrenci.getTc());
        add(txtTc);

        add(new JLabel("E-Posta:"));
        JTextField txtEposta = new JTextField(ogrenci.getEposta());
        add(txtEposta);

        add(new JLabel("Telefon Numarası:"));
        JTextField txtTel = new JTextField(ogrenci.getTelefon());
        add(txtTel);

        add(new JLabel("Adres:"));
        JTextField txtAdres = new JTextField(ogrenci.getAdres());
        add(txtAdres);

        add(new JLabel(""));
        JButton btnKaydet = new JButton("Bilgileri Güncelle");
        btnKaydet.setBackground(new Color(60, 179, 113));
        btnKaydet.setForeground(Color.WHITE);
        add(btnKaydet);

        btnKaydet.addActionListener(e -> {
            ogrenci.setTc(txtTc.getText().trim());
            ogrenci.setEposta(txtEposta.getText().trim());
            ogrenci.setTelefon(txtTel.getText().trim());
            ogrenci.setAdres(txtAdres.getText().trim());

            OgrenciYonetici yonetici = new OgrenciYonetici();
            for (Ogrenci o : yonetici.getOgrenciListesi()) {
                if (o.getId() == ogrenci.getId()) {
                    o.setTc(ogrenci.getTc());
                    o.setEposta(ogrenci.getEposta());
                    o.setTelefon(ogrenci.getTelefon());
                    o.setAdres(ogrenci.getAdres());
                    break;
                }
            }
            yonetici.dosyayaYaz();

            JOptionPane.showMessageDialog(this, "Bilgileriniz güncellendi!");
        });
    }
}
