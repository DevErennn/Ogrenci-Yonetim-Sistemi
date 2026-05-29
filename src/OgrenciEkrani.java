import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.io.File;

public class OgrenciEkrani extends JFrame {
    private Ogrenci ogrenci;

    public OgrenciEkrani(Ogrenci girisYapanOgrenci) {
        this.ogrenci = girisYapanOgrenci;

        setTitle("Öğrenci Bilgi Sistemi - " + ogrenci.getAd());
        setSize(800, 550); // Yeni sekmeler rahat sığsın diye pencereyi biraz büyüttük
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Üst Panel ve Çıkış Butonu ---
        JPanel panelUstAna = new JPanel(new BorderLayout());
        panelUstAna.setBackground(new Color(240, 248, 255));
        panelUstAna.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBilgi = new JPanel(new GridLayout(2, 1));
        panelBilgi.setBackground(new Color(240, 248, 255));

        JLabel lblHosgeldin = new JLabel("Sayın " + ogrenci.getAd() + " (" + ogrenci.getId() + ")", SwingConstants.CENTER);
        lblHosgeldin.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblDurum = new JLabel("Bölüm: " + ogrenci.getBolum() + "   |   Genel Ortalama: " + ogrenci.getOrtalama(), SwingConstants.CENTER);
        lblDurum.setFont(new Font("Arial", Font.PLAIN, 14));

        panelBilgi.add(lblHosgeldin);
        panelBilgi.add(lblDurum);

        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(220, 20, 60)); 
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFont(new Font("Arial", Font.BOLD, 12));
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> {
            dispose(); 
            Main.main(new String[]{}); 
        });

        panelUstAna.add(panelBilgi, BorderLayout.CENTER);
        panelUstAna.add(btnCikis, BorderLayout.EAST);
        
        add(panelUstAna, BorderLayout.NORTH);

        // Mesaj sayısını hesapla (Gelen Kutusu kırmızı +1 için)
        int mesajSayisi = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                // Format: Gonderen;Alici;Baslik;Mesaj
                if (veri.length >= 4 && veri[1].equals(String.valueOf(ogrenci.getId()))) {
                    mesajSayisi++;
                }
            }
        } catch (Exception e) {}

        // --- SEKMELER ---
        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Profilim", profilPaneliniOlustur());
        sekmeler.addTab("Not Listesi", notListesiPaneliniOlustur());
        sekmeler.addTab("Ders Programı", dersProgramiPaneliniOlustur());
        sekmeler.addTab("Devamsızlık Durumu", devamsizlikPaneliniOlustur());
        
        // --- YENİ EKLENEN SEKMELER ---
        sekmeler.addTab("Duyurular", duyuruPaneliniOlustur()); 
        if (mesajSayisi > 0) {
            sekmeler.addTab("<html>Gelen Kutusu <font color='red'>(+" + mesajSayisi + ")</font></html>", gelenKutusuPaneliniOlustur());
        } else {
            sekmeler.addTab("Gelen Kutusu", gelenKutusuPaneliniOlustur()); 
        }
        sekmeler.addTab("Destek", destekPaneliniOlustur());
        
        add(sekmeler, BorderLayout.CENTER);
    }

    // --- YENİ: DUYURU PANELİ ---
    private JPanel duyuruPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> liste = new JList<>(model);
        liste.setFont(new Font("Arial", Font.PLAIN, 14));

        // --- .DAT DOSYASI VE RANDOMACCESSFILE (R) KULLANIMI ---
        try (RandomAccessFile raf = new RandomAccessFile("duyurular.dat", "r")) {
            while (raf.getFilePointer() < raf.length()) {
                String satir = raf.readUTF();
                String[] veri = satir.split(";");
                // Format: Kategori;Mesaj
                if (veri.length == 2) {
                    // Öğrenci sadece "Genel" duyuruları veya kendi bölümüne atılan duyuruları görür
                    if (veri[0].equals("Genel") || veri[0].equals(ogrenci.getBolum())) {
                        model.addElement("[" + veri[0] + "] " + veri[1]);
                    }
                }
            }
        } catch (Exception e) {}
        
        panel.add(new JScrollPane(liste), BorderLayout.CENTER);
        return panel;
    }

    // --- YENİ: GELEN KUTUSU PANELİ ---
    private JPanel gelenKutusuPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        String[] kolonlar = {"Gönderen", "Başlık", "Mesaj İçeriği"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25); // Mesajlar daha rahat okunsun diye satır yüksekliği arttırıldı

        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                // Format: Gonderen;Alici;Baslik;Mesaj
                if (veri.length >= 4 && veri[1].equals(String.valueOf(ogrenci.getId()))) {
                    model.addRow(new Object[]{veri[0], veri[2], veri[3]});
                }
            }
        } catch (Exception e) {}

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private String[] bolumeGoreDersleriGetir() {
        String bolum = ogrenci.getBolum();
        java.util.List<String> derslerListesi = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("dersler.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 2 && veri[0].trim().equals(bolum)) {
                    derslerListesi.add(veri[1].trim());
                }
            }
        } catch (Exception e) {}
        
        if (derslerListesi.isEmpty()) {
            return new String[]{"Henüz ders atanmamış"};
        }
        
        return derslerListesi.toArray(new String[0]);
    }

    private int dersinDevamsizliginiGetir(String dersAdi) {
        try (BufferedReader br = new BufferedReader(new FileReader("devamsizlik.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length == 3 && Integer.parseInt(veri[0]) == ogrenci.getId() && veri[1].equals(dersAdi)) {
                    return Integer.parseInt(veri[2]);
                }
            }
        } catch (Exception e) {}
        return 0; 
    }

    private JPanel notListesiPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Ders Adı", "Vize", "Final", "Harf Notu"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);

        // --- SCANNER İLE DOSYA OKUMA KULLANIMI ---
        try (Scanner scanner = new Scanner(new File("notlar.txt"))) {
            while (scanner.hasNextLine()) {
                String satir = scanner.nextLine();
                String[] veri = satir.split(";");
                
                if (veri.length == 4 && Integer.parseInt(veri[0]) == ogrenci.getId()) {
                    String dersAdi = veri[1];
                    int vizeNotu = Integer.parseInt(veri[2]);
                    int finalNotu = Integer.parseInt(veri[3]);
                    
                    int yapilanDevamsizlik = dersinDevamsizliginiGetir(dersAdi); 
                    
                    double dersOrtalamasi = (vizeNotu * 0.4) + (finalNotu * 0.6);
                    String harfNotu;

                    if (yapilanDevamsizlik >= 8) {
                        harfNotu = "DZ"; 
                    } else {
                        harfNotu = harfNotuHesapla(dersOrtalamasi);
                    }

                    Object[] tabloSatiri = {dersAdi, vizeNotu, finalNotu, harfNotu};
                    model.addRow(tabloSatiri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private String harfNotuHesapla(double ortalama) {
        if (ortalama >= 85) return "AA";
        else if (ortalama >= 75) return "BA";
        else if (ortalama >= 65) return "BB";
        else if (ortalama >= 55) return "CB";
        else if (ortalama >= 50) return "CC";
        else if (ortalama >= 40) return "DC";
        else return "FF";
    }

    private JPanel dersProgramiPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Gün", "Saat", "Ders Adı", "Derslik"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);

        String[] dersler = bolumeGoreDersleriGetir();
        String[] gunler = {"Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma"};
        String[] saatler = {"09:00 - 11:00", "11:00 - 13:00", "13:00 - 15:00", "15:00 - 17:00"};

        Random rastgele = new Random(ogrenci.getBolum().hashCode()); 

        for (int i = 0; i < dersler.length; i++) {
            String gun = gunler[rastgele.nextInt(gunler.length)];
            String saat = saatler[rastgele.nextInt(saatler.length)];
            String derslik = "Amfi " + (1 + rastgele.nextInt(5)); 
            model.addRow(new Object[]{gun, saat, dersler[i], derslik});
        }

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private JPanel devamsizlikPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] kolonlar = {"Ders Adı", "Yapılan Devamsızlık", "Kalan Hak", "Durum"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablo = new JTable(model);

        int devamsizlikSiniri = 8; 

        try (BufferedReader br = new BufferedReader(new FileReader("devamsizlik.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                
                if (veri.length == 3 && Integer.parseInt(veri[0]) == ogrenci.getId()) {
                    String dersAdi = veri[1];
                    int yapilanDevamsizlik = Integer.parseInt(veri[2]); 
                    
                    int kalanHak = devamsizlikSiniri - yapilanDevamsizlik;
                    if (kalanHak < 0) kalanHak = 0; 

                    String durum = "";
                    if (yapilanDevamsizlik >= 8) {
                        durum = "DEVAMSIZLIKTAN KALDI";
                    } else if (yapilanDevamsizlik >= 6) { 
                        durum = "RİSKLİ";
                    } else {
                        durum = "İYİ";
                    }

                    Object[] tabloSatiri = {dersAdi, yapilanDevamsizlik, kalanHak, durum};
                    model.addRow(tabloSatiri);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    // --- YENİ: PROFİL PANELİ ---
    private JPanel profilPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(10, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        panel.add(new JLabel("Okul Numarası:"));
        panel.add(new JLabel(String.valueOf(ogrenci.getId())));

        panel.add(new JLabel("İsim Soyisim:"));
        panel.add(new JLabel(ogrenci.getAd()));

        panel.add(new JLabel("Bölüm:"));
        panel.add(new JLabel(ogrenci.getBolum()));

        panel.add(new JLabel("Ortalama:"));
        panel.add(new JLabel(String.valueOf(ogrenci.getOrtalama())));

        panel.add(new JLabel("TC Kimlik No:"));
        JTextField txtTc = new JTextField(ogrenci.getTc());
        panel.add(txtTc);

        panel.add(new JLabel("E-Posta:"));
        JTextField txtEposta = new JTextField(ogrenci.getEposta());
        panel.add(txtEposta);

        panel.add(new JLabel("Telefon Numarası:"));
        JTextField txtTel = new JTextField(ogrenci.getTelefon());
        panel.add(txtTel);

        panel.add(new JLabel("Adres:"));
        JTextField txtAdres = new JTextField(ogrenci.getAdres());
        panel.add(txtAdres);

        panel.add(new JLabel(""));
        JButton btnKaydet = new JButton("Bilgileri Güncelle");
        btnKaydet.setBackground(new Color(60, 179, 113));
        btnKaydet.setForeground(Color.WHITE);
        panel.add(btnKaydet);

        btnKaydet.addActionListener(e -> {
            ogrenci.setTc(txtTc.getText().trim());
            ogrenci.setEposta(txtEposta.getText().trim());
            ogrenci.setTelefon(txtTel.getText().trim());
            ogrenci.setAdres(txtAdres.getText().trim());
            
            OgrenciYonetici yonetici = new OgrenciYonetici();
            for (Ogrenci o : yonetici.getOgrenciListesi()) {
                if (o.getId() == ogrenci.getId()) {
                    o.setTc(ogrenci.getTc());
                    o.setEposta(ogrenci.getEposta());
                    o.setTelefon(ogrenci.getTelefon());
                    o.setAdres(ogrenci.getAdres());
                    break;
                }
            }
            yonetici.dosyayaYaz();
            
            JOptionPane.showMessageDialog(this, "Bilgileriniz güncellendi!");
        });

        return panel;
    }

    // --- YENİ: DESTEK PANELİ ---
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

            try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("mesajlar.txt", true))) {
                bw.write(ogrenci.getId() + ";admin;" + baslik + ";" + mesaj);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Destek talebiniz admin'e iletildi!");
                txtBaslik.setText(""); 
                txtMesaj.setText("");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Mesaj gönderilirken hata oluştu!"); 
            }
        });

        return panel;
    }
}