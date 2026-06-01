package admin;

import model.*;
import core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IstatistikPaneli extends JPanel {
    private JTable tabloBolumler;
    private DefaultTableModel tabloBolumlerModeli;
    
    private JTable tabloBolumSiniflar;
    private DefaultTableModel tabloBolumSiniflarModeli;

    private JLabel lblToplamOgr, lblGenelOrt, lblEnBasariliBolum, lblEnKalabalikSinif, lblEnBasariliSinif;
    
    private DaireGrafikPaneli grafikBolumDaire;
    private SutunGrafikPaneli grafikBolumSutun;
    
    private JComboBox<String> cmbGrafikBolum;
    private JComboBox<String> cmbGrafikMetrik;
    
    private OgrenciYonetici yonetici;

    public IstatistikPaneli(OgrenciYonetici yonetici) {
        this.yonetici = yonetici;
        setLayout(new GridLayout(1, 2, 15, 15)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT SIDE: Tables and Analyses
        JPanel panelSol = new JPanel(new BorderLayout(10, 10));
        
        // Tablolar Paneli
        JPanel panelTablolar = new JPanel(new GridLayout(2, 1, 10, 10));
        
        // Bölüm Detayları Tablosu
        String[] bolumKolonlar = {"Bölüm Adı", "Öğrenci Sayısı", "Bölüm Ortalaması"};
        tabloBolumlerModeli = new DefaultTableModel(bolumKolonlar, 0);
        tabloBolumler = new JTable(tabloBolumlerModeli);
        JPanel panelBolumTablo = new JPanel(new BorderLayout());
        panelBolumTablo.setBorder(BorderFactory.createTitledBorder("Bölüm Genel Detayları"));
        panelBolumTablo.add(new JScrollPane(tabloBolumler), BorderLayout.CENTER);
        panelTablolar.add(panelBolumTablo);

        // Bölüm-Sınıf Detayları Tablosu
        String[] bolumSinifKolonlar = {"Bölüm - Sınıf", "Öğrenci Sayısı", "Sınıf Ortalaması"};
        tabloBolumSiniflarModeli = new DefaultTableModel(bolumSinifKolonlar, 0);
        tabloBolumSiniflar = new JTable(tabloBolumSiniflarModeli);
        JPanel panelBolumSinifTablo = new JPanel(new BorderLayout());
        panelBolumSinifTablo.setBorder(BorderFactory.createTitledBorder("Bölüm ve Sınıf Detayları"));
        panelBolumSinifTablo.add(new JScrollPane(tabloBolumSiniflar), BorderLayout.CENTER);
        panelTablolar.add(panelBolumSinifTablo);
        
        panelSol.add(panelTablolar, BorderLayout.CENTER);
        
        // Analizler Kartı
        JPanel panelAnalizler = new JPanel(new GridLayout(5, 1, 5, 5));
        panelAnalizler.setBorder(BorderFactory.createTitledBorder("Basit Analizler ve Sonuçlar"));
        panelAnalizler.setBackground(new Color(245, 245, 250));
        
        lblToplamOgr = new JLabel(" Toplam Öğrenci Sayısı: ");
        lblGenelOrt = new JLabel(" Üniversite Genel Ortalaması: ");
        lblEnBasariliBolum = new JLabel(" En Başarılı Bölüm: ");
        lblEnBasariliSinif = new JLabel(" En Başarılı Sınıf (Yıl): ");
        lblEnKalabalikSinif = new JLabel(" En Kalabalık Sınıf (Yıl): ");
        
        Font f = new Font("Arial", Font.BOLD, 12);
        lblToplamOgr.setFont(f);
        lblGenelOrt.setFont(f);
        lblEnBasariliBolum.setFont(f);
        lblEnBasariliSinif.setFont(f);
        lblEnKalabalikSinif.setFont(f);
        
        panelAnalizler.add(lblToplamOgr);
        panelAnalizler.add(lblGenelOrt);
        panelAnalizler.add(lblEnBasariliBolum);
        panelAnalizler.add(lblEnBasariliSinif);
        panelAnalizler.add(lblEnKalabalikSinif);
        
        panelSol.add(panelAnalizler, BorderLayout.SOUTH);
        
        // RIGHT SIDE: Graphs (Tabbed Pane)
        JTabbedPane panelGrafikler = new JTabbedPane();
        
        // 1. İnteraktif Daire Grafiği
        JPanel panelDaireKapsayici = new JPanel(new BorderLayout(5, 5));
        JPanel panelKontroller = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelKontroller.setBorder(BorderFactory.createEtchedBorder());
        
        cmbGrafikBolum = new JComboBox<>();
        cmbGrafikMetrik = new JComboBox<>(new String[]{"Öğrenci Sayısı", "Not Ortalaması"});
        
        panelKontroller.add(new JLabel("Bölüm:"));
        panelKontroller.add(cmbGrafikBolum);
        panelKontroller.add(new JLabel("İçerik/Metrik:"));
        panelKontroller.add(cmbGrafikMetrik);
        
        cmbGrafikBolum.addActionListener(e -> grafigiGuncelle());
        cmbGrafikMetrik.addActionListener(e -> grafigiGuncelle());
        
        grafikBolumDaire = new DaireGrafikPaneli();
        grafikBolumDaire.setBorder(BorderFactory.createTitledBorder("Dağılım Grafiği (Daire)"));
        
        panelDaireKapsayici.add(panelKontroller, BorderLayout.NORTH);
        panelDaireKapsayici.add(grafikBolumDaire, BorderLayout.CENTER);

        // 2. Sütun Grafiği
        grafikBolumSutun = new SutunGrafikPaneli();
        grafikBolumSutun.setBorder(BorderFactory.createTitledBorder("Bölüm Öğrenci Sayıları"));
        
        panelGrafikler.addTab("Bölüm/Sınıf Dağılımı (Daire)", panelDaireKapsayici);
        panelGrafikler.addTab("Bölüm Öğrenci Sayıları (Sütun)", grafikBolumSutun);
        
        add(panelSol);
        add(panelGrafikler);
    }

    private void grafigiGuncelle() {
        String seciliBolum = (String) cmbGrafikBolum.getSelectedItem();
        String seciliMetrik = (String) cmbGrafikMetrik.getSelectedItem();
        
        if (seciliBolum == null || seciliMetrik == null) return;
        
        ArrayList<String> etiketler = new ArrayList<>();
        ArrayList<Double> degerler = new ArrayList<>();
        double toplam = 0.0;
        
        boolean metrikOgrenciSayisi = seciliMetrik.equals("Öğrenci Sayısı");
        
        if (seciliBolum.equals("Tüm Bölümler")) {
            // Dilimler: Bölümler
            Map<String, ArrayList<Double>> bolumOrtMap = new HashMap<>();
            for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
                String bolum = ogr.getBolum();
                bolumOrtMap.putIfAbsent(bolum, new ArrayList<>());
                bolumOrtMap.get(bolum).add(ogr.getOrtalama());
            }
            
            for (Map.Entry<String, ArrayList<Double>> entry : bolumOrtMap.entrySet()) {
                String b = entry.getKey();
                ArrayList<Double> notlar = entry.getValue();
                etiketler.add(b);
                if (metrikOgrenciSayisi) {
                    double count = notlar.size();
                    degerler.add(count);
                    toplam += count;
                } else {
                    double sum = 0;
                    for (double n : notlar) sum += n;
                    double avg = sum / notlar.size();
                    degerler.add(avg);
                    toplam += avg;
                }
            }
        } else {
            // Dilimler: Sınıflar (1., 2., 3., 4. Sınıf)
            Map<Integer, ArrayList<Double>> sinifOrtMap = new HashMap<>();
            for (int i = 1; i <= 4; i++) {
                sinifOrtMap.put(i, new ArrayList<>());
            }
            
            for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
                if (ogr.getBolum().equalsIgnoreCase(seciliBolum)) {
                    sinifOrtMap.get(ogr.getSinif()).add(ogr.getOrtalama());
                }
            }
            
            for (int i = 1; i <= 4; i++) {
                ArrayList<Double> notlar = sinifOrtMap.get(i);
                if (!notlar.isEmpty() || metrikOgrenciSayisi) {
                    etiketler.add(i + ". Sınıf");
                    if (metrikOgrenciSayisi) {
                        double count = notlar.size();
                        degerler.add(count);
                        toplam += count;
                    } else {
                        double sum = 0;
                        for (double n : notlar) sum += n;
                        double avg = sum / notlar.size();
                        degerler.add(avg);
                        toplam += avg;
                    }
                }
            }
        }
        
        // Etiketleri görsel olarak zenginleştir
        ArrayList<String> formatliEtiketler = new ArrayList<>();
        for (int i = 0; i < etiketler.size(); i++) {
            double val = degerler.get(i);
            if (metrikOgrenciSayisi) {
                formatliEtiketler.add(etiketler.get(i) + " (" + (int)val + " Öğrenci)");
            } else {
                formatliEtiketler.add(etiketler.get(i) + " (Ort: " + String.format("%.2f", val) + ")");
            }
        }
        
        grafikBolumDaire.verileriGuncelle(formatliEtiketler, degerler, toplam);
    }

    public void verileriGuncelle() {
        tabloBolumlerModeli.setRowCount(0);
        tabloBolumSiniflarModeli.setRowCount(0);
        
        int toplamOgrenci = 0;
        double toplamOrtalama = 0.0;
        
        // 1. Veri Yapıları
        Map<String, ArrayList<Double>> bolumNotlari = new HashMap<>();
        Map<String, ArrayList<Double>> bolumSinifNotlari = new HashMap<>();
        Map<Integer, ArrayList<Double>> sinifNotlari = new HashMap<>();
        Map<Integer, Integer> sinifOgrenciSayilari = new HashMap<>();
        
        // Öğrenci listesini tara
        for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
            toplamOgrenci++;
            toplamOrtalama += ogr.getOrtalama();
            
            // Bölüm
            String bolum = ogr.getBolum();
            bolumNotlari.putIfAbsent(bolum, new ArrayList<>());
            bolumNotlari.get(bolum).add(ogr.getOrtalama());
            
            // Bölüm - Sınıf
            String bolumSinif = bolum + " - " + ogr.getSinif() + ". Sınıf";
            bolumSinifNotlari.putIfAbsent(bolumSinif, new ArrayList<>());
            bolumSinifNotlari.get(bolumSinif).add(ogr.getOrtalama());
            
            // Sınıf
            int sinif = ogr.getSinif();
            sinifNotlari.putIfAbsent(sinif, new ArrayList<>());
            sinifNotlari.get(sinif).add(ogr.getOrtalama());
            sinifOgrenciSayilari.put(sinif, sinifOgrenciSayilari.getOrDefault(sinif, 0) + 1);
        }
        
        if (toplamOgrenci == 0) {
            lblToplamOgr.setText(" Toplam Öğrenci Sayısı: 0");
            lblGenelOrt.setText(" Üniversite Genel Ortalaması: 0.00");
            lblEnBasariliBolum.setText(" En Başarılı Bölüm: -");
            lblEnBasariliSinif.setText(" En Başarılı Sınıf (Yıl): -");
            lblEnKalabalikSinif.setText(" En Kalabalık Sınıf (Yıl): -");
            return;
        }
        
        // 2. Bölüm tablosunu doldur
        ArrayList<String> benzersizBolumler = new ArrayList<>();
        ArrayList<Integer> bolumOgrSayilari = new ArrayList<>();
        
        double enYuksekBolumOrt = -1;
        String enBasariliBolumAdi = "-";
        
        for (Map.Entry<String, ArrayList<Double>> entry : bolumNotlari.entrySet()) {
            String bAd = entry.getKey();
            ArrayList<Double> notlar = entry.getValue();
            int ogrSayi = notlar.size();
            
            double toplam = 0;
            for (double n : notlar) toplam += n;
            double ort = toplam / ogrSayi;
            
            tabloBolumlerModeli.addRow(new Object[]{bAd, ogrSayi, String.format("%.2f", ort)});
            
            benzersizBolumler.add(bAd);
            bolumOgrSayilari.add(ogrSayi);
            
            if (ort > enYuksekBolumOrt) {
                enYuksekBolumOrt = ort;
                enBasariliBolumAdi = bAd + " (" + String.format("%.2f", ort) + ")";
            }
        }
        
        // 3. Bölüm - Sınıf tablosunu doldur
        for (Map.Entry<String, ArrayList<Double>> entry : bolumSinifNotlari.entrySet()) {
            String bsAd = entry.getKey();
            ArrayList<Double> notlar = entry.getValue();
            int ogrSayi = notlar.size();
            
            double toplam = 0;
            for (double n : notlar) toplam += n;
            double ort = toplam / ogrSayi;
            
            tabloBolumSiniflarModeli.addRow(new Object[]{bsAd, ogrSayi, String.format("%.2f", ort)});
        }
        
        // 4. Sınıf analizleri (Basit Analizler için)
        double enYuksekSinifOrt = -1;
        int enBasariliSinifYili = -1;
        for (Map.Entry<Integer, ArrayList<Double>> entry : sinifNotlari.entrySet()) {
            int sYili = entry.getKey();
            ArrayList<Double> notlar = entry.getValue();
            double toplam = 0;
            for (double n : notlar) toplam += n;
            double ort = toplam / notlar.size();
            
            if (ort > enYuksekSinifOrt) {
                enYuksekSinifOrt = ort;
                enBasariliSinifYili = sYili;
            }
        }
        
        int enKalabalikSinifYili = -1;
        int maxOgrSayisi = -1;
        for (Map.Entry<Integer, Integer> entry : sinifOgrenciSayilari.entrySet()) {
            if (entry.getValue() > maxOgrSayisi) {
                maxOgrSayisi = entry.getValue();
                enKalabalikSinifYili = entry.getKey();
            }
        }
        
        // 5. Analiz kartlarını güncelle
        lblToplamOgr.setText(" Toplam Öğrenci Sayısı: " + toplamOgrenci);
        lblGenelOrt.setText(String.format(" Üniversite Genel Ortalaması: %.2f", (toplamOrtalama / toplamOgrenci)));
        lblEnBasariliBolum.setText(" En Başarılı Bölüm: " + enBasariliBolumAdi);
        lblEnBasariliSinif.setText(" En Başarılı Sınıf (Yıl): " + (enBasariliSinifYili == -1 ? "-" : enBasariliSinifYili + ". Sınıf (" + String.format("%.2f", enYuksekSinifOrt) + ")"));
        lblEnKalabalikSinif.setText(" En Kalabalık Sınıf (Yıl): " + (enKalabalikSinifYili == -1 ? "-" : enKalabalikSinifYili + ". Sınıf (" + maxOgrSayisi + " Öğrenci)"));
        
        // 6. Bölüm Seçim Combo Box'ını güncelle (seçili değeri koru)
        Object selectedBolum = cmbGrafikBolum.getSelectedItem();
        
        java.awt.event.ActionListener[] listeners = cmbGrafikBolum.getActionListeners();
        for (java.awt.event.ActionListener l : listeners) {
            cmbGrafikBolum.removeActionListener(l);
        }
        
        cmbGrafikBolum.removeAllItems();
        cmbGrafikBolum.addItem("Tüm Bölümler");
        for (String b : benzersizBolumler) {
            cmbGrafikBolum.addItem(b);
        }
        
        if (selectedBolum != null) {
            cmbGrafikBolum.setSelectedItem(selectedBolum);
        } else {
            cmbGrafikBolum.setSelectedIndex(0);
        }
        
        for (java.awt.event.ActionListener l : listeners) {
            cmbGrafikBolum.addActionListener(l);
        }

        // 7. İnteraktif daire grafiğini ve sütun grafiğini güncelle
        grafigiGuncelle();
        grafikBolumSutun.verileriGuncelle(benzersizBolumler, bolumOgrSayilari);
    }
}
