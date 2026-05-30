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
}
