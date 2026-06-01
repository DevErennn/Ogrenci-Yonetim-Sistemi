package admin;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DaireGrafikPaneli extends JPanel {
    private ArrayList<String> etiketler = new ArrayList<>();
    private ArrayList<Double> degerler = new ArrayList<>();
    private double toplam = 0.0;
    
    private Color[] renkler = {
        new Color(70, 130, 180),  // Steel Blue
        new Color(220, 20, 60),   // Crimson
        new Color(60, 179, 113),  // Medium Sea Green
        new Color(255, 140, 0),   // Dark Orange
        new Color(138, 43, 226),  // Blue Violet
        new Color(0, 206, 209),   // Dark Turquoise
        new Color(255, 20, 147),  // Deep Pink
        new Color(210, 180, 140), // Tan
        new Color(46, 139, 87),   // Sea Green
        new Color(255, 215, 0)    // Gold
    };

    public void verileriGuncelle(ArrayList<String> etiketler, ArrayList<Double> degerler, double toplam) {
        this.etiketler = etiketler;
        this.degerler = degerler;
        this.toplam = toplam;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (toplam == 0.0) {
            g2d.drawString("Grafik için henüz veri yok.", 50, 50);
            return;
        }

        int w = getWidth();
        int h = getHeight();
        
        // Daireyi sol tarafa, lejantı sağ tarafa çiz
        int cap = Math.min(w / 2 - 20, h - 60);
        if (cap < 100) cap = 100;
        int x = 30;
        int y = (h - cap) / 2;  
        
        int baslangicAcisi = 0;
        for (int i = 0; i < etiketler.size(); i++) {
            double sayi = degerler.get(i);
            int aci = (int) Math.round((sayi / toplam) * 360);
            
            if (i == etiketler.size() - 1) {
                aci = 360 - baslangicAcisi;
            }
            
            g2d.setColor(renkler[i % renkler.length]);
            g2d.fillArc(x, y, cap, cap, baslangicAcisi, aci);
            baslangicAcisi += aci; 
        }

        // Lejantı sağ tarafa dikey sütunlar halinde çiz (taşma olursa yeni sütuna geç)
        int lx = x + cap + 40;
        int ly = 30;
        for (int i = 0; i < etiketler.size(); i++) {
            Color renk = renkler[i % renkler.length];
            
            g2d.setColor(renk);
            g2d.fillRect(lx, ly, 12, 12);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 11));
            
            String text = etiketler.get(i);
            g2d.drawString(text, lx + 18, ly + 11);
            
            ly += 20;
            if (ly > h - 40) {
                ly = 30;
                lx += 180; // Dikey alan yetmezse sağa kaydırıp yeni sütundan devam et
            }
        }
    }
}
