import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*; 
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnaEkran extends JFrame {
    private OgrenciYonetici yonetici;
    
    // 1. Sekme Bileşenleri
    private JTable tablo;
    private DefaultTableModel tabloModeli;
    private JTextField txtId, txtSifre, txtAd, txtBolum, txtOrtalama;
    private JButton btnEkle, btnSil, btnGuncelle, btnSirala, btnAra;
    private JLabel lblToplamOgrenci, lblSinifOrtalamasi, lblEnBasarili;

    // 2. Sekme Bileşenleri 
    private JTable tabloBolumler;
    private DefaultTableModel tabloBolumlerModeli;
    private DaireGrafikPaneli grafikPaneli; 

    public AnaEkran() {
        yonetici = new OgrenciYonetici();

        setTitle("Öğrenci Yönetim Sistemi");
        setSize(1000, 650); // 3 sütun rahat sığsın diye genişliği 900'den 1000'e çıkardık
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // --- ÜST PANEL ---
        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(new Color(240, 248, 255));
        panelUst.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblAdmin = new JLabel("Sistem Yöneticisi Paneli (Admin)");
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnCikis = new JButton("Çıkış Yap / Girişe Dön");
        btnCikis.setBackground(new Color(220, 20, 60)); 
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFocusPainted(false);

        btnCikis.addActionListener(e -> {
            dispose(); 
            Main.main(new String[]{}); 
        });

        panelUst.add(lblAdmin, BorderLayout.CENTER);
        panelUst.add(btnCikis, BorderLayout.EAST);
        add(panelUst, BorderLayout.NORTH); 
        
        // Mesaj sayısını hesapla (Gelen Kutusu kırmızı +1 için)
        int mesajSayisi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[1].equals("admin")) {
                    mesajSayisi++;
                }
            }
        } catch (Exception e) {}

        // --- SEKMELİ YAPI ---
        JTabbedPane sekmeler = new JTabbedPane();

        JPanel panelAnaIslemler = anaIslemlerPaneliniOlustur();
        sekmeler.addTab("Öğrenci İşlemleri", panelAnaIslemler);

        JPanel panelIstatistikler = istatistikPaneliniOlustur();
        sekmeler.addTab("Bölüm İstatistikleri ve Grafik", panelIstatistikler);
        
        // GÜNCELLENDİ: Sekme adı değiştirildi
        JPanel panelDersYonetimi = dersYonetimPaneliniOlustur();
        sekmeler.addTab("Ders, Öğretmen ve Duyuru", panelDersYonetimi);
        
        sekmeler.addTab("Mesaj Gönder", adminMesajPaneliniOlustur());
        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", adminGelenKutusuPaneliniOlustur());
        } else {
            sekmeler.addTab("Gelen Kutusu", adminGelenKutusuPaneliniOlustur());
        }
        
        add(sekmeler, BorderLayout.CENTER);
        tablolariveGrafikGuncelle();

        // --- THREAD KULLANIMI: DİJİTAL SAAT ---
        JPanel panelAlt = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel lblSaat = new JLabel();
        lblSaat.setFont(new Font("Arial", Font.BOLD, 14));
        panelAlt.add(new JLabel("Sistem Saati: "));
        panelAlt.add(lblSaat);
        add(panelAlt, BorderLayout.SOUTH);

        Thread saatThread = new Thread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            while (true) {
                lblSaat.setText(sdf.format(new Date()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        saatThread.start();
    }

    private JPanel dersYonetimPaneliniOlustur() {
        // GÜNCELLENDİ: Sütun sayısı 2'den 3'e çıkarıldı
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
            // Yeni öğretmenin şifresini SHA-256 ile şifrele
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
        btnDuyuruYayinla.setBackground(new Color(255, 140, 0)); // Turuncu renk
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
            // --- .DAT DOSYASI VE RANDOMACCESSFILE (RW) KULLANIMI ---
            try (RandomAccessFile raf = new RandomAccessFile("duyurular.dat", "rw")) {
                raf.seek(raf.length()); // Dosyanın sonuna git (Append)
                String satir = comboKategori.getSelectedItem().toString() + ";" + metin;
                raf.writeUTF(satir);
                JOptionPane.showMessageDialog(this, "Duyuru öğrencilere başarıyla iletildi!");
                txtDuyuruMetni.setText("");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Hata oluştu!"); 
            }
        });

        // Üç sütunu da ana panele ekliyoruz
        panel.add(panelOgrenciDers);
        panel.add(panelOgretmenDers);
        panel.add(panelDuyuru);

        return panel;
    }

    private JPanel adminMesajPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        String[] secenekler = {"Öğrenci (ID Giriniz)", "Öğretmen (TC Giriniz)"};
        JComboBox<String> cmbKime = new JComboBox<>(secenekler);
        
        JTextField txtHedef = new JTextField();
        JTextField txtBaslik = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Mesajı Gönder");
        btnGonder.setBackground(new Color(100, 149, 237));
        btnGonder.setForeground(Color.WHITE);

        panel.add(new JLabel("Alıcı Türü:"));
        panel.add(cmbKime);
        panel.add(new JLabel("Alıcı (Öğrenci ID / Öğretmen TC):"));
        panel.add(txtHedef);
        panel.add(new JLabel("Başlık:"));
        panel.add(txtBaslik);
        panel.add(new JLabel("Mesajınız:"));
        panel.add(txtMesaj);
        panel.add(btnGonder);

        btnGonder.addActionListener(e -> {
            String hedef = txtHedef.getText().trim();
            String baslik = txtBaslik.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (hedef.isEmpty() || mesaj.isEmpty() || baslik.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Alanlar boş olamaz!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("mesajlar.txt", true))) {
                bw.write("Admin;" + hedef + ";" + baslik + ";" + mesaj);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Mesaj başarıyla iletildi!");
                txtHedef.setText(""); txtBaslik.setText(""); txtMesaj.setText("");
            } catch (Exception ex) { }
        });

        return panel;
    }

    private JPanel adminGelenKutusuPaneliniOlustur() {
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
                if (veri.length >= 4 && veri[1].equals("admin")) {
                    model.addRow(new Object[]{veri[0], veri[2], veri[3]});
                }
            }
        } catch (Exception e) {}

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel anaIslemlerPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());

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

        panel.add(panelGirdi, BorderLayout.NORTH);

        String[] kolonlar = {"ID", "Şifre", "Ad Soyad", "Bölüm", "Ortalama"};
        tabloModeli = new DefaultTableModel(kolonlar, 0);
        tablo = new JTable(tabloModeli);
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);

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
        panel.add(panelAltAna, BorderLayout.SOUTH);

        btnEkle.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String sifre = txtSifre.getText().trim();
                double ort = Double.parseDouble(txtOrtalama.getText());
                
                if(sifre.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Şifre boş olamaz!");
                    return;
                }

                yonetici.ogrenciEkle(new Ogrenci(id, txtSifre.getText().trim(), txtAd.getText(), txtBolum.getText(), ort));
                tablolariveGrafikGuncelle(); 
                alanlariTemizle();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lütfen verileri doğru formatta girin!");
            }
        });

        btnSil.addActionListener(e -> {
            int seciliSatir = tablo.getSelectedRow();
            if (seciliSatir >= 0) {
                int id = (int) tabloModeli.getValueAt(seciliSatir, 0);
                yonetici.ogrenciSil(id);
                tablolariveGrafikGuncelle();
            } else {
                JOptionPane.showMessageDialog(this, "Lütfen silmek için tablodan bir öğrenci seçin.");
            }
        });

        btnGuncelle.addActionListener(e -> {
            int seciliSatir = tablo.getSelectedRow();
            if (seciliSatir >= 0) {
                int id = (int) tabloModeli.getValueAt(seciliSatir, 0);
                String sifre = txtSifre.getText().trim(); 
                
                // Şifre artık düz metin olarak nesnede tutuluyor, dosyaya yazılırken Base64 ile şifreleniyor.
                // Bu nedenle sifrele() metodunu burada çağırmıyoruz.

                double ort = Double.parseDouble(txtOrtalama.getText());
                
                yonetici.ogrenciGuncelle(id, sifre, txtAd.getText(), txtBolum.getText(), ort);
                tablolariveGrafikGuncelle();
                alanlariTemizle();
            } else {
                JOptionPane.showMessageDialog(this, "Güncellemek için tablodan seçin.");
            }
        });

        btnSirala.addActionListener(e -> {
            yonetici.ortalamayaGoreSirala();
            tablolariveGrafikGuncelle();
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

        return panel;
    }

    private JPanel istatistikPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10)); 
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Bölüm Adı", "Öğrenci Sayısı", "Bölüm Ortalaması"};
        tabloBolumlerModeli = new DefaultTableModel(kolonlar, 0);
        tabloBolumler = new JTable(tabloBolumlerModeli);
        
        JPanel panelSol = new JPanel(new BorderLayout());
        panelSol.setBorder(BorderFactory.createTitledBorder("Bölüm Detayları"));
        panelSol.add(new JScrollPane(tabloBolumler), BorderLayout.CENTER);

        grafikPaneli = new DaireGrafikPaneli();
        grafikPaneli.setBorder(BorderFactory.createTitledBorder("Bölümlere Göre Öğrenci Dağılımı"));

        panel.add(panelSol);
        panel.add(grafikPaneli);

        return panel;
    }

    private void tablolariveGrafikGuncelle() {
        tabloModeli.setRowCount(0); 
        
        int toplamOgrenci = 0;
        double toplamNot = 0;
        double enYuksekNot = -1;
        String enBasariliIsim = "-";

        ArrayList<String> benzersizBolumler = new ArrayList<>();
        ArrayList<Integer> bolumKisiSayilari = new ArrayList<>();
        ArrayList<Double> bolumToplamNotlari = new ArrayList<>();

        for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
            Object[] satir = {ogr.getId(), ogr.getSifre(), ogr.getAd(), ogr.getBolum(), ogr.getOrtalama()};
            tabloModeli.addRow(satir);

            toplamOgrenci++;
            toplamNot += ogr.getOrtalama();
            if (ogr.getOrtalama() > enYuksekNot) {
                enYuksekNot = ogr.getOrtalama();
                enBasariliIsim = ogr.getAd() + " (" + enYuksekNot + ")";
            }

            String bolumAdi = ogr.getBolum();
            int index = benzersizBolumler.indexOf(bolumAdi);
            
            if (index == -1) {
                benzersizBolumler.add(bolumAdi);
                bolumKisiSayilari.add(1);
                bolumToplamNotlari.add(ogr.getOrtalama());
            } else {
                int mevcutKisi = bolumKisiSayilari.get(index);
                double mevcutNot = bolumToplamNotlari.get(index);
                bolumKisiSayilari.set(index, mevcutKisi + 1);
                bolumToplamNotlari.set(index, mevcutNot + ogr.getOrtalama());
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

        tabloBolumlerModeli.setRowCount(0);
        for (int i = 0; i < benzersizBolumler.size(); i++) {
            String bAd = benzersizBolumler.get(i);
            int kSayi = bolumKisiSayilari.get(i);
            double bOrt = bolumToplamNotlari.get(i) / kSayi; 
            
            Object[] bolumSatir = {bAd, kSayi, String.format("%.2f", bOrt)};
            tabloBolumlerModeli.addRow(bolumSatir);
        }

        grafikPaneli.verileriGuncelle(benzersizBolumler, bolumKisiSayilari, toplamOgrenci);
    }

    private void alanlariTemizle() {
        txtId.setText(""); txtSifre.setText(""); txtAd.setText(""); txtBolum.setText(""); txtOrtalama.setText("");
    }

    class DaireGrafikPaneli extends JPanel {
        private ArrayList<String> bolumler = new ArrayList<>();
        private ArrayList<Integer> kisiSayilari = new ArrayList<>();
        private int toplamKisi = 0;
        
        private Color[] renkler = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK};

        public void verileriGuncelle(ArrayList<String> b, ArrayList<Integer> k, int toplam) {
            this.bolumler = b;
            this.kisiSayilari = k;
            this.toplamKisi = toplam;
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (toplamKisi == 0) {
                g2d.drawString("Grafik için henüz veri yok.", 100, 150);
                return;
            }

            int baslangicAcisi = 0;
            int x = 50;  
            int y = 50;  
            int cap = 200; 
            
            int yaziY = 300; 

            for (int i = 0; i < bolumler.size(); i++) {
                int sayi = kisiSayilari.get(i);
                int aci = (int) Math.round((sayi / (double) toplamKisi) * 360);
                
                Color renk = renkler[i % renkler.length];
                g2d.setColor(renk);
                
                g2d.fillArc(x, y, cap, cap, baslangicAcisi, aci);
                
                g2d.fillRect(20, yaziY, 15, 15);
                g2d.setColor(Color.BLACK);
                g2d.drawString(bolumler.get(i) + " (" + sayi + " Öğrenci)", 45, yaziY + 12);
                
                yaziY += 25; 
                baslangicAcisi += aci; 
            }
        }
    }
}