package admin;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SutunGrafikPaneli extends JPanel {
    private ArrayList<String> etiketler = new ArrayList<>();
    private ArrayList<Integer> degerler = new ArrayList<>();
    
    private Color barRengi = new Color(70, 130, 180); // Steel Blue

    public void verileriGuncelle(ArrayList<String> etiketler, ArrayList<Integer> degerler) {
        this.etiketler = etiketler;
        this.degerler = degerler;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int pad = 50; // padding around chart

        if (degerler.isEmpty()) {
            g2d.drawString("Grafik için henüz veri yok.", 50, 50);
            return;
        }

        // Find max value for scaling
        int maxDeger = 0;
        for (int v : degerler) {
            if (v > maxDeger) maxDeger = v;
        }
        if (maxDeger == 0) maxDeger = 1;

        // Draw axes
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(pad, pad, pad, h - pad); // Y-axis
        g2d.drawLine(pad, h - pad, w - pad, h - pad); // X-axis

        int chartWidth = w - 2 * pad;
        int chartHeight = h - 2 * pad;
        int numBars = degerler.size();
        int colWidth = chartWidth / numBars;
        int barWidth = colWidth - 20;
        if (barWidth < 10) barWidth = 10;

        for (int i = 0; i < numBars; i++) {
            int val = degerler.get(i);
            int barHeight = (int) ((val / (double) maxDeger) * (chartHeight - 40));
            int x = pad + 10 + i * colWidth;
            int y = h - pad - barHeight;

            // Draw Bar
            g2d.setColor(barRengi);
            g2d.fillRect(x, y, barWidth, barHeight);
            
            // Draw border around bar
            g2d.setColor(barRengi.darker());
            g2d.drawRect(x, y, barWidth, barHeight);

            // Draw Value text on top of bar
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 11));
            String valStr = String.valueOf(val);
            int strW = g2d.getFontMetrics().stringWidth(valStr);
            g2d.drawString(valStr, x + (barWidth - strW) / 2, y - 5);

            // Draw label at bottom of Y axis
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = etiketler.get(i);
            if (label.length() > 12) {
                label = label.substring(0, 10) + "..";
            }
            int lblW = g2d.getFontMetrics().stringWidth(label);
            
            // Çakışmayı önlemek için etiketleri kademeli (staggered) olarak çiziyoruz
            int lblY = h - pad + 15;
            if (i % 2 == 1) {
                lblY += 15;
            }
            g2d.drawString(label, x + (barWidth - lblW) / 2, lblY);
        }
    }
}
