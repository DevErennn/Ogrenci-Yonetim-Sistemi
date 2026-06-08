package ogrenci;

import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class OgrenciDevamsizlikPaneli extends JPanel {
    public OgrenciDevamsizlikPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Ders Adı", "Yapılan Devamsızlık", "Kalan Hak", "Durum"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);

        int devamsizlikSiniri = 8; 

        try (BufferedReader br = new BufferedReader(new FileReader("devamsizlik.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");

                if (veri.length == 3 && Integer.parseInt(veri[0]) == ogrenci.getId()) {
                    String dersAdi = veri[1];
                    int yapilanDevamsizlik = Integer.parseInt(veri[2]); 

                    int kalanHak = devamsizlikSiniri - yapilanDevamsizlik;
                    if (kalanHak < 0) kalanHak = 0; 

                    String durum = "";
                    if (yapilanDevamsizlik >= 8) {
                        durum = "DEVAMSIZLIKTAN KALDI";
                    } else if (yapilanDevamsizlik >= 6) { 
                        durum = "RİSKLİ";
                    } else {
                        durum = "İYİ";
                    }

                    Object[] tabloSatiri = {dersAdi, yapilanDevamsizlik, kalanHak, durum};
                    model.addRow(tabloSatiri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
