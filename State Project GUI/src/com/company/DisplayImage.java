package com.company;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class DisplayImage {
    JTabbedPane tabbedPane;
    JFrame editorFrame;
    int ntabs = 0;

    public DisplayImage() throws Exception {
        editorFrame = new JFrame("Resultat");
        tabbedPane = new JTabbedPane();
    }

    public void Display(String filename){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                editorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(new File(filename));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.exit(1);
                }
                ImageIcon imageIcon = new ImageIcon(image);
                createTab(imageIcon);
                editorFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
                editorFrame.pack();
                editorFrame.setLocationRelativeTo(null);
                editorFrame.setVisible(true);
            }
        });
    }
    protected void createTab(ImageIcon imagefile) {
        if(ntabs == 0) {
            tabbedPane.addTab("NFA", null, new JLabel(imagefile));
        } else if(ntabs == 1) {
            tabbedPane.addTab("DFA", null, new JLabel(imagefile));
        } else if(ntabs == 2) {
            tabbedPane.addTab("DFA minimiser", null, new JLabel(imagefile));
        }
        tabbedPane.setBackgroundAt(ntabs, Color.white);
        ntabs++;
    }
}
