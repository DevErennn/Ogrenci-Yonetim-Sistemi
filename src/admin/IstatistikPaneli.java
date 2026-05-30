package admin;

import model.*;
import core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class IstatistikPaneli extends JPanel {
    private JTable tabloBolumler;
    private DefaultTableModel tabloBolumlerModeli;
    private DaireGrafikPaneli grafikPaneli;
    private OgrenciYonetici yonetici;

    public IstatistikPaneli(OgrenciYonetici yonetici) {
        this.yonetici = yonetici;
        setLayout(new GridLayout(1, 2, 10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Bölüm Adı", "Öğrenci Sayısı", "Bölüm Ortalaması"};
        tabloBolumlerModeli = new DefaultTableModel(kolonlar, 0);
        tabloBolumler = new JTable(tabloBolumlerModeli);
        
        JPanel panelSol = new JPanel(new BorderLayout());
        panelSol.setBorder(BorderFactory.createTitledBorder("Bölüm Detayları"));
        panelSol.add(new JScrollPane(tabloBolumler), BorderLayout.CENTER);

        grafikPaneli = new DaireGrafikPaneli();
        grafikPaneli.setBorder(BorderFactory.createTitledBorder("Bölümlere Göre Öğrenci Dağılımı"));

        add(panelSol);
        add(grafikPaneli);
    }

    public void verileriGuncelle() {
        tabloBolumlerModeli.setRowCount(0);
        int toplamOgrenci = 0;
        
        ArrayList<String> benzersizBolumler = new ArrayList<>();
        ArrayList<Integer> bolumKisiSayilari = new ArrayList<>();
        ArrayList<Double> bolumToplamNotlari = new ArrayList<>();

        for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
            toplamOgrenci++;
            String bolumAdi = ogr.getBolum();
            int index = benzersizBolumler.indexOf(bolumAdi);
            
            if (index == -1) {
                benzersizBolumler.add(bolumAdi);
                bolumKisiSayilari.add(1);
                bolumToplamNotlari.add(ogr.getOrtalama());
            } else {
                int mevcutKisi = bolumKisiSayilari.get(index);
                double mevcutNot = bolumToplamNotlari.get(index);
                bolumKisiSayilari.set(index, mevcutKisi + 1);
                bolumToplamNotlari.set(index, mevcutNot + ogr.getOrtalama());
            }
        }

        for (int i = 0; i < benzersizBolumler.size(); i++) {
            String bAd = benzersizBolumler.get(i);
            int kSayi = bolumKisiSayilari.get(i);
            double bOrt = bolumToplamNotlari.get(i) / kSayi; 
            
            Object[] bolumSatir = {bAd, kSayi, String.format("%.2f", bOrt)};
            tabloBolumlerModeli.addRow(bolumSatir);
        }

        grafikPaneli.verileriGuncelle(benzersizBolumler, bolumKisiSayilari, toplamOgrenci);
    }
}
