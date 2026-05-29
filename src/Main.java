import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) {
        OgrenciYonetici yonetici = new OgrenciYonetici();

        // GÜNCELLENDİ: Bilgilendirme metni yeni formata uyarlandı
        String kullaniciAdi = JOptionPane.showInputDialog(null, 
            "Giriş Türleri:\n- Admin için 'admin'\n- Öğretmen için TC Kimlik No (örn: 11000000001)\n- Öğrenci için Öğrenci No (örn: 250260001)\n\nKullanıcı Adı / ID:", 
            "FU - OYS - Sisteme Giriş", JOptionPane.QUESTION_MESSAGE);
            
        // Kullanıcı iptale basarsa sistemi kapat
        if (kullaniciAdi == null) {
            System.exit(0); 
        }

        String sifre = JOptionPane.showInputDialog(null, "Şifre:", "Şifre", JOptionPane.QUESTION_MESSAGE);

        if (sifre == null) {
            System.exit(0); 
        }

        // Girişlerdeki boşlukları temizle
        kullaniciAdi = kullaniciAdi.trim();
        sifre = sifre.trim();

        // --- 1. ROL: ADMİN ---
        if ("admin".equals(kullaniciAdi) && "1234".equals(sifre)) {
            SwingUtilities.invokeLater(() -> new AnaEkran().setVisible(true));
            return;
        } 
        
        // --- 2. ROL: ÖĞRETMEN (Dosyadan Kontrol) ---
        boolean ogretmenBulundu = false;
        File ogretmenDosyasi = new File("ogretmenler.txt");
        
        // YENİ EKLENDİ: Dosya yoksa çökmek yerine otomatik oluştur
        if (!ogretmenDosyasi.exists()) {
            try {
                ogretmenDosyasi.createNewFile();
                JOptionPane.showMessageDialog(null, 
                    "ogretmenler.txt dosyası bulunamadı ve sistem tarafından otomatik oluşturuldu.\nDosya Yolu: " + ogretmenDosyasi.getAbsolutePath() + "\n\nLütfen öğretim görevlisi listesini bu dosyaya yapıştırın.", 
                    "Dosya Oluşturuldu", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                System.out.println("Dosya oluşturulamadı!");
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ogretmenDosyasi))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                satir = satir.replace("\uFEFF", "").trim();
                if (satir.isEmpty()) continue;
                String[] veri = satir.split(";");
                
                // Format: TC;Şifre;Ad;Ders
                if (veri.length >= 4 && veri[0].trim().equals(kullaniciAdi)) {
                    // Kullanıcıyı buldu, şimdi şifreyi kontrol et (yeni base64 veya eski plaintext)
                    if (veri[1].trim().equals(OgrenciYonetici.sifrele(sifre)) || veri[1].trim().equals(sifre)) {
                        String ad = veri[2].trim();
                        String ders = veri[3];
                        SwingUtilities.invokeLater(() -> new OgretmenEkrani(ad, ders).setVisible(true));
                        ogretmenBulundu = true;
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Öğretmen şifresi hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
                        return; // Şifre yanlışsa öğrenci kısmında aramasına gerek yok
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("HATA: ogretmenler.txt okunamadı!");
            e.printStackTrace(); 
        }

        if (ogretmenBulundu) return; 

        // --- 3. ROL: ÖĞRENCİ ---
        try {
            int girilenId = Integer.parseInt(kullaniciAdi);
            Ogrenci bulunanOgrenci = null;

            for (Ogrenci ogr : yonetici.getOgrenciListesi()) {
                if (ogr.getId() == girilenId) {
                    bulunanOgrenci = ogr;
                    break;
                }
            }

            if (bulunanOgrenci == null) {
                throw new OgrenciBulunamadiException("Bu numaraya ait öğrenci bulunamadı!");
            }

            // Şifreyi düz metin ile kontrol et (artık nesnelerde düz metin tutuluyor)
            if (bulunanOgrenci.getSifre().equals(sifre)) {
                final Ogrenci ogrGonder = bulunanOgrenci;
                SwingUtilities.invokeLater(() -> new OgrenciEkrani(ogrGonder).setVisible(true));
            } else {
                JOptionPane.showMessageDialog(null, "Öğrenci şifresi hatalı!", "Hata", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Lütfen geçerli bir numara veya admin girişi yapın!", "Hata", JOptionPane.ERROR_MESSAGE);
        } catch (OgrenciBulunamadiException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }
}