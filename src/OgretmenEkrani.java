import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.time.LocalDate;

public class OgretmenEkrani extends JFrame {
    private String ogretmenAdi;
    private String verilenDers;
    private String tcKimlik; // Needed for inbox id

    private JTextField txtNotOgrenciId, txtVize, txtFinal;
    private JTextField txtDevamsizlikOgrenciId, txtDevamsizlikSaat;
    private JTextField txtOrtalamaHesap, txtHarfNotuHesap;

    public OgretmenEkrani(String ad, String ders) {
        this.ogretmenAdi = ad;
        this.verilenDers = ders;
        this.tcKimlik = bulOgretmenTc(ad);

        setTitle("Öğretim Elemanı Paneli - " + ogretmenAdi + " (" + verilenDers + ")");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(new Color(240, 248, 255));
        panelUst.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBilgi = new JLabel("Sayın " + ogretmenAdi + "   |   Verilen Ders: " + verilenDers);
        lblBilgi.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(220, 20, 60));
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFocusPainted(false);
        
        btnCikis.addActionListener(e -> {
            dispose();
            Main.main(new String[]{});
        });

        panelUst.add(lblBilgi, BorderLayout.CENTER);
        panelUst.add(btnCikis, BorderLayout.EAST);
        add(panelUst, BorderLayout.NORTH);

