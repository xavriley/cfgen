package cfgui;

import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.RenderingHints;

import cfgen.Pitch;
import cfgen.Interval;

import java.util.*;

public class StaffPanel extends JPanel implements MouseListener { //, ActionListener{ //, MouseMotionListener {
    
    BufferedImage bi;
    Graphics2D big;
    int numPoints = 16;
    int x, y;
    //MyNote lastNote;
    LinkedList<MyNote> notes = new LinkedList<MyNote>();
    int linesAndSpaces[] = new int[30];
    int clef; //0 = bass, 1 = treble
    Rectangle area;
    boolean firstTime = true;
    ImageIcon clefPic;
    
    ButtonPanel buttons;
    
    private final int A = 0;
    private final int B = 1;
    private final int C = 2;
    private final int D = 3;
    private final int E = 4;
    private final int F = 5;
    private final int G = 6;
    
    private final int maxLength = 12;
    private final int startX = 160;
    
    public StaffPanel(int newClef) {
        setBackground(Color.white);
        //addMouseMotionListener(this);
        addMouseListener(this);
        clef = newClef;
        if( clef == 0 ) {
            clefPic = new ImageIcon("images/bass_clef.png");
        }
        if( clef == 1 ) {
            clefPic = new ImageIcon("images/treble_clef.png");
        }
    }
    
    public void setButtonPanel(ButtonPanel butts) {
        buttons = butts;
    }
    
    public void addNote(int x, int y) {
        if(notes.size() == 0) {
            x = startX;
            notes.add(new MyNote(x, y));
            //	    System.out.println("First note added: " + x + " " + y);
        }
        else if (x > notes.getLast().x && notes.size() < maxLength) {
            x = notes.getLast().x + 40;
            notes.add(new MyNote(x, y));
            //	    System.out.println("Last note added: " + x + " " + y);	    
        }
        else if (x >= notes.getFirst().x) {
            
            ListIterator<MyNote> itr = notes.listIterator();
            while(itr.hasNext()) {
                MyNote note = itr.next();
                //System.out.println(note.x);
                if (x == note.x) {
                    if(y == note.y) {
                        //  	System.out.println("Removing note...");
                        itr.remove();
                        adjustNotes(itr.nextIndex(), -1);
                    }
                    else {
                        note.y = y;
                        //  	System.out.println("Note changed: " + x + " " + y);
                    }
                    break;
                }
                if (x == (note.x + 20) && notes.size() <= maxLength - 1) {
                    notes.add(itr.nextIndex(), new MyNote(x + 20, y));
                    adjustNotes(itr.nextIndex() + 1, 1);
                    //      System.out.println("Note inserted: " + x + " " + y);
                    break;
                }
            }
        }
        else if (x < notes.getFirst().x && notes.size() <= maxLength - 1) {
            notes.add(0, new MyNote(startX, y));
            adjustNotes(1, 1);
            //	    System.out.println("First Note Inserted: " + x + " " + y);
        }
        
        if(clef == 0 && buttons.solutions != null)
            buttons.solutions = null;
    }
    
    public int noteNumber(char noteName) {
        switch(noteName) {
        case 'A': return 0;
        case 'B': return 1;
        case 'C': return 2;
        case 'D': return 3;
        case 'E': return 4;
        case 'F': return 5;
        case 'G': return 6;
        }
        return 2;
    }
    
    public void displayString(String noteString) {
        notes.clear();
        repaint();
        
        String[] splitString = noteString.split(" ");
        
        Pitch bottom = null;
        if(clef == 1)
            bottom = new Pitch("B2");
        if(clef == 0)
            bottom = new Pitch("D0");
        
        //	for(int i = 0 ; i < linesAndSpaces.length ; i++) {
        //	    System.out.print(" " + linesAndSpaces[i]);
        //	}
        
        int X = startX - 40;
        for(int i = 0; i < splitString.length; i++) {
            //	    System.out.print(splitString[i] + "\t");
            X += 40;
            Pitch p = new Pitch(splitString[i]);
            //	    System.out.println(p + " " + p.name + " " + p.acc + " " + p.octave);
            
            if (p.compareTo(bottom) < 0) System.out.println("Note too low to display");
            Interval in = new Interval(bottom, p);
            //	    System.out.print(in.name + " plus " + in.plusOctaves + " octaves.");
            int Y = linesAndSpaces[linesAndSpaces.length - (in.name + 7 * in.plusOctaves)];
            //	    System.out.println(" -> " + Y);
            addNote(X, Y);
        }
        //	System.out.println();
    }
    
