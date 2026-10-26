package ortak;

import database.DatabaseHelper;
import javax.swing.*;
import java.awt.*;

public class DestekPaneli extends JPanel {
    public DestekPaneli(String gonderenBilgisi) {
        setLayout(new GridLayout(6, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Destek Talebi Oluştur (Admin'e Gönder)");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        add(new JLabel("Konu / Başlık:"));
        add(txtBaslik);
        add(new JLabel("Mesajınız:"));
        add(txtMesaj);
        add(new JLabel("")); 
        add(btnGonder);

        btnGonder.addActionListener(e -> {
            String baslik = txtBaslik.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (baslik.isEmpty() || mesaj.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            try {
                DatabaseHelper.mesajGonder(gonderenBilgisi, "admin", baslik, mesaj);
                JOptionPane.showMessageDialog(this, "Destek talebiniz admin'e iletildi!");
                txtBaslik.setText(""); 
                txtMesaj.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
    }
}
