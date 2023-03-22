package com.company;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import javax.swing.JComponent;

public class Painter extends JComponent {

    public static int []dx = {1, -1, 0, 0,-1 ,1 ,-1 ,1};
    public static int []dy = {0, 0, 1, -1,1 ,-1 ,1 ,-1};

    public Stack<BufferedImage> imageHistory;
    public Graphics2D g;
    Color color;

    public Painter() {
        color = Color.BLACK;
        imageHistory = new Stack<>();
        setDoubleBuffered(false);
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                BFS(new Point(e.getX(),e.getY()));
            }
        });
    }

    public void paintComponent(Graphics g2) {
        if(imageHistory.isEmpty()) {
            BufferedImage image = new BufferedImage(getSize().width, getSize().height,BufferedImage.TYPE_INT_ARGB);
            imageHistory.add(image);
        }
        BufferedImage image = imageHistory.lastElement() ;
        image = resizeImage(image);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        repaint();
        g2.drawImage(image, 0, 0, null);
    }

    public BufferedImage resizeImage(BufferedImage img) {
        Image tmp = img.getScaledInstance(getSize().width, getSize().height, Image.SCALE_SMOOTH);
        BufferedImage ret = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = ret.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return ret;
    }

    public BufferedImage copyImage(BufferedImage image) {
        return new BufferedImage
                (
                        image.getColorModel(),
                        image.copyData(null),
                        image.getColorModel().isAlphaPremultiplied(),
                        null
                );
    }

    public void setImage(BufferedImage image) {
        imageHistory.removeAllElements();
        imageHistory.add(resizeImage(image));
        g = (Graphics2D) imageHistory.lastElement().getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(imageHistory.lastElement(), 0, 0, null);
        repaint();
    }

    public void unDo(){
        if (imageHistory.size() > 1) {
            imageHistory.pop();
            g = (Graphics2D) imageHistory.lastElement().getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(imageHistory.lastElement(), 0, 0, null);
            repaint();
        }
    }

    public void clear() {
        if (imageHistory.size() > 1) {
            BufferedImage image = imageHistory.firstElement();
            imageHistory.removeAllElements();
            imageHistory.add(image);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, null);
            repaint();
        }
    }

    public void setColor(Color color){
        this.color = color;
    }

    public Color getColor(){
        return color;
    }

    public Image getCurrentImage(){
        return imageHistory.lastElement();
    }

    public Color mulColor(Color c1,Color c2){
        int r = (c1.getRed() * c2.getRed()) / 255;
        int g = (c1.getGreen() * c2.getGreen()) / 255;
        int b = (c1.getBlue() * c2.getBlue()) / 255;
        int a = (c1.getAlpha() * c2.getAlpha()) / 255;
        return new Color(r,g,b,a);
    }

    public boolean graySame(Color c1 ,Color c2) {
        int e = 350;
        int av1 = c1.getRed() + c1.getGreen() + c1.getBlue();
        int av2 = c2.getRed() + c2.getGreen() + c2.getBlue();
        return Math.abs(av1 - av2) <= e ;
    }

    public boolean isBlack(Color c1) {
        Color c2 = Color.BLACK;
        int e = 15;
        int av1 = c1.getRed() + c1.getGreen() + c1.getBlue();
        int av2 = c2.getRed() + c2.getGreen() + c2.getBlue();
        return Math.abs(av1 - av2) <= e ;
    }

    public boolean isValid(int i ,int j)
    {
        return 0 <= i && i < getSize().width && 0 <= j && j < getSize().height;
    }

    public void BFS(Point startP) {
        BufferedImage image = copyImage(imageHistory.lastElement());
        int w = getSize().width ;
        int h = getSize().height ;
        boolean [][]visited = new boolean[w][h];
        for(int i = 0 ; i < w ; i++){
            for(int j = 0 ; j < h ; j++){
                visited[i][j] = false;
            }
        }

        Color startC = new Color(image.getRGB(startP.x, startP.y) ,true);
        visited[startP.x][startP.y] = true;

        Queue< Point > q = new LinkedList<>();
        q.add(startP);
        while( !q.isEmpty() )
        {
            Point fatherP = q.poll();
            Color fatherC = new Color(image.getRGB(fatherP.x, fatherP.y) ,true);

            for(int i = 0 ; i < 8 ; i++)
            {
                Point sonP = new Point(fatherP.x + dx[i] ,fatherP.y + dy[i]);

                if( isValid(sonP.x ,sonP.y) && !visited[sonP.x][sonP.y] )
                {
                    Color sonC = new Color(image.getRGB(sonP.x, sonP.y) ,true);

                    if( graySame(startC,sonC) && !isBlack(sonC) )
                    {
                        visited[sonP.x][sonP.y] = true;
                        q.add(sonP);
                    }
                }
            }
            image.setRGB(fatherP.x ,fatherP.y ,mulColor(color,fatherC).getRGB());
        }
        imageHistory.add(image);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(image, 0, 0, null);
        repaint();
    }

}