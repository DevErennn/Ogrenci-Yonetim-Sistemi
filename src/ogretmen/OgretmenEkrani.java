package ogretmen;

import core.*;
import model.*;
import ortak.DestekPaneli;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;

public class OgretmenEkrani extends JFrame {
    private Ogretmen ogretmen;

    public OgretmenEkrani(Ogretmen ogretmen) {
        this.ogretmen = ogretmen;

        setTitle("Öğretim Elemanı Paneli - " + ogretmen.getAd() + " (" + ogretmen.getVerilenDers() + ")");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(new Color(240, 248, 255));
        panelUst.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBilgi = new JLabel("Sayın " + ogretmen.getAd() + "   |   Verilen Ders: " + ogretmen.getVerilenDers());
        lblBilgi.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(220, 20, 60));
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFocusPainted(false);
        
        btnCikis.addActionListener(e -> {
            dispose();
            app.Main.main(new String[]{});
        });

        panelUst.add(lblBilgi, BorderLayout.CENTER);
        panelUst.add(btnCikis, BorderLayout.EAST);
        add(panelUst, BorderLayout.NORTH);

        int mesajSayisi = hesaplaMesajSayisi();

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Not Girişi", new NotGirisPaneli(ogretmen.getVerilenDers(), donemBelirle()));
        sekmeler.addTab("Devamsızlık Girişi", new DevamsizlikGirisPaneli(ogretmen.getVerilenDers()));
        sekmeler.addTab("Ders Programım", dersProgramiPaneliniOlustur());
        sekmeler.addTab("Mesaj Gönder", new OgretmenMesajPaneli(ogretmen.getAd()));
        
        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", new OgretmenGelenKutusuPaneli(ogretmen.getTcKimlik()));
        } else {
            sekmeler.addTab("Gelen Kutusu", new OgretmenGelenKutusuPaneli(ogretmen.getTcKimlik())); 
        }
        sekmeler.addTab("Destek", new DestekPaneli(ogretmen.getAd()));

        add(sekmeler, BorderLayout.CENTER);
    }

    private int hesaplaMesajSayisi() {
        int sayi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals(ogretmen.getTcKimlik())) {
                    sayi++;
                }
            }
        } catch (Exception e) {}
        return sayi;
    }

    private String donemBelirle() {
        LocalDate tarih = LocalDate.now();
        int ay = tarih.getMonthValue();
        int yil = tarih.getYear();
        
        if (ay >= 9 || ay == 1) {
            int egitimYili = ay >= 9 ? yil : (yil - 1);
            return egitimYili + "-" + (egitimYili + 1) + " Güz Dönemi";
        } else if (ay >= 2 && ay <= 6) {
            return (yil - 1) + "-" + yil + " Bahar Dönemi";
        } else {
            return (yil - 1) + "-" + yil + " Yaz Dönemi";
        }
    }

    private JPanel dersProgramiPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] kolonlar = {"Gün", "Saat", "Verilen Ders", "Derslik"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        model.addRow(new Object[]{"Pazartesi", "09:00 - 12:00", ogretmen.getVerilenDers(), "Amfi 1"});
        model.addRow(new Object[]{"Çarşamba", "13:00 - 15:00", ogretmen.getVerilenDers(), "Amfi 3"});
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }
}
