package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class App {

    JButton chooseImageButton , chooseColorButton , unDoButton , clearButton , saveButton ;
    Painter painter;
    JFrame frame;
    JPanel panel;
    Container container;
    BufferedImage image;
    ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == chooseImageButton) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/Images(Input)"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
                fileChooser.addChoosableFileFilter(filter);
                int result = fileChooser.showOpenDialog(null);
                if(result == JFileChooser.APPROVE_OPTION)
                {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        image = ImageIO.read(selectedFile);
                        painter.setImage(image);

                    } catch (IOException ex) {
                        System.out.println("Error in Reading The Image .");
                    }
                }
            }
            else if (e.getSource() == chooseColorButton) {
                Color color= JColorChooser.showDialog(painter,"Choose a color",painter.getColor());
                painter.setColor(color);
            }
            else if (e.getSource() == clearButton) {
                painter.clear();
            }
            else if (e.getSource() == saveButton) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")+"/Images(Output)"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
                fileChooser.addChoosableFileFilter(filter);
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File outputFile = fileChooser.getSelectedFile();
                    String fileName = outputFile.getName();
                    if (!fileName.toLowerCase().endsWith(".png")) {
                        outputFile = new File(outputFile.getParent(), fileName + ".png");
                    }
                    try {
                        ImageIO.write((RenderedImage) painter.getCurrentImage(), "png", outputFile);
                    } catch (IOException ex) {
                        System.out.println("Error in Saving The Image .");
                    }
                }

            }
            else if (e.getSource() == unDoButton) {
                painter.unDo();
            }
        }
    };

    App(){
        frame = new JFrame("Swing Paint");
        panel = new JPanel();
        painter = new Painter();

        container = frame.getContentPane();

        chooseImageButton = new JButton("Choose Image");
        chooseColorButton = new JButton("Choose Color");
        unDoButton = new JButton("unDo");
        clearButton = new JButton("Clear");
        saveButton = new JButton("Save");
    }

    public void run() {

        chooseImageButton.addActionListener(actionListener);
        chooseColorButton.addActionListener(actionListener);
        unDoButton.addActionListener(actionListener);
        clearButton.addActionListener(actionListener);
        saveButton.addActionListener(actionListener);

        panel.add(chooseImageButton);
        panel.add(chooseColorButton);
        panel.add(unDoButton);
        panel.add(clearButton);
        panel.add(saveButton);


        container.setLayout(new BorderLayout());
        container.add(painter, BorderLayout.CENTER);
        container.add(panel, BorderLayout.NORTH);

        frame.setSize(800, 650);
        frame.setLocation(450, 30);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
