package ogrenci;

import model.Ogrenci;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.io.File;

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

        try (Scanner scanner = new Scanner(new File("notlar.txt"))) {
            while (scanner.hasNextLine()) {
                String satir = scanner.nextLine();
                String[] veri = satir.split(";");

                if (veri.length == 4 && Integer.parseInt(veri[0]) == ogrenci.getId()) {
                    String dersAdi = veri[1];
                    int vizeNotu = Integer.parseInt(veri[2]);
                    int finalNotu = Integer.parseInt(veri[3]);

                    int yapilanDevamsizlik = dersinDevamsizliginiGetir(ogrenci, dersAdi); 

                    double dersOrtalamasi = (vizeNotu * 0.4) + (finalNotu * 0.6);
                    String harfNotu;

                    if (yapilanDevamsizlik >= 8) {
                        harfNotu = "DZ"; 
                    } else {
                        harfNotu = harfNotuHesapla(dersOrtalamasi);
                    }

                    Object[] tabloSatiri = {dersAdi, vizeNotu, finalNotu, harfNotu};
                    model.addRow(tabloSatiri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(new JScrollPane(tablo), BorderLayout.CENTER);
    }

    private int dersinDevamsizliginiGetir(Ogrenci ogrenci, String dersAdi) {
        try (BufferedReader br = new BufferedReader(new FileReader("devamsizlik.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 3 && Integer.parseInt(veri[0]) == ogrenci.getId() && veri[1].equals(dersAdi)) {
                    return Integer.parseInt(veri[2]);
                }
            }
        } catch (Exception e) {}
        return 0; 
    }

    private String harfNotuHesapla(double ortalama) {
        if (ortalama >= 85) return "AA";
        else if (ortalama >= 75) return "BA";
        else if (ortalama >= 65) return "BB";
        else if (ortalama >= 55) return "CB";
        else if (ortalama >= 50) return "CC";
        else if (ortalama >= 40) return "DC";
        else return "FF";
    }
}
