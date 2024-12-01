import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    BufferedImage image;


    public ImagePanel(BufferedImage image){
        this.image = image;


        this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image,0,0,null);



    }

    public void replaceImage(BufferedImage newImage){
        this.image = newImage;


        this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        revalidate();
        repaint();
    }
}
