package ogrenci;

import database.DatabaseHelper;
import model.Ogrenci;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OgrenciDuyuruPaneli extends JPanel {
    public OgrenciDuyuruPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> liste = new JList<>(model);
        liste.setFont(new Font("Arial", Font.PLAIN, 14));

        List<String> duyurular = DatabaseHelper.ogrenciDuyurulariGetir(ogrenci.getBolum());
        if (duyurular.isEmpty()) {
            model.addElement("Henüz yayınlanmış bir duyuru bulunmamaktadır.");
        } else {
            for (String d : duyurular) {
                model.addElement(d);
            }
        }

        add(new JScrollPane(liste), BorderLayout.CENTER);
    }
}
