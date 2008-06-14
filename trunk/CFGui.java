import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import cfgen.*;
import cfgui.*;

//SCALE DISTANCES (MAYBE)
//DRAG NOTES

public class CFGui extends JApplet {
    
    StaffPanel cfPanel;
    StaffPanel solutionPanel;
    ButtonPanel buttons;
    MessagePanel messages;
    
    static int X = 1000,
        Y = 600;
    
    public void init() {    
        getContentPane().setLayout(null);
        
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(2,1));
        
        JPanel pane2 = new JPanel();
        pane2.setLayout(new GridLayout(1,1));
        
        JFrame fMessages = new JFrame("Messages");
        fMessages.getContentPane().setLayout(new GridLayout(1,1));
        
        cfPanel = new StaffPanel(0);
        solutionPanel = new StaffPanel(1);
        messages = new MessagePanel(cfPanel, solutionPanel);
        buttons = new ButtonPanel(cfPanel, solutionPanel, messages);
        
        cfPanel.setButtonPanel(buttons);
        solutionPanel.setButtonPanel(buttons);
        messages.setButtonPanel(buttons);
        
        pane.add(solutionPanel);
        pane.add(cfPanel);
        pane2.add(buttons);
        
        pane.setBounds(0, 0, 3 * X / 4, Y);
        pane2.setBounds(3 * X / 4, 0, X / 4, Y);
        //buttons.setBounds(3 * X / 4, 0, X / 4, Y/2);
        //messages.setBounds(3 * X / 4, Y/2, X / 4, Y/2);
        
        fMessages.getContentPane().add(messages, BorderLayout.CENTER);
        fMessages.setLocation(0, 600);
        fMessages.setResizable(false);
        fMessages.pack();
        fMessages.setSize(new Dimension( 1000, 140));
        fMessages.setVisible(true);
        
        getContentPane().add(pane);
        getContentPane().add(pane2);
        
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("CFGen!");
        f.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {System.exit(0);}
            });
        JApplet gui = new CFGui();
        f.getContentPane().add(gui, BorderLayout.CENTER);
        gui.init();
        f.setResizable(false);
        f.pack();
        f.setSize(new Dimension(X, Y));
        f.setVisible(true);    
    }
    
}