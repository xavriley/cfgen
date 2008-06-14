package cfgen;

import java.util.*;

public class Key {

    public static enum Quality { MAJOR, MINOR };

    // flats are added to key signatures in this order
    static final char[] flats = new char[] { 'b', 'e', 'a', 'd', 'g', 'c', 'f' };

    // sharps are added to key signatures in this order
    static final char[] sharps = new char[] { 'f', 'c', 'g', 'd', 'a', 'e', 'b' };

    // somehow related to the circle of fifths, I bet
    // maps note value to number of sharps in that key
    static final int[] circle = new int[] { 0, -5, 2, -3, 4, -1, -6, 1, -4, 3, -2, 5 };

    Pitch tonic;  // tonic pitch of this key
    Quality quality;  // major or minor
    int[] accidentals = new int[7]; // actual sharp/flat value of each note in this key
    int numSharps;  // number of sharps in key signature (negative if counting flats)
    
    public Key() {
        this(new Pitch());  // we get key of c major by default?
    }
    public Key(Pitch p) {
        this(p, Quality.MAJOR);  // major by default
    }
    public Key(Pitch p, Quality q) {
        tonic = p;
        quality = q;
        
        int signature = tonic.getValue();
        if (quality == Quality.MINOR) {
            // relative minor has the same key signature as a major key 3 half-steps up (minor third)
            signature += 3;
        }
        signature = ((signature % 12) + 12) % 12;  // mod into 0-11 range
        numSharps = circle[signature];

        if (numSharps < 0 && tonic.acc > 0)
            numSharps += 12;  // if the given tonic is *-sharp, use the equivalent sharp-using signature 
        if (numSharps > 0 && tonic.acc < 0)
            numSharps -= 12;  // vice versa for flats

        // actually add sharps/flats to key signature
        for(int i = 0; i < numSharps ; i++)
            accidentals[sharps[i%7] - 'a']++;
        for(int i = 0; i < -numSharps ; i++)
            accidentals[flats[i%7] - 'a']--;
    }

    public boolean isMajor() {
        return quality == Quality.MAJOR;
    }
    public boolean isMinor() {
        return quality == Quality.MINOR;
    }
    
    // given a note name, what degree is it in this key? (1st-7th)
    public int getDegree(char p) {
        return (p - tonic.name + 7) % 7 + 1;
    }

    // is the given pitch in this key?
    public boolean contains(Pitch p) {
        return p.equals(getPitch(p.name, p.octave));
    }

    // given a note name, return an actual Pitch from this key
    public Pitch getPitch(char p) {
        return getPitch(p, 1);  // default to octave 1
    }
    // ... also specify a target octave
    public Pitch getPitch(char p, int octave) {
        // accomodate an irrational caller who gives note names outside 'a'-'g' range
        // irrational caller ends up being us (see getScale)
        while(p > 'g') {
            p-=7;
            octave++;
        }
        while(p < 'a') {
            p+=7;
            octave--;
        }
        return new Pitch(p, getAccidentals(p), octave);
    }
    
    // given a note name, what accidental does the key signature specify for it?
    public int getAccidentals(char p) {
        return accidentals[p - 'a'];
    }
    
    public String toString() {
        return "Key of " + tonic + (quality == Quality.MAJOR ? " major" : " minor");
    }
    
    // give us a scale in this key
    public List<Pitch> getScale() {
        List<Pitch> scale = new ArrayList<Pitch>(8);
        for(int i = 0 ; i < 8 ; i++) {
            scale.add(getPitch((char) (tonic.name + i)));  // irrational caller!
        }
        return scale;
    }
    
    // TODO(mrotondo): lets try to get some actual shit goin on.
    
}
