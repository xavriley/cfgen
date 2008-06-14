package cfgen;

public class Note {
    
    Pitch p;
    int d; // duration: 32 = 1 beat
    
    public Note() {
        this(new Pitch(), 128);  // C, 4 beats
    }
    public Note(Pitch newP) {
        this(newP, 128);
    }
    public Note(int newD) {
        this(new Pitch(), newD);
    }
    public Note(Pitch newP, int newD) {
        p = newP;
        d = newD;
    }
    
}
