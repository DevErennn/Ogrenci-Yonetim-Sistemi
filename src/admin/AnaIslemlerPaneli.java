package admin;

import model.*;
import core.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AnaIslemlerPaneli extends JPanel {
    private OgrenciYonetici yonetici;
    private JTable tablo;
    private DefaultTableModel tabloModeli;
    private JTextField txtId, txtSifre, txtAd, txtBolum, txtOrtalama;
    private JButton btnEkle, btnSil, btnGuncelle, btnSirala, btnAra;
    private JLabel lblToplamOgrenci, lblSinifOrtalamasi, lblEnBasarili;
    private DataUpdateListener updateListener;

    public AnaIslemlerPaneli(OgrenciYonetici yonetici, DataUpdateListener updateListener) {
        this.yonetici = yonetici;
        this.updateListener = updateListener;
        
        setLayout(new BorderLayout());

        JPanel panelGirdi = new JPanel(new GridLayout(3, 4, 10, 10));
        panelGirdi.setBorder(BorderFactory.createTitledBorder("Öğrenci Bilgileri"));

        txtId = new JTextField();
        txtSifre = new JTextField(); 
        txtAd = new JTextField();
        txtBolum = new JTextField();
        txtOrtalama = new JTextField();

        panelGirdi.add(new JLabel("Öğrenci ID (25026..):"));
        panelGirdi.add(txtId);
        panelGirdi.add(new JLabel("Giriş Şifresi:"));
        panelGirdi.add(txtSifre); 
        panelGirdi.add(new JLabel("Ad Soyad:"));
        panelGirdi.add(txtAd);
        panelGirdi.add(new JLabel("Bölüm:"));
        panelGirdi.add(txtBolum);
        panelGirdi.add(new JLabel("Ortalama:"));
        panelGirdi.add(txtOrtalama);

        add(panelGirdi, BorderLayout.NORTH);

        String[] kolonlar = {"ID", "Şifre", "Ad Soyad", "Bölüm", "Ortalama"};
        tabloModeli = new DefaultTableModel(kolonlar, 0);
        tablo = new JTable(tabloModeli);
        add(new JScrollPane(tablo), BorderLayout.CENTER);

        JPanel panelAltAna = new JPanel(new BorderLayout());

        JPanel panelButonlar = new JPanel();
        btnEkle = new JButton("Ekle");
        btnSil = new JButton("Sil");
        btnGuncelle = new JButton("Güncelle");
        btnSirala = new JButton("Ortalamaya Göre Sırala");
        btnAra = new JButton("Ara");

        panelButonlar.add(btnEkle);
        panelButonlar.add(btnSil);
        panelButonlar.add(btnGuncelle);
        panelButonlar.add(btnSirala);
        panelButonlar.add(btnAra);
        panelAltAna.add(panelButonlar, BorderLayout.NORTH);

        JPanel panelGenelIstatistik = new JPanel(new GridLayout(1, 3, 10, 10));
        panelGenelIstatistik.setBorder(BorderFactory.createTitledBorder("Genel Durum"));
        panelGenelIstatistik.setBackground(new Color(240, 248, 255)); 

        lblToplamOgrenci = new JLabel("Toplam: 0");
        lblSinifOrtalamasi = new JLabel("Ortalama: 0.0");
        lblEnBasarili = new JLabel("En Başarılı: -");

        Font font = new Font("Arial", Font.BOLD, 12);
        lblToplamOgrenci.setFont(font);
        lblSinifOrtalamasi.setFont(font);
        lblEnBasarili.setFont(font);

        panelGenelIstatistik.add(lblToplamOgrenci);
        panelGenelIstatistik.add(lblSinifOrtalamasi);
        panelGenelIstatistik.add(lblEnBasarili);
        
        panelAltAna.add(panelGenelIstatistik, BorderLayout.SOUTH);
        add(panelAltAna, BorderLayout.SOUTH);

        butonOlaylariniTanimla();
    }

    private void butonOlaylariniTanimla() {
        btnEkle.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String sifre = txtSifre.getText().trim();
                double ort = Double.parseDouble(txtOrtalama.getText());
                
                if(sifre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Şifre boş olamaz!");
                    return;
                }

                yonetici.ogrenciEkle(new Ogrenci(id, sifre, txtAd.getText(), txtBolum.getText(), ort));
                verileriGuncelle(); 
                alanlariTemizle();
                if (updateListener != null) updateListener.onDataUpdated();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lütfen verileri doğru formatta girin!");
            }
        });

        btnSil.addActionListener(e -> {
            int seciliSatir = tablo.getSelectedRow();
            if (seciliSatir >= 0) {
                int id = (int) tabloModeli.getValueAt(seciliSatir, 0);
                yonetici.ogrenciSil(id);
                verileriGuncelle();
                if (updateListener != null) updateListener.onDataUpdated();
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silmek için tablodan bir öğrenci seçin.");
            }
        });

        btnGuncelle.addActionListener(e -> {
            int seciliSatir = tablo.getSelectedRow();
            if (seciliSatir >= 0) {
                int id = (int) tabloModeli.getValueAt(seciliSatir, 0);
                String sifre = txtSifre.getText().trim(); 
                double ort = Double.parseDouble(txtOrtalama.getText());
                
                yonetici.ogrenciGuncelle(id, sifre, txtAd.getText(), txtBolum.getText(), ort);
                verileriGuncelle();
                alanlariTemizle();
                if (updateListener != null) updateListener.onDataUpdated();
            } else {
                JOptionPane.showMessageDialog(this, "Güncellemek için tablodan seçin.");
            }
        });

        btnSirala.addActionListener(e -> {
            yonetici.ortalamayaGoreSirala();
            verileriGuncelle();
        });

        btnAra.addActionListener(e -> {
            String aranan = JOptionPane.showInputDialog(this, "Aranacak Öğrenci ID veya Ad Soyad:", "Öğrenci Ara", JOptionPane.QUESTION_MESSAGE);
            if (aranan == null || aranan.trim().isEmpty()) return;
            
            aranan = aranan.trim().toLowerCase();
            boolean bulundu = false;
            
            for (int i = 0; i < tabloModeli.getRowCount(); i++) {
                String idStr = tabloModeli.getValueAt(i, 0).toString();
                String adStr = tabloModeli.getValueAt(i, 2).toString().toLowerCase();
                
                if (idStr.equals(aranan) || adStr.contains(aranan)) {
                    tablo.setRowSelectionInterval(i, i);
                    tablo.scrollRectToVisible(tablo.getCellRect(i, 0, true));
                    bulundu = true;
                    break;
                }
            }
            
            if (!bulundu) {
                JOptionPane.showMessageDialog(this, "Öğrenci bulunamadı!", "Bulunamadı", JOptionPane.WARNING_MESSAGE);
            }
        });

        tablo.getSelectionModel().addListSelectionListener(e -> {
            int seciliSatir = tablo.getSelectedRow();
            if (seciliSatir >= 0) {
                txtId.setText(tabloModeli.getValueAt(seciliSatir, 0).toString());
                txtSifre.setText(tabloModeli.getValueAt(seciliSatir, 1).toString()); 
                txtAd.setText(tabloModeli.getValueAt(seciliSatir, 2).toString());
                txtBolum.setText(tabloModeli.getValueAt(seciliSatir, 3).toString());
                txtOrtalama.setText(tabloModeli.getValueAt(seciliSatir, 4).toString());
            }
        });
    }

    public void verileriGuncelle() {
        tabloModeli.setRowCount(0); 
        
        int toplamOgrenci = 0;
        double toplamNot = 0;
        double enYuksekNot = -1;
        String enBasariliIsim = "-";

        for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
            Object[] satir = {ogr.getId(), ogr.getSifre(), ogr.getAd(), ogr.getBolum(), ogr.getOrtalama()};
            tabloModeli.addRow(satir);

            toplamOgrenci++;
            toplamNot += ogr.getOrtalama();
            if (ogr.getOrtalama() > enYuksekNot) {
                enYuksekNot = ogr.getOrtalama();
                enBasariliIsim = ogr.getAd() + " (" + enYuksekNot + ")";
            }
        }

        lblToplamOgrenci.setText("Toplam Öğrenci: " + toplamOgrenci);
        if (toplamOgrenci > 0) {
            lblSinifOrtalamasi.setText(String.format("Genel Ortalama: %.2f", (toplamNot / toplamOgrenci)));
            lblEnBasarili.setText("En Başarılı: " + enBasariliIsim);
        } else {
            lblSinifOrtalamasi.setText("Genel Ortalama: 0.0");
            lblEnBasarili.setText("En Başarılı: -");
        }
    }

    private void alanlariTemizle() {
        txtId.setText(""); txtSifre.setText(""); txtAd.setText(""); txtBolum.setText(""); txtOrtalama.setText("");
    }
}
