public abstract class Kullanici implements KullaniciArayuzu {
    protected String ad;
    protected String sifre;

    public Kullanici(String ad, String sifre) {
        this.ad = ad;
        this.sifre = sifre;
    }

    public String getAd() { return ad; }
    public void setAd(String ad) { this.ad = ad; }

    public String getSifre() { return sifre; }
    public void setSifre(String sifre) { this.sifre = sifre; }

    public abstract String getKullaniciTipi();
}
