public class Ogrenci extends Kullanici {
    private int id;
    private String bolum;
    private double ortalama;

    // Constructor (Yapıcı Metot)
    public Ogrenci(int id, String sifre, String ad, String bolum, double ortalama) {
        super(ad, sifre);
        this.id = id;
        this.bolum = bolum;
        this.ortalama = ortalama;
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
}