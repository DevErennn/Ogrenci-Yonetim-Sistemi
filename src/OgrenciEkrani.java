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

        // --- SEKMELER ---
        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Not Listesi", notListesiPaneliniOlustur());
        sekmeler.addTab("Ders Programı", dersProgramiPaneliniOlustur());
        sekmeler.addTab("Devamsızlık Durumu", devamsizlikPaneliniOlustur());
        
        // --- YENİ EKLENEN SEKMELER ---
        sekmeler.addTab("Duyurular", duyuruPaneliniOlustur()); 
        sekmeler.addTab("Gelen Kutusu", gelenKutusuPaneliniOlustur()); 
        
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
        
        String[] kolonlar = {"Gönderen Öğretmen", "Mesaj İçeriği"};
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
        JTable tablo = new JTable(model);
        tablo.setRowHeight(25); // Mesajlar daha rahat okunsun diye satır yüksekliği arttırıldı

        try (BufferedReader br = new BufferedReader(new FileReader("mesajlar.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                // Format: HocaAd;AliciID;Mesaj
                if (veri.length == 3 && veri[1].equals(String.valueOf(ogrenci.getId()))) {
                    model.addRow(new Object[]{veri[0], veri[2]});
                }
            }
        } catch (Exception e) {}

        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }

    private String[] bolumeGoreDersleriGetir() {
        String bolum = ogrenci.getBolum();
        if (bolum.equals("Bilgisayar Mühendisliği") || bolum.equals("Yazılım Mühendisliği")) {
            return new String[]{"Programlama", "Calculus I", "Physics I", "Algoritmalar", "Bilgisayar Bilimleri Temelleri"};
        } else if (bolum.equals("Makine Mühendisliği") || bolum.equals("Mekatronik Mühendisliği")) {
            return new String[]{"Statik", "Dinamik", "Calculus I", "Physics I", "Termodinamik"};
        } else if (bolum.equals("Elektrik Elektronik Mühendisliği")) {
            return new String[]{"Devre Analizi", "Lojik Tasarım", "Calculus I", "Physics I", "Elektromanyetik Alanlar"};
        } else {
            return new String[]{"Matematik", "Fizik", "İngilizce", "Tarih", "Türk Dili"};
        }
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
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
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
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
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
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
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
}