        int mesajSayisi = hesaplaMesajSayisi();

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Not Girişi", notGirisPaneliniOlustur());
        sekmeler.addTab("Devamsızlık Girişi", devamsizlikGirisPaneliniOlustur());
        sekmeler.addTab("Ders Programım", dersProgramiPaneliniOlustur());
        sekmeler.addTab("Mesaj Gönder", mesajGirisPaneliniOlustur());
        
        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", gelenKutusuPaneliniOlustur());
        } else {
            sekmeler.addTab("Gelen Kutusu", gelenKutusuPaneliniOlustur()); 
        }
        sekmeler.addTab("Destek", destekPaneliniOlustur());

        add(sekmeler, BorderLayout.CENTER);
    }

    private String bulOgretmenTc(String ad) {
        try (BufferedReader br = new BufferedReader(new FileReader("ogretmenler.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[2].equals(ad)) {
                    return veri[0];
                }
            }
        } catch (Exception e) {}
        return "tanimsiz";
    }

    private int hesaplaMesajSayisi() {
        int sayi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals(tcKimlik)) {
                    sayi++;
                }
            }
        } catch (Exception e) {}
        return sayi;
    }

    private String donemBelirle() {
        LocalDate tarih = LocalDate.now();
        int ay = tarih.getMonthValue();
        int yil = tarih.getYear();
        
        // Türkiye Akademik Takvimi: Eylül(9) - Ocak(1) Güz, Şubat(2) - Haziran(6) Bahar, Temmuz(7)-Ağustos(8) Yaz
        if (ay >= 9 || ay == 1) {
            int egitimYili = ay >= 9 ? yil : (yil - 1);
            return egitimYili + "-" + (egitimYili + 1) + " Güz Dönemi";
        } else if (ay >= 2 && ay <= 6) {
            return (yil - 1) + "-" + yil + " Bahar Dönemi";
        } else {
            return (yil - 1) + "-" + yil + " Yaz Dönemi";
        }
    }

    private JPanel notGirisPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPaneli = new JPanel(new GridLayout(7, 2, 10, 10));
        formPaneli.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPaneli.add(new JLabel("Geçerli Dönem:"));
        JLabel lblDonem = new JLabel("<html><b><font color='blue'>" + donemBelirle() + "</font></b></html>");
        formPaneli.add(lblDonem);

        formPaneli.add(new JLabel("Öğrenci ID:"));
        txtNotOgrenciId = new JTextField();
        formPaneli.add(txtNotOgrenciId);

        formPaneli.add(new JLabel("Ders Adı:"));
        JTextField txtDers = new JTextField(verilenDers);
        txtDers.setEditable(false); 
        formPaneli.add(txtDers);

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
        panel.add(formPaneli, BorderLayout.CENTER);
        panel.add(altPanel, BorderLayout.SOUTH);

        btnNotKaydet.addActionListener(e -> {
            try {
                String id = txtNotOgrenciId.getText().trim();
                String vize = txtVize.getText().trim();
                String fin = txtFinal.getText().trim();

                if (id.isEmpty() || vize.isEmpty() || fin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Alanlar boş bırakılamaz!");
                    return;
                }

                notKaydet(id, vize, fin); 
                JOptionPane.showMessageDialog(this, "Notlar başarıyla kaydedildi!");
                txtNotOgrenciId.setText(""); txtVize.setText(""); txtFinal.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
        return panel;
    }

    private JPanel devamsizlikGirisPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPaneli = new JPanel(new GridLayout(3, 2, 10, 10));
        formPaneli.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPaneli.add(new JLabel("Öğrenci ID:"));
        txtDevamsizlikOgrenciId = new JTextField();
        formPaneli.add(txtDevamsizlikOgrenciId);

        formPaneli.add(new JLabel("Ders Adı:"));
        JTextField txtDers = new JTextField(verilenDers);
        txtDers.setEditable(false); 
        formPaneli.add(txtDers);

        formPaneli.add(new JLabel("Devamsızlık (Saat):"));
        txtDevamsizlikSaat = new JTextField();
        formPaneli.add(txtDevamsizlikSaat);

        JButton btnDevamsizlikKaydet = new JButton("Devamsızlık Kaydet");
        btnDevamsizlikKaydet.setBackground(new Color(220, 20, 60)); 
        btnDevamsizlikKaydet.setForeground(Color.WHITE);
        
        JPanel altPanel = new JPanel();
        altPanel.add(btnDevamsizlikKaydet);
        panel.add(formPaneli, BorderLayout.CENTER);
        panel.add(altPanel, BorderLayout.SOUTH);

        btnDevamsizlikKaydet.addActionListener(e -> {
            try {
                String id = txtDevamsizlikOgrenciId.getText().trim();
                String devamsizlik = txtDevamsizlikSaat.getText().trim();

                if (id.isEmpty() || devamsizlik.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Alanlar boş bırakılamaz!");
                    return;
                }

                devamsizlikKaydet(id, devamsizlik);
                JOptionPane.showMessageDialog(this, "Devamsızlık başarıyla kaydedildi!");
                txtDevamsizlikOgrenciId.setText(""); txtDevamsizlikSaat.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata oluştu: " + ex.getMessage());
            }
        });
        return panel;
    }

    private void notKaydet(String id, String vize, String fin) {
        File dosya = new File("notlar.txt");
        ArrayList<String> satirlar = new ArrayList<>();
        boolean kayitBulundu = false;

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 4 && veri[0].equals(id) && veri[1].equalsIgnoreCase(verilenDers)) {
                    satirlar.add(id + ";" + verilenDers + ";" + vize + ";" + fin);
                    kayitBulundu = true;
                } else {
                    satirlar.add(satir);
                }
            }
        } catch (IOException e) {}

        if (!kayitBulundu) satirlar.add(id + ";" + verilenDers + ";" + vize + ";" + fin);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya))) {
            for (String s : satirlar) { bw.write(s); bw.newLine(); }
        } catch (IOException e) {}
    }

    private void devamsizlikKaydet(String id, String saat) {
        File dosya = new File("devamsizlik.txt");
        ArrayList<String> satirlar = new ArrayList<>();
        boolean kayitBulundu = false;

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 3 && veri[0].equals(id) && veri[1].equalsIgnoreCase(verilenDers)) {
                    satirlar.add(id + ";" + verilenDers + ";" + saat);
                    kayitBulundu = true;
                } else {
                    satirlar.add(satir);
                }
            }
        } catch (IOException e) {}

        if (!kayitBulundu) satirlar.add(id + ";" + verilenDers + ";" + saat);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosya))) {
            for (String s : satirlar) { bw.write(s); bw.newLine(); }
        } catch (IOException e) {}
    }

    private JPanel dersProgramiPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] kolonlar = {"Gün", "Saat", "Verilen Ders", "Derslik"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        model.addRow(new Object[]{"Pazartesi", "09:00 - 12:00", verilenDers, "Amfi 1"});
        model.addRow(new Object[]{"Çarşamba", "13:00 - 15:00", verilenDers, "Amfi 3"});
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel mesajGirisPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        String[] secenekler = {"Öğrenci (ID Giriniz)", "Öğretmen (TC Giriniz)", "Bölüme Duyuru (Bölüm Adı Seçiniz)"};
        JComboBox<String> cmbKime = new JComboBox<>(secenekler);
        
        JTextField txtHedef = new JTextField();
        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Mesajı / Duyuruyu Gönder");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        panel.add(new JLabel("Alıcı Türü:"));
        panel.add(cmbKime);
        panel.add(new JLabel("Alıcı (Öğrenci ID / Öğretmen TC / Bölüm Adı):"));
        panel.add(txtHedef);
        panel.add(new JLabel("Başlık:"));
        panel.add(txtBaslik);
        panel.add(new JLabel("Mesajınız:"));
        panel.add(txtMesaj);
        panel.add(btnGonder);

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
                    bw.write(ogretmenAdi + ";" + hedef + ";" + baslik + ";" + mesaj);
                    bw.newLine();
                    JOptionPane.showMessageDialog(this, "Mesaj başarıyla iletildi!");
                    txtHedef.setText(""); txtBaslik.setText(""); txtMesaj.setText("");
                } catch (Exception ex) { }
            }
        });

        return panel;
    }

    private JPanel gelenKutusuPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] kolonlar = {"Gönderen", "Başlık", "Mesaj İçeriği"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25);

        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals(tcKimlik)) {
                    model.addRow(new Object[]{veri[0], veri[2], veri[3]});
                }
            }
        } catch (Exception e) {}

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel destekPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Destek Talebi Oluştur (Admin'e Gönder)");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        panel.add(new JLabel("Konu / Başlık:"));
        panel.add(txtBaslik);
        panel.add(new JLabel("Mesajınız:"));
        panel.add(txtMesaj);
        panel.add(new JLabel("")); 
        panel.add(btnGonder);

        btnGonder.addActionListener(e -> {
            String baslik = txtBaslik.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (baslik.isEmpty() || mesaj.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("mesajlar.txt", true))) {
                bw.write(ogretmenAdi + ";admin;" + baslik + ";" + mesaj);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Destek talebiniz admin'e iletildi!");
                txtBaslik.setText(""); txtMesaj.setText("");
            } catch (Exception ex) { }
        });

        return panel;
    }
}