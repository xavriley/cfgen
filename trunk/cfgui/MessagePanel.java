package cfgui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.Color;

import cfgen.Comment;

import java.util.*;

public class MessagePanel extends JPanel { //implements ActionListener {
    
    JLabel[] messages = null;
    JList messageList = null;
    
    StaffPanel solPanel;
    StaffPanel cfPanel;
    ButtonPanel buttonPanel;
    
    public MessagePanel(StaffPanel cf, StaffPanel sol) {
        solPanel = sol;
        cfPanel = cf;
        
        setBackground(Color.white);
        setLayout(null);
    }
    
    public void setButtonPanel(ButtonPanel BP) {
        buttonPanel = BP;
    }
    
    public void setMessages(Collection comments) {
        Comment[] com = new Comment[comments.size()];
        Iterator itr = comments.iterator();
        int i = 0;
        while (itr.hasNext() ) {
            com[i++] = (Comment) itr.next();
        }
        messageList = new JList(com);
        messageList.setLayoutOrientation(JList.VERTICAL_WRAP);
        messageList.setBounds(0, 0, 1000, 110);
        messageList.addListSelectionListener(new LSL(messageList, solPanel, cfPanel, buttonPanel));
        //JScrollPane sp = new JScrollPane(messageList);
        //add(sp);
        add(messageList);
        repaint();
    }
    
}