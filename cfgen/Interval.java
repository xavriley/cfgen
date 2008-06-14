package cfgen;

public class Interval {

    // how many half steps a major jump of this degree is
    static final int[] normal = new int[] { 0, 2, 4, 5, 7, 9, 11, 12 };

    public int name;  // unison, 2nd, etc.
    public int quality;  // what type of interval (major, minor, etc)
    public int plusOctaves;  // extra octaves
    Pitch bottom, top;

    public Interval(Pitch a, Pitch b) {
        if (a.compareTo(b) < 0) {
            top = b; bottom = a;
        } else {
            top = a; bottom = b;
        }
        int steps = (top.getValue() - bottom.getValue()) % 12;
        name = (top.octave * 7 + top.name) - (bottom.octave * 7 + bottom.name) + 1;
        quality = steps - normal[(name - 1) % 7];
        
        plusOctaves = 0;
        while (name > 8) {
            name -= 7;
            plusOctaves++;
        }
    }
    
    public int getValue() {
        return top.getValue() - bottom.getValue();
    }
    
    public boolean isConsonant() {
        return (name == 1 || name == 3 || name == 5 || name == 6 || name == 8);
    }
    
    public boolean isPerfect() {
        return (name == 1 || name == 4 || name == 5 || name == 8);
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        
        switch (quality) {
        case -2:
            if (isPerfect())
                buf.append("doubly-diminished");
            else
                buf.append("diminished");
            break;
        case -1:
            if (isPerfect())
                buf.append("diminished");
            else
                buf.append("minor");
            break;
        case 0:
            if (isPerfect())
                buf.append("perfect");
            else
                buf.append("major");
            break;
        case 1:
            buf.append("augmented");
            break;
        case 2:
            buf.append("doubly-augmented");
            break;
        }
        
        buf.append(" ");
        
        switch (name) {
        case 1:
            buf.append("unison");
            break;
        case 2:
            buf.append("2nd");
            break;
        case 3:
            buf.append("3rd");
            break;
        case 8:
            buf.append("octave");
            break;
        default:
            buf.append(name);
            buf.append("th");
            break;
        }
        
        return buf.toString();
    }
    
}
