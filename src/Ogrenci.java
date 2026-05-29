public class Ogrenci extends Kullanici {
    private int id;
    private String bolum;
    private double ortalama;
    private String eposta;
    private String telefon;
    private String adres;
    private String tc;

    // Constructor (Yapıcı Metot)
    public Ogrenci(int id, String sifre, String ad, String bolum, double ortalama, String eposta, String telefon, String adres, String tc) {
        super(ad, sifre);
        this.id = id;
        this.bolum = bolum;
        this.ortalama = ortalama;
        this.eposta = eposta;
        this.telefon = telefon;
        this.adres = adres;
        this.tc = tc;
    }

    // Geriye dönük uyumluluk için eski constructor (gerekirse diye)
    public Ogrenci(int id, String sifre, String ad, String bolum, double ortalama) {
        this(id, sifre, ad, bolum, ortalama, "tanimsiz", "tanimsiz", "tanimsiz", "tanimsiz");
    }

    @Override
    public String getKullaniciTipi() {
        return "Öğrenci";
    }

    @Override
    public void bilgileriGoster() {
        System.out.println("Öğrenci: " + ad + " - Bölüm: " + bolum);
    }

    // Getter ve Setter Metotları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBolum() { return bolum; }
    public void setBolum(String bolum) { this.bolum = bolum; }

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