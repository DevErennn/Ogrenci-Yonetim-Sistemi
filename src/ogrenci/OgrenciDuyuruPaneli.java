package ogrenci;

import model.Ogrenci;
import javax.swing.*;
import java.awt.*;
import java.io.RandomAccessFile;

public class OgrenciDuyuruPaneli extends JPanel {
    public OgrenciDuyuruPaneli(Ogrenci ogrenci) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> liste = new JList<>(model);
        liste.setFont(new Font("Arial", Font.PLAIN, 14));

        try (RandomAccessFile raf = new RandomAccessFile("duyurular.dat", "r")) {
            while (raf.getFilePointer() < raf.length()) {
                String satir = raf.readUTF();
                String[] veri = satir.split(";");
                if (veri.length == 2) {
                    if (veri[0].equals("Genel") || veri[0].equals(ogrenci.getBolum())) {
                        model.addElement("[" + veri[0] + "] " + veri[1]);
                    }
                }
            }
        } catch (Exception e) {}
        
        add(new JScrollPane(liste), BorderLayout.CENTER);
    }
}
