package ogrenci;

import database.DatabaseHelper;
import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OgrenciGelenKutusuPaneli extends JPanel {
    public OgrenciGelenKutusuPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Gönderen", "Başlık", "Mesaj İçeriği", "Tarih"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25); 

        List<String[]> mesajlar = DatabaseHelper.getGelenMesajlar(String.valueOf(ogrenci.getId()));
        for (String[] m : mesajlar) {
            model.addRow(new Object[]{m[0], m[1], m[2], m[3]});
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
