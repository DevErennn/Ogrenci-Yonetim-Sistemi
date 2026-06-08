package admin;

import model.*;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class AdminMesajPaneli extends JPanel {
    public AdminMesajPaneli() {
        setLayout(new GridLayout(8, 1, 10, 5));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        String[] secenekler = {"Öğrenci (ID Giriniz)", "Öğretmen (TC Giriniz)"};
        JComboBox<String> cmbKime = new JComboBox<>(secenekler);

        JTextField txtHedef = new JTextField();
        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Mesajı Gönder");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        add(new JLabel("Alıcı Türü:"));
        add(cmbKime);
        add(new JLabel("Alıcı (Öğrenci ID / Öğretmen TC):"));
        add(txtHedef);
        add(new JLabel("Başlık:"));
        add(txtBaslik);
        add(new JLabel("Mesajınız:"));
        add(txtMesaj);
        add(btnGonder);

        btnGonder.addActionListener(e -> {
            String hedef = txtHedef.getText().trim();
            String baslik = txtBaslik.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (hedef.isEmpty() || mesaj.isEmpty() || baslik.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("mesajlar.txt", true))) {
                bw.write("Admin;" + hedef + ";" + core.OgrenciYonetici.sifrele(baslik) + ";" + core.OgrenciYonetici.sifrele(mesaj));
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Mesaj başarıyla iletildi!");
                txtHedef.setText(""); txtBaslik.setText(""); txtMesaj.setText("");
            } catch (Exception ex) { }
        });
    }
}
