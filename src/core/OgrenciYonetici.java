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

        try (BufferedReader br = new BufferedReader(new FileReader(dosya))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                satir = satir.replace("\uFEFF", "").trim(); 
                if (satir.isEmpty()) continue;

                String[] veri = satir.split(";");
                
                if (veri.length >= 5) {
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

                        ogrenciListesi.add(new Ogrenci(id, sifre, ad, bolum, ort, eposta, telefon, adres, tc));
                    } catch (Exception e) {}
                }
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
                               ogr.getTc();
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

    // --- GÜNCELLENDİ: Şifre parametresi de eklendi ---
    public void ogrenciGuncelle(int id, String yeniSifre, String yeniAd, String yeniBolum, double yeniOrt) {
        for (Ogrenci ogr : ogrenciListesi) {
            if (ogr.getId() == id) {
                ogr.setSifre(yeniSifre); // Şifre de artık güncelleniyor
                ogr.setAd(yeniAd);
                ogr.setBolum(yeniBolum);
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

