package model;

public class Ogrenci extends Kullanici {
    private int id;
    private String bolum;
    private int sinif;
    private double ortalama;
    private String eposta;
    private String telefon;
    private String adres;
    private String tc;

    public Ogrenci(int id, String sifre, String ad, String bolum, int sinif, double ortalama, String eposta, String telefon, String adres, String tc) {
        super(ad, sifre);
        this.id = id;
        this.bolum = bolum;
        this.sinif = sinif;
        this.ortalama = ortalama;
        this.eposta = eposta;
        this.telefon = telefon;
        this.adres = adres;
        this.tc = tc;
    }

    public Ogrenci(int id, String sifre, String ad, String bolum, int sinif, double ortalama) {
        this(id, sifre, ad, bolum, sinif, ortalama, "tanimsiz", "tanimsiz", "tanimsiz", "tanimsiz");
    }

    //default degerler
    public Ogrenci(int id, String sifre, String ad, String bolum, double ortalama) {
        this(id, sifre, ad, bolum, 1, ortalama, "tanimsiz", "tanimsiz", "tanimsiz", "tanimsiz");
    }

    @Override
    public String getKullaniciTipi() {
        return "Öğrenci";
    }

    @Override
    public void bilgileriGoster() {
        System.out.println("Öğrenci: " + ad + " - Bölüm: " + bolum + " - Sınıf: " + sinif);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBolum() { return bolum; }
    public void setBolum(String bolum) { this.bolum = bolum; }

    public int getSinif() { return sinif; }
    public void setSinif(int sinif) { this.sinif = sinif; }

    public double getOrtalama() { return ortalama; }
    public void setOrtalama(double ortalama) { this.ortalama = ortalama; }

    public String getEposta() { return eposta; }
    public void setEposta(String eposta) { this.eposta = eposta; }

    public String getTelefon() { return telefon; }
    public void setTelefon(String telefon) { this.telefon = telefon; }

    public String getAdres() { return adres; }
    public void setAdres(String adres) { this.adres = adres; }

    public String getTc() { return tc; }
    public void setTc(String tc) { this.tc = tc; }
}