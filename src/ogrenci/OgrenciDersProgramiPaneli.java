package ogrenci;

import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

public class OgrenciDersProgramiPaneli extends JPanel {
    public OgrenciDersProgramiPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Gün", "Saat", "Ders Adı", "Derslik"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);

        String[] dersler = bolumeGoreDersleriGetir(ogrenci.getBolum());
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        String[] saatler = {"09:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00", "15:00 - 17:00"};

        Random rastgele = new Random(ogrenci.getBolum().hashCode()); 

        for (int i = 0; i < dersler.length; i++) {
            String gun = gunler[rastgele.nextInt(gunler.length)];
            String saat = saatler[rastgele.nextInt(saatler.length)];
            String derslik = "Amfi " + (1 + rastgele.nextInt(5)); 
            model.addRow(new Object[]{gun, saat, dersler[i], derslik});
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }

    private String[] bolumeGoreDersleriGetir(String bolum) {
        java.util.List<String> derslerListesi = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("dersler.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 2 && veri[0].trim().equals(bolum)) {
                    derslerListesi.add(veri[1].trim());
                }
            }
        } catch (Exception e) {}
        
        if (derslerListesi.isEmpty()) {
            return new String[]{"Henüz ders atanmamış"};
        }
        
        return derslerListesi.toArray(new String[0]);
    }
}
