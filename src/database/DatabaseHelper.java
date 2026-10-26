package database;

import model.Ogrenci;
import model.Ogretmen;
import core.OgrenciYonetici;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:ogrenci_sistemi.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static synchronized void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabloları oluştur
            stmt.execute("CREATE TABLE IF NOT EXISTS ogrenciler (" +
                    "id INTEGER PRIMARY KEY," +
                    "sifre TEXT NOT NULL," +
                    "ad TEXT NOT NULL," +
                    "bolum TEXT NOT NULL," +
                    "sinif INTEGER NOT NULL DEFAULT 1," +
                    "ortalama REAL NOT NULL DEFAULT 0.0," +
                    "eposta TEXT DEFAULT 'tanimsiz'," +
                    "telefon TEXT DEFAULT 'tanimsiz'," +
                    "adres TEXT DEFAULT 'tanimsiz'," +
                    "tc TEXT DEFAULT 'tanimsiz'" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS ogretmenler (" +
                    "tc_kimlik TEXT PRIMARY KEY," +
                    "sifre TEXT NOT NULL," +
                    "ad TEXT NOT NULL," +
                    "verilen_ders TEXT NOT NULL" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS dersler (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "bolum TEXT NOT NULL," +
                    "sinif INTEGER NOT NULL," +
                    "ders_adi TEXT NOT NULL," +
                    "ogretmen_adi TEXT" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS notlar (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ogrenci_id INTEGER NOT NULL," +
                    "ders_adi TEXT NOT NULL," +
                    "vize INTEGER NOT NULL DEFAULT 0," +
                    "final INTEGER NOT NULL DEFAULT 0," +
                    "UNIQUE(ogrenci_id, ders_adi)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS devamsizlik (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ogrenci_id INTEGER NOT NULL," +
                    "ders_adi TEXT NOT NULL," +
                    "saat INTEGER NOT NULL DEFAULT 0," +
                    "UNIQUE(ogrenci_id, ders_adi)" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS mesajlar (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "gonderen TEXT NOT NULL," +
                    "alici TEXT NOT NULL," +
                    "baslik TEXT NOT NULL," +
                    "icerik TEXT NOT NULL," +
                    "tarih TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            stmt.execute("CREATE TABLE IF NOT EXISTS duyurular (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "hedef TEXT NOT NULL," +
                    "icerik TEXT NOT NULL," +
                    "tarih TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ");");

            // Txt dosyalarından SQLite'a otomatik veri aktarımı (Migration)
            verileriTxtDosyalarindanAktar(conn);
            varsayilanVerileriEkleEgerBos(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void verileriTxtDosyalarindanAktar(Connection conn) {
        try {
            // 1. Öğrencileri aktar
            if (tabloBosMu(conn, "ogrenciler")) {
                File ogrDosya = new File("ogrenciler.txt");
                if (ogrDosya.exists()) {
                    String sql = "INSERT OR REPLACE INTO ogrenciler (id, sifre, ad, bolum, ortalama, eposta, telefon, adres, tc, sinif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(ogrDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.replace("\uFEFF", "").trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 5) {
                                try {
                                    pstmt.setInt(1, Integer.parseInt(v[0].trim()));
                                    pstmt.setString(2, OgrenciYonetici.sifreCoz(v[1].trim()));
                                    pstmt.setString(3, v[2].trim());
                                    pstmt.setString(4, v[3].trim());
                                    pstmt.setDouble(5, Double.parseDouble(v[4].trim()));
                                    pstmt.setString(6, v.length > 5 ? v[5].trim() : "tanimsiz");
                                    pstmt.setString(7, v.length > 6 ? v[6].trim() : "tanimsiz");
                                    pstmt.setString(8, v.length > 7 ? v[7].trim() : "tanimsiz");
                                    pstmt.setString(9, v.length > 8 ? v[8].trim() : "tanimsiz");
                                    pstmt.setInt(10, v.length > 9 ? Integer.parseInt(v[9].trim()) : 1);
                                    pstmt.executeUpdate();
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }

            // 2. Öğretmenleri aktar
            if (tabloBosMu(conn, "ogretmenler")) {
                File hocaDosya = new File("ogretmenler.txt");
                if (hocaDosya.exists()) {
                    String sql = "INSERT OR REPLACE INTO ogretmenler (tc_kimlik, sifre, ad, verilen_ders) VALUES (?, ?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(hocaDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 4) {
                                pstmt.setString(1, v[0].trim());
                                pstmt.setString(2, OgrenciYonetici.sifreCoz(v[1].trim()));
                                pstmt.setString(3, v[2].trim());
                                pstmt.setString(4, v[3].trim());
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 3. Dersleri aktar
            if (tabloBosMu(conn, "dersler")) {
                File dersDosya = new File("dersler.txt");
                if (dersDosya.exists()) {
                    String sql = "INSERT INTO dersler (bolum, sinif, ders_adi, ogretmen_adi) VALUES (?, ?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(dersDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 3) {
                                pstmt.setString(1, v[0].trim());
                                pstmt.setInt(2, Integer.parseInt(v[1].trim()));
                                pstmt.setString(3, v[2].trim());
                                pstmt.setString(4, v.length >= 4 ? v[3].trim() : "");
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 4. Notları aktar
            if (tabloBosMu(conn, "notlar")) {
                File notDosya = new File("notlar.txt");
                if (notDosya.exists()) {
                    String sql = "INSERT OR REPLACE INTO notlar (ogrenci_id, ders_adi, vize, final) VALUES (?, ?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(notDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 4) {
                                try {
                                    pstmt.setInt(1, Integer.parseInt(v[0].trim()));
                                    pstmt.setString(2, v[1].trim());
                                    pstmt.setInt(3, Integer.parseInt(v[2].trim()));
                                    pstmt.setInt(4, Integer.parseInt(v[3].trim()));
                                    pstmt.executeUpdate();
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }

            // 5. Devamsızlıkları aktar
            if (tabloBosMu(conn, "devamsizlik")) {
                File devDosya = new File("devamsizlik.txt");
                if (devDosya.exists()) {
                    String sql = "INSERT OR REPLACE INTO devamsizlik (ogrenci_id, ders_adi, saat) VALUES (?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(devDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 3) {
                                try {
                                    pstmt.setInt(1, Integer.parseInt(v[0].trim()));
                                    pstmt.setString(2, v[1].trim());
                                    pstmt.setInt(3, Integer.parseInt(v[2].trim()));
                                    pstmt.executeUpdate();
                                } catch (Exception ignored) {}
                            }
                        }
                    }
                }
            }

            // 6. Mesajları aktar
            if (tabloBosMu(conn, "mesajlar")) {
                File msgDosya = new File("mesajlar.txt");
                if (msgDosya.exists()) {
                    String sql = "INSERT INTO mesajlar (gonderen, alici, baslik, icerik) VALUES (?, ?, ?, ?)";
                    try (BufferedReader br = new BufferedReader(new FileReader(msgDosya));
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        String satir;
                        while ((satir = br.readLine()) != null) {
                            satir = satir.trim();
                            if (satir.isEmpty()) continue;
                            String[] v = satir.split(";");
                            if (v.length >= 4) {
                                pstmt.setString(1, v[0].trim());
                                pstmt.setString(2, v[1].trim());
                                pstmt.setString(3, OgrenciYonetici.sifreCoz(v[2].trim()));
                                pstmt.setString(4, OgrenciYonetici.sifreCoz(v[3].trim()));
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 7. Duyuruları aktar (duyurular.dat)
            if (tabloBosMu(conn, "duyurular")) {
                File duyuruDosya = new File("duyurular.dat");
                if (duyuruDosya.exists() && duyuruDosya.length() > 0) {
                    String sql = "INSERT INTO duyurular (hedef, icerik) VALUES (?, ?)";
                    try (RandomAccessFile raf = new RandomAccessFile(duyuruDosya, "r");
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        while (raf.getFilePointer() < raf.length()) {
                            try {
                                String satir = raf.readUTF();
                                String[] v = satir.split(";");
                                if (v.length >= 2) {
                                    pstmt.setString(1, v[0].trim());
                                    pstmt.setString(2, v[1].trim());
                                    pstmt.executeUpdate();
                                }
                            } catch (EOFException e) {
                                break;
                            }
                        }
                    } catch (Exception ignored) {}
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void varsayilanVerileriEkleEgerBos(Connection conn) {
        try {
            // Tablolar hala boşsa (txt dosyaları bulunamadıysa/silindiyse), README'deki demo hesapları ekle
            if (tabloBosMu(conn, "ogretmenler")) {
                String sql = "INSERT OR REPLACE INTO ogretmenler (tc_kimlik, sifre, ad, verilen_ders) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "11000000001");
                    pstmt.setString(2, "1234");
                    pstmt.setString(3, "Dr. Öğr. Üyesi Mustafa Yıldız");
                    pstmt.setString(4, "Kimya Mühendisliği");
                    pstmt.executeUpdate();

                    pstmt.setString(1, "11000000002");
                    pstmt.setString(2, "1234");
                    pstmt.setString(3, "Prof. Dr. Kemal Arslan");
                    pstmt.setString(4, "Bilgisayar Mühendisliği");
                    pstmt.executeUpdate();
                }
            }

            if (tabloBosMu(conn, "ogrenciler")) {
                String sql = "INSERT OR REPLACE INTO ogrenciler (id, sifre, ad, bolum, ortalama, eposta, telefon, adres, tc, sinif) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, 250260028);
                    pstmt.setString(2, "1234");
                    pstmt.setString(3, "Caner Doğan");
                    pstmt.setString(4, "Kimya Mühendisliği");
                    pstmt.setDouble(5, 3.45);
                    pstmt.setString(6, "caner.dogan@edu.tr");
                    pstmt.setString(7, "5551112233");
                    pstmt.setString(8, "Kampüs");
                    pstmt.setString(9, "25026002801");
                    pstmt.setInt(10, 3);
                    pstmt.executeUpdate();

                    pstmt.setInt(1, 250260001);
                    pstmt.setString(2, "kaya123");
                    pstmt.setString(3, "Eren Kaya");
                    pstmt.setString(4, "Bilgisayar Mühendisliği");
                    pstmt.setDouble(5, 3.65);
                    pstmt.setString(6, "eren.kaya@edu.tr");
                    pstmt.setString(7, "5552223344");
                    pstmt.setString(8, "Kampüs");
                    pstmt.setString(9, "25026000102");
                    pstmt.setInt(10, 2);
                    pstmt.executeUpdate();
                }
            }

            if (tabloBosMu(conn, "dersler")) {
                String sql = "INSERT INTO dersler (bolum, sinif, ders_adi, ogretmen_adi) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "Bilgisayar Mühendisliği");
                    pstmt.setInt(2, 2);
                    pstmt.setString(3, "Nesne Yönelimli Programlama");
                    pstmt.setString(4, "Prof. Dr. Kemal Arslan");
                    pstmt.executeUpdate();

                    pstmt.setString(1, "Kimya Mühendisliği");
                    pstmt.setInt(2, 3);
                    pstmt.setString(3, "Termodinamik");
                    pstmt.setString(4, "Dr. Öğr. Üyesi Mustafa Yıldız");
                    pstmt.executeUpdate();
                }
            }

            if (tabloBosMu(conn, "notlar")) {
                String sql = "INSERT OR REPLACE INTO notlar (ogrenci_id, ders_adi, vize, final) VALUES (?, ?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, 250260001);
                    pstmt.setString(2, "Nesne Yönelimli Programlama");
                    pstmt.setInt(3, 85);
                    pstmt.setInt(4, 90);
                    pstmt.executeUpdate();

                    pstmt.setInt(1, 250260028);
                    pstmt.setString(2, "Termodinamik");
                    pstmt.setInt(3, 75);
                    pstmt.setInt(4, 80);
                    pstmt.executeUpdate();
                }
            }

            if (tabloBosMu(conn, "devamsizlik")) {
                String sql = "INSERT OR REPLACE INTO devamsizlik (ogrenci_id, ders_adi, saat) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, 250260001);
                    pstmt.setString(2, "Nesne Yönelimli Programlama");
                    pstmt.setInt(3, 2);
                    pstmt.executeUpdate();

                    pstmt.setInt(1, 250260028);
                    pstmt.setString(2, "Termodinamik");
                    pstmt.setInt(3, 4);
                    pstmt.executeUpdate();
                }
            }

            if (tabloBosMu(conn, "duyurular")) {
                String sql = "INSERT INTO duyurular (hedef, icerik) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, "Genel");
                    pstmt.setString(2, "2026-2027 Bahar dönemi ders kayıtları başlamıştır.");
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean tabloBosMu(Connection conn, String tabloAdi) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tabloAdi)) {
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    // --- Öğrenci İşlemleri ---

    public static ArrayList<Ogrenci> tumOgrencileriGetir() {
        ArrayList<Ogrenci> liste = new ArrayList<>();
        String sql = "SELECT id, sifre, ad, bolum, sinif, ortalama, eposta, telefon, adres, tc FROM ogrenciler ORDER BY ortalama DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String sifre = rs.getString("sifre");
                String ad = rs.getString("ad");
                String bolum = rs.getString("bolum");
                int sinif = rs.getInt("sinif");
                double ort = rs.getDouble("ortalama");
                String eposta = rs.getString("eposta");
                String telefon = rs.getString("telefon");
                String adres = rs.getString("adres");
                String tc = rs.getString("tc");
                liste.add(new Ogrenci(id, sifre, ad, bolum, sinif, ort, eposta, telefon, adres, tc));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public static void ogrenciEkle(Ogrenci ogr) {
        String sql = "INSERT OR REPLACE INTO ogrenciler (id, sifre, ad, bolum, sinif, ortalama, eposta, telefon, adres, tc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogr.getId());
            pstmt.setString(2, ogr.getSifre());
            pstmt.setString(3, ogr.getAd());
            pstmt.setString(4, ogr.getBolum());
            pstmt.setInt(5, ogr.getSinif());
            pstmt.setDouble(6, ogr.getOrtalama());
            pstmt.setString(7, ogr.getEposta() != null ? ogr.getEposta() : "tanimsiz");
            pstmt.setString(8, ogr.getTelefon() != null ? ogr.getTelefon() : "tanimsiz");
            pstmt.setString(9, ogr.getAdres() != null ? ogr.getAdres() : "tanimsiz");
            pstmt.setString(10, ogr.getTc() != null ? ogr.getTc() : "tanimsiz");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ogrenciGuncelle(int id, String yeniSifre, String yeniAd, String yeniBolum, int yeniSinif, double yeniOrt) {
        String sql = "UPDATE ogrenciler SET sifre = ?, ad = ?, bolum = ?, sinif = ?, ortalama = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, yeniSifre);
            pstmt.setString(2, yeniAd);
            pstmt.setString(3, yeniBolum);
            pstmt.setInt(4, yeniSinif);
            pstmt.setDouble(5, yeniOrt);
            pstmt.setInt(6, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ogrenciSil(int id) {
        String sql = "DELETE FROM ogrenciler WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            // İlgili notlar ve devamsızlıkları da temizleyebiliriz
            try (PreparedStatement p2 = conn.prepareStatement("DELETE FROM notlar WHERE ogrenci_id = ?");
                 PreparedStatement p3 = conn.prepareStatement("DELETE FROM devamsizlik WHERE ogrenci_id = ?")) {
                p2.setInt(1, id);
                p2.executeUpdate();
                p3.setInt(1, id);
                p3.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ogrenciProfilGuncelle(int id, String eposta, String telefon, String adres, String tc) {
        String sql = "UPDATE ogrenciler SET eposta = ?, telefon = ?, adres = ?, tc = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, eposta);
            pstmt.setString(2, telefon);
            pstmt.setString(3, adres);
            pstmt.setString(4, tc);
            pstmt.setInt(5, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ogrenciOrtalamaGuncelle(int id, double yeniOrt) {
        String sql = "UPDATE ogrenciler SET ortalama = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, yeniOrt);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Ogrenci ogrenciGirisKontrol(int id, String sifre) {
        String sql = "SELECT * FROM ogrenciler WHERE id = ? AND sifre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, sifre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Ogrenci(
                            rs.getInt("id"),
                            rs.getString("sifre"),
                            rs.getString("ad"),
                            rs.getString("bolum"),
                            rs.getInt("sinif"),
                            rs.getDouble("ortalama"),
                            rs.getString("eposta"),
                            rs.getString("telefon"),
                            rs.getString("adres"),
                            rs.getString("tc")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Öğretmen İşlemleri ---

    public static Ogretmen ogretmenGirisKontrol(String tc, String sifre) {
        String sql = "SELECT * FROM ogretmenler WHERE tc_kimlik = ? AND sifre = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, sifre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Ogretmen(
                            rs.getString("tc_kimlik"),
                            rs.getString("sifre"),
                            rs.getString("ad"),
                            rs.getString("verilen_ders")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void ogretmenEkle(String tc, String sifre, String ad, String verilenDers) {
        String sql = "INSERT OR REPLACE INTO ogretmenler (tc_kimlik, sifre, ad, verilen_ders) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tc);
            pstmt.setString(2, sifre);
            pstmt.setString(3, ad);
            pstmt.setString(4, verilenDers);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> ogretmenDersleriniGetir(String ogretmenAdi, String defaultDers) {
        List<String> dersler = new ArrayList<>();
        String sql = "SELECT DISTINCT ders_adi FROM dersler WHERE LOWER(TRIM(ogretmen_adi)) = LOWER(TRIM(?))";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ogretmenAdi);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String d = rs.getString("ders_adi").trim();
                    if (!dersler.contains(d)) {
                        dersler.add(d);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (dersler.isEmpty() && defaultDers != null && !defaultDers.isEmpty()) {
            String[] parts = defaultDers.split("[,;]");
            for (String p : parts) {
                String trimmed = p.trim();
                if (!trimmed.isEmpty() && !dersler.contains(trimmed)) {
                    dersler.add(trimmed);
                }
            }
        }
        return dersler;
    }

    // --- Ders İşlemleri ---

    public static void dersEkle(String bolum, int sinif, String dersAdi, String ogretmenAdi) {
        String sql = "INSERT INTO dersler (bolum, sinif, ders_adi, ogretmen_adi) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bolum);
            pstmt.setInt(2, sinif);
            pstmt.setString(3, dersAdi);
            pstmt.setString(4, ogretmenAdi);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> bolumSinifDersleriGetir(String bolum, int sinif) {
        List<String> dersler = new ArrayList<>();
        String sql = "SELECT DISTINCT ders_adi FROM dersler WHERE LOWER(TRIM(bolum)) = LOWER(TRIM(?)) AND sinif = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bolum);
            pstmt.setInt(2, sinif);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dersler.add(rs.getString("ders_adi").trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dersler;
    }

    public static List<String[]> tumDersleriGetir() {
        List<String[]> liste = new ArrayList<>();
        String sql = "SELECT bolum, sinif, ders_adi, ogretmen_adi FROM dersler";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                liste.add(new String[]{
                        rs.getString("bolum"),
                        String.valueOf(rs.getInt("sinif")),
                        rs.getString("ders_adi"),
                        rs.getString("ogretmen_adi")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    // --- Not İşlemleri ---

    public static void notKaydet(int ogrenciId, String dersAdi, int vize, int finalNotu) {
        String sql = "INSERT INTO notlar (ogrenci_id, ders_adi, vize, final) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT(ogrenci_id, ders_adi) DO UPDATE SET vize = excluded.vize, final = excluded.final";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            pstmt.setString(2, dersAdi);
            pstmt.setInt(3, vize);
            pstmt.setInt(4, finalNotu);
            pstmt.executeUpdate();
            ogrenciGpaGuncelle(ogrenciId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void ogrenciGpaGuncelle(int ogrenciId) {
        String sql = "SELECT vize, final FROM notlar WHERE ogrenci_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                double toplam = 0;
                int count = 0;
                while (rs.next()) {
                    int vize = rs.getInt("vize");
                    int fin = rs.getInt("final");
                    toplam += (vize * 0.4) + (fin * 0.6);
                    count++;
                }
                if (count > 0) {
                    double avg100 = toplam / count;
                    double gpa = (avg100 / 100.0) * 4.0;
                    gpa = Math.round(gpa * 100.0) / 100.0;
                    ogrenciOrtalamaGuncelle(ogrenciId, gpa);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Object[]> ogrenciNotlariniGetir(int ogrenciId) {
        List<Object[]> satirlar = new ArrayList<>();
        String sql = "SELECT n.ders_adi, n.vize, n.final, COALESCE(d.saat, 0) as devamsizlik " +
                     "FROM notlar n " +
                     "LEFT JOIN devamsizlik d ON n.ogrenci_id = d.ogrenci_id AND LOWER(TRIM(n.ders_adi)) = LOWER(TRIM(d.ders_adi)) " +
                     "WHERE n.ogrenci_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dersAdi = rs.getString("ders_adi");
                    int vize = rs.getInt("vize");
                    int fin = rs.getInt("final");
                    int devamsizlik = rs.getInt("devamsizlik");

                    double ort = (vize * 0.4) + (fin * 0.6);
                    String harfNotu;
                    if (devamsizlik >= 8) {
                        harfNotu = "DZ";
                    } else {
                        if (ort >= 85) harfNotu = "AA";
                        else if (ort >= 75) harfNotu = "BA";
                        else if (ort >= 65) harfNotu = "BB";
                        else if (ort >= 55) harfNotu = "CB";
                        else if (ort >= 50) harfNotu = "CC";
                        else if (ort >= 40) harfNotu = "DC";
                        else harfNotu = "FF";
                    }

                    satirlar.add(new Object[]{dersAdi, vize, fin, harfNotu});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return satirlar;
    }

    public static void ogrenciyeDersAta(int ogrenciId, String dersAdi) {
        String sql = "INSERT OR IGNORE INTO notlar (ogrenci_id, ders_adi, vize, final) VALUES (?, ?, 0, 0)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            pstmt.setString(2, dersAdi);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Devamsızlık İşlemleri ---

    public static void devamsizlikKaydet(int ogrenciId, String dersAdi, int saat) {
        String sql = "INSERT INTO devamsizlik (ogrenci_id, ders_adi, saat) VALUES (?, ?, ?) " +
                     "ON CONFLICT(ogrenci_id, ders_adi) DO UPDATE SET saat = excluded.saat";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            pstmt.setString(2, dersAdi);
            pstmt.setInt(3, saat);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int dersDevamsizlikGetir(int ogrenciId, String dersAdi) {
        String sql = "SELECT saat FROM devamsizlik WHERE ogrenci_id = ? AND LOWER(TRIM(ders_adi)) = LOWER(TRIM(?))";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            pstmt.setString(2, dersAdi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("saat");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<Object[]> ogrenciDevamsizliklariniGetir(int ogrenciId) {
        List<Object[]> satirlar = new ArrayList<>();
        String sql = "SELECT ders_adi, saat FROM devamsizlik WHERE ogrenci_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ogrenciId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dersAdi = rs.getString("ders_adi");
                    int yapilanDevamsizlik = rs.getInt("saat");
                    int devamsizlikSiniri = 8;
                    int kalanHak = Math.max(0, devamsizlikSiniri - yapilanDevamsizlik);

                    String durum;
                    if (yapilanDevamsizlik >= 8) {
                        durum = "DEVAMSIZLIKTAN KALDI";
                    } else if (yapilanDevamsizlik >= 6) {
                        durum = "RİSKLİ";
                    } else {
                        durum = "İYİ";
                    }

                    satirlar.add(new Object[]{dersAdi, yapilanDevamsizlik, kalanHak, durum});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return satirlar;
    }

    // --- Mesaj İşlemleri ---

    public static void mesajGonder(String gonderen, String alici, String baslik, String icerik) {
        String sql = "INSERT INTO mesajlar (gonderen, alici, baslik, icerik) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, gonderen);
            pstmt.setString(2, alici);
            pstmt.setString(3, baslik);
            pstmt.setString(4, icerik);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getGelenMesajSayisi(String alici) {
        String sql = "SELECT COUNT(*) FROM mesajlar WHERE alici = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, alici);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static List<String[]> getGelenMesajlar(String alici) {
        List<String[]> mesajlar = new ArrayList<>();
        String sql = "SELECT gonderen, baslik, icerik, tarih FROM mesajlar WHERE alici = ? ORDER BY id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, alici);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    mesajlar.add(new String[]{
                            rs.getString("gonderen"),
                            rs.getString("baslik"),
                            rs.getString("icerik"),
                            rs.getString("tarih")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mesajlar;
    }

    // --- Duyuru İşlemleri ---

    public static void duyuruEkle(String hedef, String icerik) {
        String sql = "INSERT INTO duyurular (hedef, icerik) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hedef);
            pstmt.setString(2, icerik);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> ogrenciDuyurulariGetir(String bolum) {
        List<String> duyurular = new ArrayList<>();
        String sql = "SELECT hedef, icerik, tarih FROM duyurular WHERE hedef = 'Genel' OR LOWER(TRIM(hedef)) = LOWER(TRIM(?)) ORDER BY id DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bolum);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String hedef = rs.getString("hedef");
                    String icerik = rs.getString("icerik");
                    duyurular.add("[" + hedef + "] " + icerik);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return duyurular;
    }

    public static void main(String[] args) {
        System.out.println("SQLite Veritabanı başlatılıyor...");
        initDatabase();
        System.out.println("Tablolar oluşturuldu ve veriler aktarıldı.");
        System.out.println("Kayıtlı Öğrenci Sayısı: " + tumOgrencileriGetir().size());
        System.out.println("Kayıtlı Ders Sayısı: " + tumDersleriGetir().size());
        System.out.println("Admin'e Gelen Mesaj Sayısı: " + getGelenMesajSayisi("admin"));
        
        // Giriş ve veri doğrulama testleri
        var hoca = ogretmenGirisKontrol("11000000001", "1234");
        System.out.println("Öğretmen Giriş Testi (11000000001): " + (hoca != null ? "BAŞARILI (" + hoca.getAd() + ")" : "BAŞARISIZ"));

        var ogr = ogrenciGirisKontrol(250260000, "ucar4646");
        System.out.println("Öğrenci Giriş Testi (250260000): " + (ogr != null ? "BAŞARILI (" + ogr.getAd() + ")" : "BAŞARISIZ"));

        var notlar = ogrenciNotlariniGetir(250260028);
        System.out.println("Öğrenci (250260028) Not Sayısı: " + notlar.size());

        var devamsizlik = ogrenciDevamsizliklariniGetir(250260028);
        System.out.println("Öğrenci (250260028) Devamsızlık Sayısı: " + devamsizlik.size());

        var duyurular = ogrenciDuyurulariGetir("Bilgisayar Mühendisliği");
        System.out.println("Bilgisayar Müh. Duyuru Sayısı: " + duyurular.size());

        System.out.println("Tüm kontroller başarıyla tamamlandı!");
    }
}
