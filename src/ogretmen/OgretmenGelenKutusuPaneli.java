package ogretmen;

import core.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;

public class OgretmenGelenKutusuPaneli extends JPanel {
    private String tcKimlik;

    public OgretmenGelenKutusuPaneli(String tcKimlik) {
        this.tcKimlik = tcKimlik;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Gönderen", "Başlık", "Mesaj İçeriği"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25);

        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals(tcKimlik)) {
                    model.addRow(new Object[]{veri[0], core.OgrenciYonetici.sifreCoz(veri[2]), core.OgrenciYonetici.sifreCoz(veri[3])});
                }
            }
        } catch (Exception e) {}

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }
}
