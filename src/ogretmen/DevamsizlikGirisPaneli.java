package ogretmen;

import database.DatabaseHelper;
import model.*;
import javax.swing.*;
import java.awt.*;

public class DevamsizlikGirisPaneli extends JPanel {
    private JTextField txtDevamsizlikOgrenciId, txtDevamsizlikSaat;
    private JComboBox<String> cmbDers;
    private Ogretmen ogretmen;

    public DevamsizlikGirisPaneli(Ogretmen ogretmen) {
        this.ogretmen = ogretmen;
        setLayout(new BorderLayout());
        JPanel formPaneli = new JPanel(new GridLayout(3, 2, 10, 10));
        formPaneli.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPaneli.add(new JLabel("Öğrenci ID:"));
        txtDevamsizlikOgrenciId = new JTextField();
        formPaneli.add(txtDevamsizlikOgrenciId);

        formPaneli.add(new JLabel("Ders Seçin:"));
        cmbDers = new JComboBox<>(ogretmen.getDerslerListesi().toArray(new String[0]));
        formPaneli.add(cmbDers);

        formPaneli.add(new JLabel("Devamsızlık (Saat):"));
        txtDevamsizlikSaat = new JTextField();
        formPaneli.add(txtDevamsizlikSaat);

        JButton btnDevamsizlikKaydet = new JButton("Devamsızlık Kaydet");
        btnDevamsizlikKaydet.setBackground(new Color(220, 20, 60)); 
        btnDevamsizlikKaydet.setForeground(Color.WHITE);

        JPanel altPanel = new JPanel();
        altPanel.add(btnDevamsizlikKaydet);
        add(formPaneli, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);

        btnDevamsizlikKaydet.addActionListener(e -> {
            try {
                String idStr = txtDevamsizlikOgrenciId.getText().trim();
                String devamsizlikStr = txtDevamsizlikSaat.getText().trim();
                String seciliDers = (String) cmbDers.getSelectedItem();

                if (idStr.isEmpty() || devamsizlikStr.isEmpty() || seciliDers == null) {
                    JOptionPane.showMessageDialog(this, "Alanlar boş bırakılamaz!");
                    return;
                }

                int ogrId = Integer.parseInt(idStr);
                int saat = Integer.parseInt(devamsizlikStr);

                DatabaseHelper.devamsizlikKaydet(ogrId, seciliDers, saat);
                JOptionPane.showMessageDialog(this, "Devamsızlık başarıyla kaydedildi!");
                txtDevamsizlikOgrenciId.setText(""); 
                txtDevamsizlikSaat.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Öğrenci ID ve Devamsızlık saati sayı olmalıdır!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
    }
}
