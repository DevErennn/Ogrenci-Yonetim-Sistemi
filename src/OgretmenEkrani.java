import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class OgretmenEkrani extends JFrame {
    private String ogretmenAdi;
    private String verilenDers;

    private JTextField txtNotOgrenciId, txtVize, txtFinal;
    private JTextField txtDevamsizlikOgrenciId, txtDevamsizlikSaat;

    public OgretmenEkrani(String ad, String ders) {
        this.ogretmenAdi = ad;
        this.verilenDers = ders;

        setTitle("Öğretim Elemanı Paneli - " + ogretmenAdi + " (" + verilenDers + ")");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- YENİ EKLENEN ÜST PANEL VE ÇIKIŞ BUTONU ---
        JPanel panelUst = new JPanel(new BorderLayout());
        panelUst.setBackground(new Color(240, 248, 255));
        panelUst.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBilgi = new JLabel("Sayın " + ogretmenAdi + "   |   Verilen Ders: " + verilenDers);
        lblBilgi.setFont(new Font("Arial", Font.BOLD, 16));

        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(220, 20, 60)); // Kırmızımsı
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFocusPainted(false);
        
        btnCikis.addActionListener(e -> {
            dispose(); // Mevcut ekranı kapat
            Main.main(new String[]{}); // Sistemi baştan başlat
        });

        panelUst.add(lblBilgi, BorderLayout.CENTER);
        panelUst.add(btnCikis, BorderLayout.EAST);
        add(panelUst, BorderLayout.NORTH);
        // ----------------------------------------------

        JTabbedPane sekmeler = new JTabbedPane();
        sekmeler.addTab("Not Girişi", notGirisPaneliniOlustur());
        sekmeler.addTab("Devamsızlık Girişi", devamsizlikGirisPaneliniOlustur());
        sekmeler.addTab("Ders Programım", dersProgramiPaneliniOlustur());
        
        // --- YENİ EKLENEN SEKME: ÖĞRENCİYE MESAJ GÖNDER ---
        sekmeler.addTab("Mesaj Gönder", mesajGirisPaneliniOlustur());

        add(sekmeler, BorderLayout.CENTER);
    }

    // --- YENİ: MESAJ GÖNDERME PANELİ ---
    private JPanel mesajGirisPaneliniOlustur() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JTextField txtHedefOgrId = new JTextField();
        JTextField txtMesaj = new JTextField();
        JButton btnGonder = new JButton("Mesajı Gönder");
        btnGonder.setBackground(new Color(100, 149, 237)); // Mavi
        btnGonder.setForeground(Color.WHITE);

        panel.add(new JLabel("Öğrenci Numarası (örn: 250260001):"));
        panel.add(txtHedefOgrId);
        panel.add(new JLabel("Mesajınız:"));
        panel.add(txtMesaj);
        panel.add(new JLabel("")); // Tasarım için boşluk
        panel.add(btnGonder);

        btnGonder.addActionListener(e -> {
            String ogrId = txtHedefOgrId.getText().trim();
            String mesaj = txtMesaj.getText().trim();

            if (ogrId.isEmpty() || mesaj.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Öğrenci numarası veya mesaj boş olamaz!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("mesajlar.txt", true))) {
                // Format: GonderenHocaAd;AliciID;Mesaj
                bw.write(ogretmenAdi + ";" + ogrId + ";" + mesaj);
                bw.newLine();
                JOptionPane.showMessageDialog(this, "Mesajınız öğrenciye başarıyla iletildi!");
                txtHedefOgrId.setText(""); 
                txtMesaj.setText("");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Mesaj gönderilirken hata oluştu!"); 
            }
        });

        return panel;
    }

    // --- SEKME 1: NOT GİRİŞİ (Sadece notlar.txt) ---
    private JPanel notGirisPaneliniOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel formPaneli = new JPanel(new GridLayout(4, 2, 10, 10));
        formPaneli.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

    // --- SEKME 2: DEVAMSIZLIK GİRİŞİ (Sadece devamsizlik.txt) ---
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
        DefaultTableModel model = new DefaultTableModel(kolonlar, 0);
        JTable tablo = new JTable(model);
        model.addRow(new Object[]{"Pazartesi", "09:00 - 12:00", verilenDers, "Amfi 1"});
        model.addRow(new Object[]{"Çarşamba", "13:00 - 15:00", verilenDers, "Amfi 3"});
        panel.add(new JScrollPane(tablo), BorderLayout.CENTER);
        return panel;
    }
}