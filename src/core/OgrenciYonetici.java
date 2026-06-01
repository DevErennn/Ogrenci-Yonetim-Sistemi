package core;

import model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class OgrenciYonetici {

    private ArrayList<Ogrenci> ogrenciListesi;
    private final String dosyaAdi = "ogrenciler.txt";

    public OgrenciYonetici() {
        ogrenciListesi = new ArrayList<>();
        dosyadanOku();
    }

    public void dosyadanOku() {
        ogrenciListesi.clear();
        File dosya = new File(dosyaAdi);
        if (!dosya.exists()) return;

        boolean kurulumGerekli = false;
        ArrayList<String[]> okunanSatirlar = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                satir = satir.replace("\uFEFF", "").trim(); 
                if (satir.isEmpty()) continue;

                String[] veri = satir.split(";");
                if (veri.length >= 5) {
                    okunanSatirlar.add(veri);
                    if (veri.length < 10) {
                        kurulumGerekli = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (kurulumGerekli) {
            veriTabaniniSifirlaVeRastgeleYarat(okunanSatirlar);
        } else {
            for (String[] veri : okunanSatirlar) {
                try {
                    int id = Integer.parseInt(veri[0].trim());
                    String sifre = sifreCoz(veri[1].trim());
                    String ad = veri[2].trim();
                    String bolum = veri[3].trim();
                    double ort = Double.parseDouble(veri[4].trim());
                    String eposta = veri.length > 5 ? veri[5].trim() : "tanimsiz";
                    String telefon = veri.length > 6 ? veri[6].trim() : "tanimsiz";
                    String adres = veri.length > 7 ? veri[7].trim() : "tanimsiz";
                    String tc = veri.length > 8 ? veri[8].trim() : "tanimsiz";
                    int sinif = veri.length > 9 ? Integer.parseInt(veri[9].trim()) : 1;

                    ogrenciListesi.add(new Ogrenci(id, sifre, ad, bolum, sinif, ort, eposta, telefon, adres, tc));
                } catch (Exception e) {}
            }
        }
    }
    
    private void veriTabaniniSifirlaVeRastgeleYarat(ArrayList<String[]> okunanSatirlar) {
        java.util.Random rand = new java.util.Random();
        
        // 1. Öğrencileri sınıf bilgisiyle yükle (1-4 arası rastgele)
        for (String[] veri : okunanSatirlar) {
            try {
                int id = Integer.parseInt(veri[0].trim());
                String sifre = sifreCoz(veri[1].trim());
                String ad = veri[2].trim();
                String bolum = veri[3].trim();
                
                // Rastgele sınıf ata (1-4)
                int sinif = 1 + rand.nextInt(4);
                
                String eposta = veri.length > 5 ? veri[5].trim() : "tanimsiz";
                String telefon = veri.length > 6 ? veri[6].trim() : "tanimsiz";
                String adres = veri.length > 7 ? veri[7].trim() : "tanimsiz";
                String tc = veri.length > 8 ? veri[8].trim() : "tanimsiz";
                
                // Geçici olarak ortalama 0.0 atıyoruz, ders notlarına göre hesaplayacağız
                ogrenciListesi.add(new Ogrenci(id, sifre, ad, bolum, sinif, 0.0, eposta, telefon, adres, tc));
            } catch (Exception e) {}
        }
        
        // 2. Dersler.txt'yi oku ve hafızada tut
        java.util.List<String[]> dersler = new java.util.ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("dersler.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                satir = satir.trim();
                if (satir.isEmpty()) continue;
                String[] parts = satir.split(";");
                if (parts.length >= 3) {
                    dersler.add(parts); // Bölüm;Sınıf;Ders;Öğretmen
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // 3. Notlar ve devamsızlık listesini oluştur
        java.util.List<String> yeniNotlar = new java.util.ArrayList<>();
        java.util.List<String> yeniDevamsizliklar = new java.util.ArrayList<>();
        
        for (Ogrenci ogr : ogrenciListesi) {
            java.util.List<Double> ogrNotlar = new java.util.ArrayList<>();
            
            for (String[] ders : dersler) {
                String dBolum = ders[0].trim();
                int dSinif = 1;
                try {
                    dSinif = Integer.parseInt(ders[1].trim());
                } catch (Exception e) {}
                String dAd = ders[2].trim();
                
                if (dBolum.equalsIgnoreCase(ogr.getBolum()) && dSinif == ogr.getSinif()) {
                    int vize = 45 + rand.nextInt(56); // 45 - 100
                    int fin = 45 + rand.nextInt(56);  // 45 - 100
                    int devamsizlik = rand.nextInt(9); // 0 - 8 (arada devamsızlıktan kalan olsun)
                    
                    yeniNotlar.add(ogr.getId() + ";" + dAd + ";" + vize + ";" + fin);
                    yeniDevamsizliklar.add(ogr.getId() + ";" + dAd + ";" + devamsizlik);
                    
                    double dersOrt = (vize * 0.4) + (fin * 0.6);
                    ogrNotlar.add(dersOrt);
                }
            }
            
            // Ortalama hesapla
            double gpa = 0.0;
            if (!ogrNotlar.isEmpty()) {
                double toplamOrt = 0;
                for (double n : ogrNotlar) {
                    toplamOrt += n;
                }
                double avg100 = toplamOrt / ogrNotlar.size();
                gpa = (avg100 / 100.0) * 4.0;
                // virgülden sonra 2 hane
                gpa = Math.round(gpa * 100.0) / 100.0;
            } else {
                gpa = 2.0 + (rand.nextDouble() * 2.0); // ders yoksa rastgele 2-4 arası
                gpa = Math.round(gpa * 100.0) / 100.0;
            }
            ogr.setOrtalama(gpa);
        }
        
        // 4. Dosyaları diske yaz
        dosyayaYaz(); // ogrenciler.txt
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("notlar.txt"))) {
            for (String n : yeniNotlar) {
                bw.write(n);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("devamsizlik.txt"))) {
            for (String d : yeniDevamsizliklar) {
                bw.write(d);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void dosyayaYaz() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(dosyaAdi))) {
            for (Ogrenci ogr : ogrenciListesi) {
                String satir = ogr.getId() + ";" +
                               sifrele(ogr.getSifre()) + ";" +
                               ogr.getAd() + ";" +
                               ogr.getBolum() + ";" +
                               ogr.getOrtalama() + ";" +
                               ogr.getEposta() + ";" +
                               ogr.getTelefon() + ";" +
                               ogr.getAdres() + ";" +
                               ogr.getTc() + ";" +
                               ogr.getSinif();
                bw.write(satir);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Ogrenci> getOgrenciListesi() {
        return ogrenciListesi;
    }

    public void ogrenciEkle(Ogrenci ogr) {
        ogrenciListesi.add(ogr);
        dosyayaYaz();
    }

    public void ogrenciSil(int id) {
        ogrenciListesi.removeIf(ogr -> ogr.getId() == id);
        dosyayaYaz();
    }

    public void ogrenciGuncelle(int id, String yeniSifre, String yeniAd, String yeniBolum, double yeniOrt) {
        for (Ogrenci ogr : ogrenciListesi) {
            if (ogr.getId() == id) {
                ogr.setSifre(yeniSifre);
                ogr.setAd(yeniAd);
                ogr.setBolum(yeniBolum);
                ogr.setOrtalama(yeniOrt);
                break;
            }
        }
        dosyayaYaz();
    }

    public void ogrenciGuncelle(int id, String yeniSifre, String yeniAd, String yeniBolum, int yeniSinif, double yeniOrt) {
        for (Ogrenci ogr : ogrenciListesi) {
            if (ogr.getId() == id) {
                ogr.setSifre(yeniSifre);
                ogr.setAd(yeniAd);
                ogr.setBolum(yeniBolum);
                ogr.setSinif(yeniSinif);
                ogr.setOrtalama(yeniOrt);
                break;
            }
        }
        dosyayaYaz();
    }

    public void ortalamayaGoreSirala() {
        Collections.sort(ogrenciListesi, new Comparator<Ogrenci>() {
            @Override
            public int compare(Ogrenci o1, Ogrenci o2) {
                return Double.compare(o2.getOrtalama(), o1.getOrtalama()); 
            }
        });
    }

    public static String sifrele(String sifre) {
        return Base64.getEncoder().encodeToString(sifre.getBytes(StandardCharsets.UTF_8));
    }

    public static String sifreCoz(String sifreliMetin) {
        try {
            byte[] decoded = Base64.getDecoder().decode(sifreliMetin);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return sifreliMetin;
        }
    }
}

