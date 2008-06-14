package cfgui;

import java.awt.Color;

class MyNote {
    int x;
    int y;
    
    int pitch;
    char pitchC;
    int octave;
    
    Color color = Color.black;
    
    boolean highlighted = false;
    
    public MyNote(int newX, int newY) {
        x = newX;
        y = newY;	
    }
    
    public String toString() {
        return(pitchName() + ", " + octave);
    }
    
    public void setColor(Color c) {
        if(c == Color.black) {
            color = c;
            return;
        }
        if(color == Color.red) 
            return;
        if(c == Color.red) {
            color = c;
            return;
        }
        if(c == Color.green && color == Color.orange)
            return;
        
        color = c;
        
    }
    
    public char pitchName() {
        switch(pitch) {
        case 0: return 'a';
        case 1: return 'b';
        case 2: return 'c';
        case 3: return 'd';
        case 4: return 'e';
        case 5: return 'f';
        case 6: return 'g';
        }
        return 'c';
    }
    
}