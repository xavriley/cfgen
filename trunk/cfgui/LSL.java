package cfgui;

import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JList;

import cfgen.Comment;

public class LSL implements ListSelectionListener { 
    JList list;
    StaffPanel solPanel;
    StaffPanel cfPanel;
    ButtonPanel buttonPanel;
    
    public LSL(JList theList, StaffPanel SP, StaffPanel CFP, ButtonPanel BP) {
        list = theList;
        solPanel = SP;
        cfPanel = CFP;
        buttonPanel = BP;
    }
    
    public void valueChanged(ListSelectionEvent e) {
        Comment c = (Comment) list.getSelectedValue();
        //System.out.println(c.notes.length);
        buttonPanel.highlightNotes(c);
        
        //System.out.println(c);
    }
}