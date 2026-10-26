package ogrenci;

import database.DatabaseHelper;
import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
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
        tablo.setRowHeight(25);

        List<String> dersler = DatabaseHelper.bolumSinifDersleriGetir(ogrenci.getBolum(), ogrenci.getSinif());
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        String[] saatler = {"09:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00", "15:00 - 17:00"};

        Random rastgele = new Random(ogrenci.getBolum().hashCode() + ogrenci.getSinif()); 

        if (dersler.isEmpty()) {
            model.addRow(new Object[]{"-", "-", "Bu dönem için kayıtlı ders bulunamadı", "-"});
        } else {
            for (String ders : dersler) {
                String gun = gunler[rastgele.nextInt(gunler.length)];
                String saat = saatler[rastgele.nextInt(saatler.length)];
                String derslik = "Amfi " + (1 + rastgele.nextInt(5)); 
                model.addRow(new Object[]{gun, saat, ders, derslik});
            }
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