    public void setPitches() {
        int middle = linesAndSpaces.length / 2;
        int[] pitches = new int[linesAndSpaces.length];
        
        if(clef == 1)
            pitches[pitches.length/2] = B;
        if(clef == 0)
            pitches[pitches.length/2] = D;
        
        for(int i = (pitches.length / 2) - 1; i >= 0; i--)
            pitches[i] = Math.abs(pitches[i + 1] + 1) % 7;
        for(int i = (pitches.length / 2) + 1; i < pitches.length; i++)
            pitches[i] = pitches[i - pitches.length / 2 + 1];
        
        for(MyNote note : notes) {
            for(int i = 0; i < linesAndSpaces.length; i++) {
                if(note.y == linesAndSpaces[i]) {
                    note.pitch = pitches[i];
                    if ( clef == 1) {
                        int diff = 37 - i;
                        int octave = diff / 7 + 1;
                        note.octave = octave;
                        //System.out.println(octave);
                    }
                    if ( clef == 0) {
                        int diff = 32 - i;
                        int octave = diff / 7 ;
                        note.octave = octave;
                        //System.out.println(octave);
                    }
                    break;
                }
            }
        }
    }
    
    
    public void adjustNotes(int index, int changeMult) {
        for(MyNote note : notes) {
            if (index-- > 0) continue;
            note.x += 40 * changeMult;
        }
    }
    
    public void mousePressed(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        
        for(int i = 0; i < linesAndSpaces.length; i++) {
            if(Math.abs(y - linesAndSpaces[i]) <= 5) {
                y = linesAndSpaces[i];
                //System.out.println(i);
                
                if((x % 40) < 10)
                    x = x - (x % 40);
                else if((x % 40) >= 30)
                    x = x + (40 - (x % 40));
                else
                    x = x - (x % 40) + 20;
                
                
                //System.out.println(y);
                
                addNote(x, y);
                
                break;
            }
        }
        
        repaint();
    }
    
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        update(g);
    }
    
    public void update(Graphics g) {
    
        Graphics2D g2 = (Graphics2D) g;
        Dimension dim = getSize();
        int w = dim.width;
        int h = dim.height;
        
        if(firstTime) {
            bi = (BufferedImage)createImage(w, h);
            big = bi.createGraphics();
            big.setColor(Color.black);
            big.setStroke(new BasicStroke(3.0f));
            big.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            area = new Rectangle(dim);
            //System.out.println("This area is " + w + " by " + h );
            
            for(int i = 0; i < linesAndSpaces.length; i++) {
                linesAndSpaces[i] = h/2 + ((i - linesAndSpaces.length / 2) * 10);
                //System.out.println(linesAndSpaces[i]);
            }
            
            firstTime = false;
        }
        
        big.setColor(Color.white);
        big.setPaint(Color.white);
        big.fillRect(0, 0, w, h);
        
        big.setColor(Color.black);
        big.setPaint(Color.black);
        if( clef == 0) clefPic.paintIcon(this, big, 10, h/2 - 42);
        if( clef == 1) clefPic.paintIcon(this, big, 0, h/2 - 55);
        big.drawLine(0, h/2 - 40, w, h/2 - 40);
        big.drawLine(0, h/2 - 20, w, h/2 - 20);
        big.drawLine(0, h/2 , w, h/2);
        big.drawLine(0, h/2 + 20, w, h/2 + 20);
        big.drawLine(0, h/2 + 40, w, h/2 + 40);
        
        for(MyNote note : notes) {
            int top = linesAndSpaces[linesAndSpaces.length / 2 - 4];
            int bottom = linesAndSpaces[linesAndSpaces.length /2 + 4];
            if( note.y < top ) {
                for (int i = top; i >= note.y; i -= 20)
                    big.drawLine(note.x - 15, i, note.x + 15, i);
            }
            if( note.y > bottom ) {
                for (int i = bottom; i <= note.y; i += 20)
                    big.drawLine(note.x - 15, i, note.x + 15, i);
            }
            big.setPaint(note.color);
            big.drawOval(note.x - 10, note.y - 10, 20, 20);
            big.setPaint(Color.blue);
            if(note.highlighted) {
                big.drawLine( note.x - 5, note.y - 5, note.x + 5, note.y + 5);
                big.drawLine( note.x - 5, note.y + 5, note.x + 5, note.y - 5);
            }
            
            big.setPaint(Color.black);
        }	
        
        g2.drawImage(bi, 0, 0, this);
        
    }
}