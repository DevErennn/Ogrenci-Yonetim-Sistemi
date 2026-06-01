package ogretmen;

import core.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.RandomAccessFile;

public class OgretmenMesajPaneli extends JPanel {
    private String ogretmenAdi;

    public OgretmenMesajPaneli(String ogretmenAdi) {
        this.ogretmenAdi = ogretmenAdi;
        setLayout(new GridLayout(8, 1, 10, 5));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        String[] secenekler = {"Öğrenci (ID Giriniz)", "Öğretmen (TC Giriniz)", "Bölüme Duyuru (Bölüm Adı Seçiniz)"};
        JComboBox<String> cmbKime = new JComboBox<>(secenekler);
        
        JTextField txtHedef = new JTextField();
        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Mesajı / Duyuruyu Gönder");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        add(new JLabel("Alıcı Türü:"));
        add(cmbKime);
        add(new JLabel("Alıcı (Öğrenci ID / Öğretmen TC / Bölüm Adı):"));
        add(txtHedef);
        add(new JLabel("Başlık:"));
        add(txtBaslik);
        add(new JLabel("Mesajınız:"));
        add(txtMesaj);
        add(btnGonder);

        cmbKime.addActionListener(e -> {
            if (cmbKime.getSelectedIndex() == 2) {
                txtHedef.setText("Genel veya Bölüm Adı (örn: Bilgisayar Mühendisliği)");
            } else {
                txtHedef.setText("");
            }
        });

        btnGonder.addActionListener(e -> {
            String tur = (String) cmbKime.getSelectedItem();
            String hedef = txtHedef.getText().trim();
            String baslik = txtBaslik.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (hedef.isEmpty() || mesaj.isEmpty() || baslik.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            if (cmbKime.getSelectedIndex() == 2) {
                // Duyuru olarak kaydet
                try (RandomAccessFile raf = new RandomAccessFile("duyurular.dat", "rw")) {
                    raf.seek(raf.length());
                    raf.writeUTF(hedef + ";" + baslik + " - " + mesaj);
                    JOptionPane.showMessageDialog(this, "Duyuru başarıyla yayınlandı!");
                    txtHedef.setText(""); txtBaslik.setText(""); txtMesaj.setText("");
                } catch (Exception ex) { }
            } else {
                // Mesaj olarak kaydet
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("mesajlar.txt", true))) {
                    bw.write(ogretmenAdi + ";" + hedef + ";" + core.OgrenciYonetici.sifrele(baslik) + ";" + core.OgrenciYonetici.sifrele(mesaj));
                    bw.newLine();
                    JOptionPane.showMessageDialog(this, "Mesaj başarıyla iletildi!");
                    txtHedef.setText(""); txtBaslik.setText(""); txtMesaj.setText("");
                } catch (Exception ex) { }
            }
        });
    }
}
