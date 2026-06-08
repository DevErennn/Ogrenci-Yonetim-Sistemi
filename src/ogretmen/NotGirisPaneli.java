package ogretmen;

import core.*;
import model.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class NotGirisPaneli extends JPanel {
    private JTextField txtNotOgrenciId, txtVize, txtFinal;
    private JTextField txtOrtalamaHesap, txtHarfNotuHesap;
    private JComboBox<String> cmbDers;
    private Ogretmen ogretmen;
    private String donem;

    public NotGirisPaneli(Ogretmen ogretmen, String donem) {
        this.ogretmen = ogretmen;
        this.donem = donem;
        setLayout(new BorderLayout());

        JPanel formPaneli = new JPanel(new GridLayout(7, 2, 10, 10));
        formPaneli.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPaneli.add(new JLabel("Geçerli Dönem:"));
        JLabel lblDonem = new JLabel("<html><b><font color='blue'>" + donem + "</font></b></html>");
        formPaneli.add(lblDonem);

        formPaneli.add(new JLabel("Öğrenci ID:"));
        txtNotOgrenciId = new JTextField();
        formPaneli.add(txtNotOgrenciId);

        formPaneli.add(new JLabel("Ders Seçin:"));
        cmbDers = new JComboBox<>(ogretmen.getDerslerListesi().toArray(new String[0]));
        formPaneli.add(cmbDers);

        formPaneli.add(new JLabel("Vize Notu:"));
        txtVize = new JTextField();
        formPaneli.add(txtVize);

        formPaneli.add(new JLabel("Final Notu:"));
        txtFinal = new JTextField();
        formPaneli.add(txtFinal);

        formPaneli.add(new JLabel("Hesaplanan Ortalama:"));
        txtOrtalamaHesap = new JTextField();
        txtOrtalamaHesap.setEditable(false);
        formPaneli.add(txtOrtalamaHesap);

        formPaneli.add(new JLabel("Harf Notu:"));
        txtHarfNotuHesap = new JTextField();
        txtHarfNotuHesap.setEditable(false);
        formPaneli.add(txtHarfNotuHesap);

        DocumentListener hesaplaListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { hesapla(); }
            public void removeUpdate(DocumentEvent e) { hesapla(); }
            public void insertUpdate(DocumentEvent e) { hesapla(); }

            public void hesapla() {
                try {
                    int v = Integer.parseInt(txtVize.getText().trim());
                    int f = Integer.parseInt(txtFinal.getText().trim());
                    if (v < 0 || v > 100 || f < 0 || f > 100) return;
                    double ort = (v * 0.4) + (f * 0.6);
                    txtOrtalamaHesap.setText(String.format("%.2f", ort));

                    String harf = "FF";
                    if (ort >= 85) harf = "AA";
                    else if (ort >= 75) harf = "BA";
                    else if (ort >= 65) harf = "BB";
                    else if (ort >= 55) harf = "CB";
                    else if (ort >= 50) harf = "CC";
                    else if (ort >= 40) harf = "DC";
                    txtHarfNotuHesap.setText(harf);
                } catch (Exception ex) {
                    txtOrtalamaHesap.setText("");
                    txtHarfNotuHesap.setText("");
                }
            }
        };

        txtVize.getDocument().addDocumentListener(hesaplaListener);
        txtFinal.getDocument().addDocumentListener(hesaplaListener);

        JButton btnNotKaydet = new JButton("Notları Kaydet");
        btnNotKaydet.setBackground(new Color(60, 179, 113));
        btnNotKaydet.setForeground(Color.WHITE);

        JPanel altPanel = new JPanel();
        altPanel.add(btnNotKaydet);
        add(formPaneli, BorderLayout.CENTER);
        add(altPanel, BorderLayout.SOUTH);

        btnNotKaydet.addActionListener(e -> {
            try {
                String id = txtNotOgrenciId.getText().trim();
                String vize = txtVize.getText().trim();
                String fin = txtFinal.getText().trim();
                String seciliDers = (String) cmbDers.getSelectedItem();

                if (id.isEmpty() || vize.isEmpty() || fin.isEmpty() || seciliDers == null) {
                    JOptionPane.showMessageDialog(this, "Alanlar boş bırakılamaz!");
                    return;
                }

                notKaydet(id, seciliDers, vize, fin); 
                JOptionPane.showMessageDialog(this, "Notlar başarıyla kaydedildi!");
                txtNotOgrenciId.setText(""); txtVize.setText(""); txtFinal.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
    }

    private void notKaydet(String id, String dersAdi, String vize, String fin) {
        File dosya = new File("notlar.txt");
        ArrayList<String> satirlar = new ArrayList<>();
        boolean kayitBulundu = false;

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 4 && veri[0].equals(id) && veri[1].equalsIgnoreCase(dersAdi)) {
                    satirlar.add(id + ";" + dersAdi + ";" + vize + ";" + fin);
                    kayitBulundu = true;
                } else {
                    satirlar.add(satir);
                }
            }
        } catch (IOException e) {}

        if (!kayitBulundu) satirlar.add(id + ";" + dersAdi + ";" + vize + ";" + fin);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya))) {
            for (String s : satirlar) { bw.write(s); bw.newLine(); }
        } catch (IOException e) {}
    }
}
