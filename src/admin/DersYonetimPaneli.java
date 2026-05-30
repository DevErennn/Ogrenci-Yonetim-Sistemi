package admin;

import model.*;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.RandomAccessFile;

public class DersYonetimPaneli extends JPanel {
    public DersYonetimPaneli() {
        setLayout(new GridLayout(1, 3, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. SÜTUN: ÖĞRENCİYE DERS ATA ---
        JPanel panelOgrenciDers = new JPanel(new GridLayout(6, 1, 10, 10));
        panelOgrenciDers.setBorder(BorderFactory.createTitledBorder("Öğrenciye Ders Ata"));
        
        JTextField txtOgrId = new JTextField();
        JTextField txtDersAdi = new JTextField();
        JButton btnOgrDersEkle = new JButton("Öğrenciye Ders Kaydı Aç");
        btnOgrDersEkle.setBackground(new Color(60, 179, 113)); 
        btnOgrDersEkle.setForeground(Color.WHITE);

        panelOgrenciDers.add(new JLabel("Öğrenci ID (örn: 250260001):"));
        panelOgrenciDers.add(txtOgrId);
        panelOgrenciDers.add(new JLabel("Ders Adı (Bölümüne Uygun):"));
        panelOgrenciDers.add(txtDersAdi);
        panelOgrenciDers.add(new JLabel("")); 
        panelOgrenciDers.add(btnOgrDersEkle);

        btnOgrDersEkle.addActionListener(e -> {
            String id = txtOgrId.getText().trim();
            String ders = txtDersAdi.getText().trim();
            if(id.isEmpty() || ders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("notlar.txt", true))) {
                bw.write(id + ";" + ders + ";0;0");
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Ders öğrenciye başarıyla atandı!");
                txtOgrId.setText(""); txtDersAdi.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dosyaya yazılamadı!");
            }
        });

        // --- 2. SÜTUN: ÖĞRETMEN EKLE ---
        JPanel panelOgretmenDers = new JPanel(new GridLayout(10, 1, 5, 5));
        panelOgretmenDers.setBorder(BorderFactory.createTitledBorder("Sisteme Yeni Öğretmen & Ders Ekle"));
        
        JTextField txtHocaKadi = new JTextField();
        JTextField txtHocaSifre = new JTextField("1234");
        JTextField txtHocaAd = new JTextField();
        JTextField txtHocaDers = new JTextField();
        JButton btnHocaEkle = new JButton("Öğretmen ve Dersi Kaydet");
        btnHocaEkle.setBackground(new Color(70, 130, 180)); 
        btnHocaEkle.setForeground(Color.WHITE);

        panelOgretmenDers.add(new JLabel("TC Kimlik No (11 Haneli):"));
        panelOgretmenDers.add(txtHocaKadi);
        panelOgretmenDers.add(new JLabel("Şifre:"));
        panelOgretmenDers.add(txtHocaSifre);
        panelOgretmenDers.add(new JLabel("Ad Soyad (örn: Prof. Dr. Ali):"));
        panelOgretmenDers.add(txtHocaAd);
        panelOgretmenDers.add(new JLabel("Verdiği Ders Adı:"));
        panelOgretmenDers.add(txtHocaDers);
        panelOgretmenDers.add(new JLabel("")); 
        panelOgretmenDers.add(btnHocaEkle);

        btnHocaEkle.addActionListener(e -> {
            String kadi = txtHocaKadi.getText().trim();
            String sifre = OgrenciYonetici.sifrele(txtHocaSifre.getText().trim());
            String ad = txtHocaAd.getText().trim();
            String ders = txtHocaDers.getText().trim();
            
            if(kadi.isEmpty() || sifre.isEmpty() || ad.isEmpty() || ders.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("ogretmenler.txt", true))) {
                bw.write(kadi + ";" + sifre + ";" + ad + ";" + ders);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Öğretmen ve Ders sisteme başarıyla eklendi!");
                txtHocaKadi.setText(""); txtHocaAd.setText(""); txtHocaDers.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dosyaya yazılamadı!");
            }
        });

        // --- 3. SÜTUN (YENİ): DUYURU YAYINLA ---
        JPanel panelDuyuru = new JPanel(new GridLayout(6, 1, 10, 10));
        panelDuyuru.setBorder(BorderFactory.createTitledBorder("Sistem Duyurusu Yayınla"));
        
        String[] kategoriler = {"Genel", "Bilgisayar Mühendisliği", "Yazılım Mühendisliği", "Makine Mühendisliği", "Elektrik Elektronik Mühendisliği", "Mekatronik Mühendisliği"};
        JComboBox<String> comboKategori = new JComboBox<>(kategoriler);
        JTextField txtDuyuruMetni = new JTextField();
        JButton btnDuyuruYayinla = new JButton("Duyuruyu Yayınla");
        btnDuyuruYayinla.setBackground(new Color(255, 140, 0)); 
        btnDuyuruYayinla.setForeground(Color.WHITE);

        panelDuyuru.add(new JLabel("Kimler Görecek?"));
        panelDuyuru.add(comboKategori);
        panelDuyuru.add(new JLabel("Duyuru İçeriği:"));
        panelDuyuru.add(txtDuyuruMetni);
        panelDuyuru.add(new JLabel("")); 
        panelDuyuru.add(btnDuyuruYayinla);

        btnDuyuruYayinla.addActionListener(e -> {
            String metin = txtDuyuruMetni.getText().trim();
            if(metin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Duyuru metni boş olamaz!");
                return;
            }
            try (RandomAccessFile raf = new RandomAccessFile("duyurular.dat", "rw")) {
                raf.seek(raf.length()); 
                String satir = comboKategori.getSelectedItem().toString() + ";" + metin;
                raf.writeUTF(satir);
                JOptionPane.showMessageDialog(this, "Duyuru öğrencilere başarıyla iletildi!");
                txtDuyuruMetni.setText("");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Hata oluştu!"); 
            }
        });

        add(panelOgrenciDers);
        add(panelOgretmenDers);
        add(panelDuyuru);
    }
}
