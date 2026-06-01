package model;

public class Ogretmen extends Kullanici {
    private String tcKimlik;
    private String verilenDers;

    public Ogretmen(String tcKimlik, String sifre, String ad, String verilenDers) {
        super(ad, sifre);
        this.tcKimlik = tcKimlik;
        this.verilenDers = verilenDers;
    }

    @Override
    public String getKullaniciTipi() {
        return "Öğretim Elemanı";
    }

    @Override
    public void bilgileriGoster() {
        System.out.println("Öğretmen: " + ad + " - Ders: " + verilenDers);
    }

    public String getTcKimlik() { return tcKimlik; }
    public void setTcKimlik(String tcKimlik) { this.tcKimlik = tcKimlik; }
    
    public String getVerilenDers() { return verilenDers; }
    public void setVerilenDers(String verilenDers) { this.verilenDers = verilenDers; }

    public java.util.List<String> getDerslerListesi() {
        java.util.List<String> derslerListesi = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("dersler.txt"))) {
            String satir;
            while ((satir = br.readLine()) != null) {
                String[] veri = satir.split(";");
                if (veri.length >= 4 && veri[3].trim().equalsIgnoreCase(this.ad.trim())) {
                    String dersAdi = veri[2].trim();
                    if (!derslerListesi.contains(dersAdi)) {
                        derslerListesi.add(dersAdi);
                    }
                }
            }
        } catch (Exception e) {}
        
        if (derslerListesi.isEmpty() && verilenDers != null && !verilenDers.isEmpty()) {
            String[] parts = verilenDers.split("[,;]");
            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty() && !derslerListesi.contains(trimmed)) {
                    derslerListesi.add(trimmed);
                }
            }
        }
        return derslerListesi;
    }
}
