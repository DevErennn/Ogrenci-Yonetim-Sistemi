package admin;

import model.*;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DaireGrafikPaneli extends JPanel {
    private ArrayList<String> bolumler = new ArrayList<>();
    private ArrayList<Integer> kisiSayilari = new ArrayList<>();
    private int toplamKisi = 0;
    
    private Color[] renkler = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK};

    public void verileriGuncelle(ArrayList<String> b, ArrayList<Integer> k, int toplam) {
        this.bolumler = b;
        this.kisiSayilari = k;
        this.toplamKisi = toplam;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (toplamKisi == 0) {
            g2d.drawString("Grafik için henüz veri yok.", 100, 150);
            return;
        }

        int baslangicAcisi = 0;
        int x = 50;  
        int y = 50;  
        int cap = 200; 
        
        int yaziY = 300; 

        for (int i = 0; i < bolumler.size(); i++) {
            int sayi = kisiSayilari.get(i);
            int aci = (int) Math.round((sayi / (double) toplamKisi) * 360);
            
            Color renk = renkler[i % renkler.length];
            g2d.setColor(renk);
            
            g2d.fillArc(x, y, cap, cap, baslangicAcisi, aci);
            
            g2d.fillRect(20, yaziY, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.drawString(bolumler.get(i) + " (" + sayi + " Öğrenci)", 45, yaziY + 12);
            
            yaziY += 25; 
            baslangicAcisi += aci; 
        }
    }
}
