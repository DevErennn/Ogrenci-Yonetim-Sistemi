package ogretmen;

import core.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

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
                String id = txtDevamsizlikOgrenciId.getText().trim();
                String devamsizlik = txtDevamsizlikSaat.getText().trim();
                String seciliDers = (String) cmbDers.getSelectedItem();

                if (id.isEmpty() || devamsizlik.isEmpty() || seciliDers == null) {
                    JOptionPane.showMessageDialog(this, "Alanlar boş bırakılamaz!");
                    return;
                }

                devamsizlikKaydet(id, seciliDers, devamsizlik);
                JOptionPane.showMessageDialog(this, "Devamsızlık başarıyla kaydedildi!");
                txtDevamsizlikOgrenciId.setText(""); txtDevamsizlikSaat.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
    }

    private void devamsizlikKaydet(String id, String dersAdi, String saat) {
        File dosya = new File("devamsizlik.txt");
        ArrayList<String> satirlar = new ArrayList<>();
        boolean kayitBulundu = false;

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 3 && veri[0].equals(id) && veri[1].equalsIgnoreCase(dersAdi)) {
                    satirlar.add(id + ";" + dersAdi + ";" + saat);
                    kayitBulundu = true;
                } else {
                    satirlar.add(satir);
                }
            }
        } catch (IOException e) {}

        if (!kayitBulundu) satirlar.add(id + ";" + dersAdi + ";" + saat);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya))) {
            for (String s : satirlar) { bw.write(s); bw.newLine(); }
        } catch (IOException e) {}
    }
}
