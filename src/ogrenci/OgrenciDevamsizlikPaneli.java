package ogrenci;

import database.DatabaseHelper;
import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OgrenciDevamsizlikPaneli extends JPanel {
    public OgrenciDevamsizlikPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Ders Adı", "Yapılan Devamsızlık (Saat)", "Kalan Hak", "Durum"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25);

        List<Object[]> kayitlar = DatabaseHelper.ogrenciDevamsizliklariniGetir(ogrenci.getId());
        for (Object[] satir : kayitlar) {
            model.addRow(satir);
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
