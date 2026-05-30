package ogrenci;

import model.Ogrenci;
import ortak.DestekPaneli;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class OgrenciEkrani extends JFrame {
    private Ogrenci ogrenci;

    public OgrenciEkrani(Ogrenci girisYapanOgrenci) {
        this.ogrenci = girisYapanOgrenci;

        setTitle("Öğrenci Bilgi Sistemi - " + ogrenci.getAd());
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Üst Panel ve Çıkış Butonu ---
        JPanel panelUstAna = new JPanel(new BorderLayout());
        panelUstAna.setBackground(new Color(240, 248, 255));
        panelUstAna.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBilgi = new JPanel(new GridLayout(2, 1));
        panelBilgi.setBackground(new Color(240, 248, 255));

        JLabel lblHosgeldin = new JLabel("Sayın " + ogrenci.getAd() + " (" + ogrenci.getId() + ")", SwingConstants.CENTER);
        lblHosgeldin.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblDurum = new JLabel("Bölüm: " + ogrenci.getBolum() + "   |   Genel Ortalama: " + ogrenci.getOrtalama(), SwingConstants.CENTER);
        lblDurum.setFont(new Font("Arial", Font.PLAIN, 14));

        panelBilgi.add(lblHosgeldin);
        panelBilgi.add(lblDurum);

        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(220, 20, 60)); 
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFont(new Font("Arial", Font.BOLD, 12));
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> {
            dispose(); 
            app.Main.main(new String[]{});
        });

        panelUstAna.add(panelBilgi, BorderLayout.CENTER);
        panelUstAna.add(btnCikis, BorderLayout.EAST);
        
        add(panelUstAna, BorderLayout.NORTH);

        int mesajSayisi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals(String.valueOf(ogrenci.getId()))) {
                    mesajSayisi++;
                }
            }
        } catch (Exception e) {}

        // --- SEKMELER ---
        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Profilim", new OgrenciProfilPaneli(ogrenci));
        sekmeler.addTab("Not Listesi", new OgrenciNotPaneli(ogrenci));
        sekmeler.addTab("Ders Programı", new OgrenciDersProgramiPaneli(ogrenci));
        sekmeler.addTab("Devamsızlık Durumu", new OgrenciDevamsizlikPaneli(ogrenci));
        sekmeler.addTab("Duyurular", new OgrenciDuyuruPaneli(ogrenci)); 
        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", new OgrenciGelenKutusuPaneli(ogrenci));
        } else {
            sekmeler.addTab("Gelen Kutusu", new OgrenciGelenKutusuPaneli(ogrenci)); 
        }
        sekmeler.addTab("Destek", new DestekPaneli(String.valueOf(ogrenci.getId())));
        
        add(sekmeler, BorderLayout.CENTER);
    }
}
