package admin;

import model.*;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnaEkran extends JFrame {
    private OgrenciYonetici yonetici;

    private AnaIslemlerPaneli anaIslemlerPaneli;
    private IstatistikPaneli istatistikPaneli;

    public AnaEkran() {
        yonetici = new OgrenciYonetici();

        setTitle("Öğrenci Yönetim Sistemi");
        setSize(1000, 650); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(new Color(240, 248, 255));
        panelUst.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblAdmin = new JLabel("Sistem Yöneticisi Paneli (Admin)");
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnCikis = new JButton("Çıkış Yap / Girişe Dön");
        btnCikis.setBackground(new Color(220, 20, 60)); 
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFocusPainted(false);

        btnCikis.addActionListener(e -> {
            dispose(); 
            app.Main.main(new String[]{}); 
        });

        panelUst.add(lblAdmin, BorderLayout.CENTER);
        panelUst.add(btnCikis, BorderLayout.EAST);
        add(panelUst, BorderLayout.NORTH); 

        int mesajSayisi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals("admin")) {
                    mesajSayisi++;
                }
            }
        } catch (Exception e) {}

        JTabbedPane sekmeler = new JTabbedPane();

        istatistikPaneli = new IstatistikPaneli(yonetici);

        DataUpdateListener guncellemeDinleyici = new DataUpdateListener() {
            @Override
            public void onDataUpdated() {
                istatistikPaneli.verileriGuncelle();
            }
        };

        anaIslemlerPaneli = new AnaIslemlerPaneli(yonetici, guncellemeDinleyici);

        sekmeler.addTab("Öğrenci İşlemleri", anaIslemlerPaneli);
        sekmeler.addTab("Bölüm İstatistikleri ve Grafik", istatistikPaneli);
        sekmeler.addTab("Ders, Öğretmen ve Duyuru", new DersYonetimPaneli());
        sekmeler.addTab("Mesaj Gönder", new AdminMesajPaneli());

        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", new AdminGelenKutusuPaneli());
        } else {
            sekmeler.addTab("Gelen Kutusu", new AdminGelenKutusuPaneli());
        }

        add(sekmeler, BorderLayout.CENTER);

        anaIslemlerPaneli.verileriGuncelle();
        istatistikPaneli.verileriGuncelle();

        JPanel panelAlt = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblSaat = new JLabel();
        lblSaat.setFont(new Font("Arial", Font.BOLD, 14));
        panelAlt.add(new JLabel("Sistem Saati: "));
        panelAlt.add(lblSaat);
        add(panelAlt, BorderLayout.SOUTH);

        Thread saatThread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            while (true) {
                lblSaat.setText(sdf.format(new Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        saatThread.start();
    }
}