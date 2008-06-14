package cfgen;

public class Pitch implements Comparable {

    // map from letter names to value adjustments (relative to 'c')
    private static int[] values = new int[] { -3 , -1 , 0,  2, 4, 5, 7 };

    public char name;
    public int acc;
    public int octave;

    public Pitch() {
        this('c');  // use C by default
    }
    public Pitch(char newName) {
        this(newName, 0);  // not sharp or flat by default
    }

    public Pitch(char newName, int newAcc) {
        this(newName, newAcc, 1);  // octave 1 by default
    }

    public Pitch(char newName, int newAcc, int newOctave) {
        name = newName;
        acc = newAcc;
        octave = newOctave;
    }

    public Pitch(int value) {
        this(value, new Key());
    }
    public Pitch(int value, Key k) {
        this('c', 0, 1);
        while (value >= 12) {
            octave++; value -= 12;
        }
        while (value <= -12) {
            octave--; value += 12;
        }
        while (value > 0) {
            raise(); value--;
        }
        while (value < 0) {
            lower();
            value++;
        }
        rename(k);
    }

    public Pitch(String s) {
        s = s.toLowerCase();

        name = s.charAt(0);
        s = s.substring(1);

        if      (s.startsWith("b"))  acc = -1;
        else if (s.startsWith("bb")) acc = -2;
        else if (s.startsWith("#"))  acc = 1;
        else if (s.startsWith("##")) acc = 2;
        else                         acc = 0;
        s = s.substring(Math.abs(acc));

        octave = Integer.parseInt(s);
    }
    
    public void raise() {
        acc++;
    }
    public void lower() {
        acc--;
    }
    
    // rename this pitch, discarding as many accidentals as possible
    public void rename() {
        rename(new Key());  // default to empty key signature
    }
    public void rename(Key k) {
        while(acc > 0) {
            if (name == 'e' || name == 'b') {  // e# == f, b# == c
                name++; acc--;
                continue;
            }
            if (acc > k.getAccidentals(name)) {  // if more sharps than the key calls for
                name++; acc -= 2;
                if (name > 'g') {  // if off the top of the scale
                    name -= 7;
                    octave++;
                }
            } else break;
        }
        while(acc < 0) {
            if (name == 'f' || name == 'c') {  // fb == e, cb == b
                name--; acc++;
                continue;
            }
            if (acc < k.getAccidentals(name)) {  // if more flats than the key calls for
                name--; acc += 2;
                if (name < 'a') {  // if off the bottom of the scale
                    name += 7;
                    octave--;
                }
            } else break;
        }
    }
    
    public int getValue() {
        return (octave - 1) * 12 + values[name - 'a'] + acc;
    }
    
    public int compareTo(Object other) {
        return getValue() - ((Pitch) other).getValue();
    }
    
    public boolean equals(Object other) {
        return compareTo(other) == 0;
    }
    
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(Character.toUpperCase(name));
        switch(acc) {
        case -2:
            buf.append("bb");
            break;
        case -1:
            buf.append("b");
            break;
        case 1:
            buf.append("#");
            break;
        case 2:
            buf.append("##");
            break;
        }
        buf.append(octave);
        return buf.toString();
    }
}
