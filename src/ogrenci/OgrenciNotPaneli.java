package ogrenci;

import database.DatabaseHelper;
import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OgrenciNotPaneli extends JPanel {
    public OgrenciNotPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Ders Adı", "Vize", "Final", "Harf Notu"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25);

        List<Object[]> notlar = DatabaseHelper.ogrenciNotlariniGetir(ogrenci.getId());
        for (Object[] satir : notlar) {
            model.addRow(satir);
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
