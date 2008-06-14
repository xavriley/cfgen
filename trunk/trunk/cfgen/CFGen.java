package cfgen;

import StateSpace.*;
import java.util.*;

public class CFGen {
    
    public static void main(String[] args) {
        
        // reading CF from args
        final CantusFirmus cf = new CantusFirmus((args.length > 0) ? args[0] : "c0 d0 e0 f0 g0 a1 g0 e0 d0 c0");
        
        Callback<String> print = new Callback<String>() {
            public void run(String s) { System.out.println(s); }
        };

        compose(cf, print);
    }
    
    public static Collection<String> compose(final CantusFirmus cf) {
        final Collection<String> solutions = new LinkedList<String>();
        Callback<String> save = new Callback<String>() {
            public void run(String s) { solutions.add(s); }
        };
        compose(cf, save);
        return solutions;
    }

    public static void compose(final CantusFirmus cf, Callback<String> handler) {
        //System.out.println("Cantus Firmus: " + cf);
        
        // creating initial state
        int length = cf.notes.length;
        State init = new State();
        init.data = new Pitch[length];

        SuccessCheck sc = new SuccessCheck() {
                public boolean check(State s) {
                    for(int i = 0 ; i < s.data.length ; i++) {
                        if (s.data[i] == null) return false;
                    }
                    FirstSpecies fs = new FirstSpecies((Pitch[]) s.data);
                    fs.addCantusFirmus(cf);
                    return fs.check().size() == 0;
                }
            };
        
        MoveGenerator mg = new MoveGenerator() {
                public Operator[] moves(State s) {
                    LinkedList<Operator> moves = new LinkedList<Operator>();
                    
                    int k; //determine where the new note is to be placed
                    for (k = 0 ; k < s.data.length ; k++)
                        if (s.data[k] == null) break;
                    if (k == s.data.length) return new Operator[0];
                    final int place = k;
                    
                    Interval in1, in2, in3, in4;
                    select:
                    for(int i = 0 ; i <= 9 ; i = (i <= 0) ? -i + 1 : -i) {
                        final Pitch newPitch = new Pitch((place == 0) ?
                                                         cf.notes[0].getValue() + 12 + i :
                                                         ((Pitch) s.data[place-1]).getValue() + i);
                        newPitch.rename(cf.k);
                        if (!cf.k.contains(newPitch)) continue;
                        
                        // "Notes in the melody must be above those in the bass."
                        if (newPitch.compareTo(cf.notes[place]) < 0) continue select;
                        
                        in1 = new Interval(newPitch, cf.notes[place]);
                        
                        // "First interval must be a unison, octave, or perfect 5th with CF in base."
                        if (place == 0)
                            if (in1.quality != 0 ||
                                (in1.name != 1 &&
                                 in1.name != 8 &&
                                 (in1.name != 5 || cf.notes[0].compareTo(cf.notes[0]) < 0)))
                                continue select;
                        
                        // "Last interval must be an octave"
                        if (place + 1  == cf.notes.length)
                            if (in1.quality != 0 ||
                                (in1.name != 1 &&
                                 in1.name != 8))
                                continue select;
                        
                        // "Vertical intervals must be consonant"
                        if (!in1.isConsonant() ||
                            (in1.isPerfect() && in1.quality != 0) ||
                            ((in1.name == 3 || in1.name == 6) && (in1.quality == -2 || in1.quality == 1)))
                            continue select;
                        
                        // no huge ranges
                        Pitch high = newPitch;
                        Pitch low = newPitch;
                        for(int j = 0 ; j < place ; j++) {
                            if (((Pitch) s.data[j]).compareTo(high) > 0) high = (Pitch) s.data[j];
                            if (((Pitch) s.data[j]).compareTo(low) < 0) low = (Pitch) s.data[j];
                        }
                        int range = high.compareTo(low);
                        if (range > 16) continue select;
                        
                        if (place > 0) {
                            // check interval with previous note
                            in2 = new Interval((Pitch) s.data[place - 1], newPitch);
                            
                            // no leaps of tritones
                            if (in2.getValue() == 6) continue select;
                            
                            // no ties in 1st species
                            if (in2.getValue() == 0) continue select;
                            
                            // no hidden perfects
                            if (in1.isPerfect()) {
                                int i1 = newPitch.compareTo((Pitch) s.data[place-1]);
                                int i2 = cf.notes[place].compareTo(cf.notes[place-1]);
                                if (i1*i2 > 0)
                                    continue select;
                            }
                            
                            // check previous vertical interval
                            in2 = new Interval((Pitch) s.data[place - 1], cf.notes[place - 1]);
                            
                            // "No parallel perfect 5ths or 8ths"
                            if ((in1.name == 1 || in1.name == 5 || in1.name == 8) &&
                                (in1.quality == 0) &&
                                (in1.name == in2.name && in1.quality == in2.quality))
                                continue select;
                            
                            if (place > 2) {
                                // check previous vertical intervals - "only three in a row of any given interval"
                                in3 = new Interval((Pitch) s.data[place - 2], cf.notes[place - 2]);
                                in4 = new Interval((Pitch) s.data[place - 3], cf.notes[place - 3]);
                                if (in1.name == in2.name &&
                                    in1.name == in3.name &&
                                    in1.name == in4.name)
                                    continue select;
                            }
                            
                        }
                        
                        moves.addFirst(new Operator() {
                                public State apply(State old) {
                                    State ns = new State();
                                    ns.data = new Pitch[old.data.length];
                                    for(int j = 0 ; j < place ; j++)
                                        ns.data[j] = old.data[j];
                                    ns.data[place] = newPitch;
                                    return ns;
                                }
                            });
                        
                    }
                    
                    Operator[] result = new Operator[moves.size()];
                    for(int j = 0 ; j < result.length ; j++)
                        result[j] = (Operator) (moves.removeFirst());
                    return result;
                }
            };
        
        ThreadedSolver s = new ThreadedSolver();
        Thread t = new Thread(s);
        t.start();
        s.solveLater(init, sc, mg, false, handler);
    }
    
}
